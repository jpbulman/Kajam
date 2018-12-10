package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;

import db.MeetingDAO;
import db.TimeSlotDAO;
import model.Meeting;
import model.TimeSlot;

public class ChangeTimeSlotAvailabilityByDayOfWeek {
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
		
		ChangeTimeslotAvailabilityByDayOfWeekResponse response;
		
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
			response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(UUID.randomUUID(), DayOfWeek.MONDAY, false, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			ChangeTimeslotAvailabilityByDayOfWeekRequest req = new Gson().fromJson(body, ChangeTimeslotAvailabilityByDayOfWeekRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			
			UUID id;
			try {
				id = UUID.fromString(req.arg1);
			} catch(Exception e) {
				id = null;
				respError += "Invalid ID ";
			}
			
			int d = 0;
			try {
				d = Integer.parseInt(req.arg2);
			}catch(Exception e) {
				respError += "Invalid number format ";
			}
			
			DayOfWeek day = null;
			if(d == 1) {
				day = DayOfWeek.MONDAY;
			}else if(d == 2) {
				day = DayOfWeek.TUESDAY;
			}else if(d == 3) {
				day = DayOfWeek.WEDNESDAY;
			}else if(d == 4) {
				day = DayOfWeek.THURSDAY;
			}else if(d == 5) {
				day = DayOfWeek.FRIDAY;
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
				TimeSlotDAO daoTS = new TimeSlotDAO();
				if(req.arg7.compareTo("available") == 0) {
					s.isFree = true;
					try {
						daoTS.updateTimeSlot(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
					response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(id, date, startTime, true, 200);
					responseJson.put("body", new Gson().toJson(response));
				}else {
					s.isFree = false;
					try {
						daoTS.updateTimeSlot(s);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(id, date, startTime, false, 200);
					responseJson.put("body", new Gson().toJson(response));
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
