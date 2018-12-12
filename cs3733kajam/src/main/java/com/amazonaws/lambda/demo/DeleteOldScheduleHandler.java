package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;

import db.MeetingDAO;
import db.ScheduleDAO;
import db.TimeSlotDAO;
import model.Meeting;
import model.Schedule;
import model.TimeSlot;

public class DeleteOldScheduleHandler implements RequestStreamHandler {
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();
	
	
	
	private ArrayList<Schedule> deleteSchedules(int days) {
		if (logger != null) { logger.log("in deleteOldSchedule"); }
		ScheduleDAO dao = new ScheduleDAO();

		// check if present
		ArrayList<Schedule> underN = new ArrayList<Schedule>();
		ArrayList<Schedule> schedules = dao.getAllSchedules();
		if (schedules.size() == 0) {
			return underN;
		}
		//logger.log(ts.toString());
		for (Schedule s : schedules) {
			LocalDateTime ts = LocalDateTime.now();
			ts = ts.minusDays(days);
			try {
				if (LocalDateTime.parse(s.timestamp).isBefore(ts)) {
					underN.add(s);
					deleteSchedule(s);
					if (logger != null) { logger.log("DELETED schedule: "+ s.name); }
				}
			}catch (Exception e) {
				if (logger != null) { logger.log("old format"); }
			}
		}
		return underN;
	}
	
	private void deleteSchedule(Schedule s) throws Exception {
		ScheduleDAO dao = new ScheduleDAO();
		dao.deleteSchedule(s);
		// Delete all time slots and meetings in schedule
		TimeSlotDAO td = new TimeSlotDAO();
		MeetingDAO md = new MeetingDAO();
		s.timeSlots = td.getAllTimeSlots(s.id);
		for(TimeSlot t: s.timeSlots) {
			td.deleteTimeSlot(t);
			Meeting m = md.getMeetingByTimeSlotID(t.id);
			if(m!=null) {
				md.deleteMeeting(m);
			}
			
		}
	}
	
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to delete old schedules");
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "DELETE,GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	        
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);

		DeleteOldScheduleResponse response = null;
		
		// extract body from incoming HTTP DELETE request. If any error, then return 422 error
		String body;
		boolean processed = false;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			JSONParser parser = new JSONParser();
			JSONObject event = (JSONObject) parser.parse(reader);
			logger.log("event:" + event.toJSONString());

			String method = (String) event.get("httpMethod");
			if (method != null && method.equalsIgnoreCase("OPTIONS")) {
				logger.log("Options request");
				ErrorResponse r = new ErrorResponse("DONE (OPTIONS)", 200);
				responseJson.put("body", new Gson().toJson(r));
		        processed = true;
		        body = null;
			}else {
				body = (String)event.get("body");
				if (body == null) {
					body = event.toJSONString();  // this is only here to make testing easier
				}
			}
		} catch (ParseException pe) {
			logger.log(pe.toString());
			//TODO: add more parameters
			response = new DeleteOldScheduleResponse(null, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			DeleteOldScheduleRequest req = new Gson().fromJson(body, DeleteOldScheduleRequest.class);
			logger.log(req.toString());
			
			int val1 = 0;
			String respError = "";
			ArrayList<Schedule> sArr = null;
			try {
				val1 = Integer.parseInt(req.arg1);
			}catch(Exception e) {
				e.printStackTrace();
				respError = "Invalid input";
			}
			
			if(respError.compareTo("") == 0) {
				try {
					sArr = deleteSchedules(val1);
				}catch(Exception e) {
					respError += "Error while deleting schedules";
				}
			}
			
			// compute proper response
			if(respError.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(resp));
			}else {
				DeleteOldScheduleResponse resp = new DeleteOldScheduleResponse(sArr, 200);
				responseJson.put("body", new Gson().toJson(resp));
			}
		}
		
        logger.log("end result:" + responseJson.toJSONString());
        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(responseJson.toJSONString());  
        writer.close();

	}

}
