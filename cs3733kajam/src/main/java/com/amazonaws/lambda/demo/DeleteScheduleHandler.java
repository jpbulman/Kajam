package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

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


public class DeleteScheduleHandler implements RequestStreamHandler {

	public LambdaLogger logger = null;
	
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();
	
	boolean deleteSchedule(UUID id) throws Exception {
		if (logger != null) { logger.log("in createSchedule"); }
		ScheduleDAO dao = new ScheduleDAO();
		Schedule s = dao.getSchedule(id);
		if (s == null) {
			//throw new NullPointerException();
			return false;
		} else{
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
			return true;
		}
	}
	
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to delete meeting");
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "DELETE,GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	        
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);

		DeleteScheduleResponse response = null;
		
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
			response = new DeleteScheduleResponse(null, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			DeleteScheduleRequest req = new Gson().fromJson(body, DeleteScheduleRequest.class);
			logger.log(req.toString());
			
			UUID val1 = null; // id
			String respError = "";
			
			try {
				val1 = UUID.fromString(req.arg1);
			} catch (Exception e) {
				val1 = null;
				respError = "Invalid input format";
			}
			
			if(respError.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(resp));
			}else {
				try {
					if(deleteSchedule(val1)) {
						DeleteScheduleResponse resp = new DeleteScheduleResponse(val1, 200);
						responseJson.put("body", new Gson().toJson(resp));
					}else {
						ErrorResponse resp = new ErrorResponse("Could not delete schedule", 400);
						responseJson.put("body", new Gson().toJson(resp));
					}
				} catch (Exception e) {
					ErrorResponse resp = new ErrorResponse("Could not delete schedule", 400);
					responseJson.put("body", new Gson().toJson(resp));
				}
			}

		}
		
        logger.log("end result:" + responseJson.toJSONString());
        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(responseJson.toJSONString());  
        writer.close();
		
		
		
	}
	
}
