package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

public class ChangeTimeslotAvailability implements RequestStreamHandler{
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
	
	Schedule getSchedule(UUID id) throws Exception{
		if (logger != null) { logger.log("in getSchedule"); }
		ScheduleDAO dao = new ScheduleDAO();
		
		// check if present
		Schedule exist = dao.getSchedule(id);
		if (exist == null) {
			throw new NullPointerException();
		} else {
			return exist;
		}
	}
	
	ArrayList<TimeSlot> getTimeSlotsByDate(UUID id, LocalDate date, LocalTime startTime, LocalTime endTime, int duration) throws Exception{
		if (logger != null) { logger.log("in getTimeSlotsByDate"); }
		ArrayList<TimeSlot> ts = new ArrayList<TimeSlot>();
		LocalTime currentTime = startTime;
		if (logger != null) { 
			logger.log("currentTime " + currentTime.toString()); 
			logger.log("startTime " + startTime.toString());
			logger.log("endTime " + endTime.toString());
			logger.log("duration " + duration);
			logger.log("date " + date);
		}
		while(currentTime.isBefore(endTime)) {
			TimeSlot s = getTimeSlot(id, date, currentTime);
			if(s != null) {
				ts.add(s);
			}
			
			if (logger != null) { 
				logger.log("timeslot " + s.toString()); 
			}
			currentTime = currentTime.plusMinutes(duration);
		}
		
		if (logger != null) { logger.log("at end of getTimeSlotsByDate"); }
		return ts;
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
		
		ChangeTimeslotAvailabilityResponse response;
		
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
			response = new ChangeTimeslotAvailabilityResponse(UUID.randomUUID(), LocalDate.now(), LocalTime.now(), LocalTime.now(), false, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			ChangeTimeslotAvailabilityRequest req = new Gson().fromJson(body, ChangeTimeslotAvailabilityRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			
			UUID id;
			Schedule s;
			try {
				id = UUID.fromString(req.arg1);
				s = getSchedule(id);
			} catch(Exception e) {
				id = null;
				s = null;
				respError += "Invalid ID ";
			}
			
			int year = 0;
			int month = 0;
			int day = 0;
			int starthour = 0;
			int startmin = 0;
			int endhour = 0;
			int endmin = 0;
			try {
				year = Integer.parseInt(req.arg2);
				month = Integer.parseInt(req.arg3);
				day = Integer.parseInt(req.arg4);
				starthour = Integer.parseInt(req.arg5);
				startmin = Integer.parseInt(req.arg6);
				endhour = Integer.parseInt(req.arg7);
				endmin = Integer.parseInt(req.arg8);
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
				startTime = LocalTime.of(starthour, startmin);
			}catch(Exception e) {
				respError += "Invalid startTime ";
			}
			
			LocalTime endTime = null;
			try {
				endTime = LocalTime.of(endhour, endmin);
			}catch(Exception e) {
				respError += "Invalid startTime ";
			}
			
			ArrayList<TimeSlot> ts = new ArrayList<TimeSlot>();
			try {
				ts.addAll(getTimeSlotsByDate(id, date, startTime, endTime, s.duration));
			} catch(Exception e) {
				e.printStackTrace();
				respError += "Error retrieving timeslots ";
			}
			
			// compute proper response
			if(respError.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(resp));
			}else {
				TimeSlotDAO daoTS = new TimeSlotDAO();
				if(req.arg7.compareTo("available") == 0) {
					try {
						for(int i = 0; i < ts.size(); i++) {
							TimeSlot temp = ts.get(i);
							temp.isFree = true;
							daoTS.updateTimeSlot(temp);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					response = new ChangeTimeslotAvailabilityResponse(id, date, startTime, endTime, true, 200);
					responseJson.put("body", new Gson().toJson(response));
				}else {
					try {
						for(int i = 0; i < ts.size(); i++) {
							TimeSlot temp = ts.get(i);
							temp.isFree = true;
							daoTS.updateTimeSlot(temp);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					response = new ChangeTimeslotAvailabilityResponse(id, date, startTime, endTime, false, 200);
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
