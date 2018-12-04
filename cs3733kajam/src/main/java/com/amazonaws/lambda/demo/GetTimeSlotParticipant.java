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

public class GetTimeSlotParticipant implements RequestStreamHandler{
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();

	boolean useRDS = true;
	
	TimeSlot getTimeSlot(UUID id, LocalDate date, LocalTime startTime) throws Exception{
		if (logger != null) { logger.log("in getSchedule"); }
		TimeSlotDAO dao = new TimeSlotDAO();
		
		// check if present 
		TimeSlot exist = dao.getTimeSlotByDateTime(id, date, startTime);
		if (exist == null) {
			throw new NullPointerException();
		} else {
			return exist;
		}
	}
	
	Meeting getMeeting(UUID id) throws Exception{
		if (logger != null) { logger.log("in getSchedule"); }
		MeetingDAO dao = new MeetingDAO();
		
		// check if present 
		Meeting exist = dao.getMeetingByTimeSlotID(id);
		if (exist == null) {
			throw new NullPointerException();
		} else {
			return exist;
		}
	}
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    	
    	LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestStreamHandler");

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	    
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);
		
		GetTimeslotParticipantResponse response;
		
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
			response = new GetTimeslotParticipantResponse(UUID.randomUUID(), LocalDate.now(), LocalTime.now(), false, false, "", 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			GetTimeslotRequest req = new Gson().fromJson(body, GetTimeslotRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			
			UUID id;
			try {
				id = UUID.fromString(req.arg1);
			} catch(Exception e) {
				id = null;
				respError += "Invalid ID ";
			}
			
			int year = 0;
			int month = 0;
			int day = 0;
			int hour = 0;
			int min = 0;
			try {
				year = Integer.parseInt(req.arg2);
				month = Integer.parseInt(req.arg3);
				day = Integer.parseInt(req.arg4);
				hour = Integer.parseInt(req.arg5);
				min = Integer.parseInt(req.arg6);
			}catch(Exception e) {
				respError += "Invalid number format ";
			}

			LocalDate date = null;
			try {
				date = LocalDate.of(year, month, day);
			}catch (Exception e) {
				respError += "Invalid date ";
			}
			
			LocalTime startTime = null;
			try {
				startTime = LocalTime.of(hour, min);
			}catch(Exception e) {
				respError += "Invalid startTime ";
			}
			
			TimeSlot s;
			try {
				s = getTimeSlot(id, date, startTime);
			} catch (Exception e) {
				s = null;
				respError += "No timeslot matches given parameters ";
			}
			
			// compute proper response
			if(respError.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(resp));
			}else {
				if(s.isFree == true) {
					GetTimeslotParticipantResponse resp = new GetTimeslotParticipantResponse(id, date, startTime, false, true, "NONE", 200);
					responseJson.put("body", new Gson().toJson(resp));
				}else {
					Meeting m;
					try {
						m = getMeeting(s.id);
					}catch(Exception e) {
						m = null;
					}
					if(m != null) {
						GetTimeslotParticipantResponse resp = new GetTimeslotParticipantResponse(id, date, startTime, false, false, m.name, 200);
						responseJson.put("body", new Gson().toJson(resp));
					}else {
						GetTimeslotParticipantResponse resp = new GetTimeslotParticipantResponse(id, date, startTime, true, false, "NONE", 200);
						responseJson.put("body", new Gson().toJson(resp));
					}
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
