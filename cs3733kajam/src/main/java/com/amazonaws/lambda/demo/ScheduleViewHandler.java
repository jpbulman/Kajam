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
import java.util.ArrayList;
import java.util.List;
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

import db.ScheduleDAO;
import db.TimeSlotDAO;
import model.Schedule;
import model.TimeSlot;


public class ScheduleViewHandler implements RequestStreamHandler{

	public LambdaLogger logger = null;
	
	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();
	
	//TODO: add lines about s3 storage and RDS?
	
	TimeSlot getTimeSlot(UUID id, LocalDate date, LocalTime startTime) throws Exception{
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
	
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to view weekly schedule");

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	        
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);
		
		ScheduleViewResponse response = null;
		
		// extract body from incoming HTTP POST request. If any error, then return 422 error
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
			} else {
				body = (String)event.get("body");
				if (body == null) {
					body = event.toJSONString();  // this is only here to make testing easier
				}
			}
		} catch (ParseException pe) {
			logger.log(pe.toString());
			response = new ScheduleViewResponse(UUID.randomUUID(), LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now(), LocalDate.now(), null, 400);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			ScheduleViewRequest req = new Gson().fromJson(body, ScheduleViewRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			int duration = 0;
			LocalTime startHour = LocalTime.now();
			LocalTime endHour = LocalTime.now();
			
			Schedule s;
			try {
				s = getSchedule(UUID.fromString(req.arg1));
				duration = s.duration;
				startHour = s.startTime;
				endHour = s.endTime;
			} catch (Exception e) {
				s = null;
				respError = "Invalid ID ";
			}
			
			int year = 0;
			int month = 0;
			int day = 0;
			try {
				year = Integer.parseInt(req.arg2);
				month = Integer.parseInt(req.arg3);
				day = Integer.parseInt(req.arg4);
			} catch (NumberFormatException e) {
				respError += "Invalid Input Error ";
			}
			
			LocalDate dayInWeek = null;
			try {
				dayInWeek = LocalDate.of(year, month, day);
			}catch(Exception e) {
				respError += "Invalid date ";
			}
				
			DayOfWeek d = null;
			LocalDate mon = null;
			LocalDate tues = null;
			LocalDate wed = null;
			LocalDate thur = null;
			LocalDate fri = null;
			try {
				d = dayInWeek.getDayOfWeek();
				if(d == DayOfWeek.SATURDAY) {
					mon = dayInWeek.plusDays(2);
					tues = dayInWeek.plusDays(3);
					wed = dayInWeek.plusDays(4);
					thur = dayInWeek.plusDays(5);
					fri = dayInWeek.plusDays(6);
				}else if(d == DayOfWeek.SUNDAY) {
					mon = dayInWeek.plusDays(1);
					tues = dayInWeek.plusDays(2);
					wed = dayInWeek.plusDays(3);
					thur = dayInWeek.plusDays(4);
					fri = dayInWeek.plusDays(5);
				}else if(d == DayOfWeek.MONDAY) {
					mon = dayInWeek;
					tues = dayInWeek.plusDays(1);
					wed = dayInWeek.plusDays(2);
					thur = dayInWeek.plusDays(3);
					fri = dayInWeek.plusDays(4);
				}else if(d == DayOfWeek.TUESDAY) {
					mon = dayInWeek.plusDays(-1);
					tues = dayInWeek;
					wed = dayInWeek.plusDays(1);
					thur = dayInWeek.plusDays(2);
					fri = dayInWeek.plusDays(3);
				}else if(d == DayOfWeek.WEDNESDAY) {
					mon = dayInWeek.plusDays(-2);
					tues = dayInWeek.plusDays(-1);
					wed = dayInWeek;
					thur = dayInWeek.plusDays(1);
					fri = dayInWeek.plusDays(2);
				}else if(d == DayOfWeek.THURSDAY) {
					mon = dayInWeek.plusDays(-3);
					tues = dayInWeek.plusDays(-2);
					wed = dayInWeek.plusDays(-1);
					thur = dayInWeek;
					fri = dayInWeek.plusDays(1);
				}else {
					mon = dayInWeek.plusDays(-4);
					tues = dayInWeek.plusDays(-3);
					wed = dayInWeek.plusDays(-2);
					thur = dayInWeek.plusDays(-1);
					fri = dayInWeek;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
			ArrayList<TimeSlot> ts = new ArrayList<TimeSlot>();
			
			try {
				if(s.startDate.isBefore(mon.plusDays(1)) && s.endDate.isAfter(mon.minusDays(1))) {
					ts.addAll(getTimeSlotsByDate(s.id, mon, startHour, endHour, duration));
				}
				
				if(s.startDate.isBefore(tues.plusDays(1)) && s.endDate.isAfter(tues.minusDays(1))) {
					ts.addAll(getTimeSlotsByDate(s.id, tues, startHour, endHour, duration));
				}
				
				if(s.startDate.isBefore(wed.plusDays(1)) && s.endDate.isAfter(wed.minusDays(1))) {
					ts.addAll(getTimeSlotsByDate(s.id, wed, startHour, endHour, duration));
				}
				
				if(s.startDate.isBefore(thur.plusDays(1)) && s.endDate.isAfter(thur.minusDays(1))) {
					ts.addAll(getTimeSlotsByDate(s.id, thur, startHour, endHour, duration));
				}
				
				if(s.startDate.isBefore(fri.plusDays(1)) && s.endDate.isAfter(fri.minusDays(1))) {
					ts.addAll(getTimeSlotsByDate(s.id, fri, startHour, endHour, duration));
				}
			} catch (Exception e) {
				e.printStackTrace();
				respError += "Errored while collecting timeslots ";
			}
			
			
			
			if(respError.compareTo("")==0) {
				ScheduleViewResponse resp = new ScheduleViewResponse(UUID.fromString(req.arg1), mon, tues, wed, thur, fri, ts, 200);
				responseJson.put("body", new Gson().toJson(resp)); 
			}else {
				ErrorResponse resp = new ErrorResponse(respError, 400);
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