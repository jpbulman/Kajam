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

public class ChangeTimeSlotAvailabilityByDayOfWeek implements RequestStreamHandler{
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();

	boolean useRDS = true;
	
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
	
	TimeSlot getTimeSlot(UUID id, LocalDate date, LocalTime startTime) throws Exception{
		if (logger != null) { logger.log("in getTimeSlot"); }
		TimeSlotDAO dao = new TimeSlotDAO();
		
		// check if present 
		TimeSlot exist = dao.getTimeSlotByDateTime(id, date, startTime);
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
			response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(UUID.randomUUID(), DayOfWeek.MONDAY.toString(), LocalTime.now(), LocalTime.now(), false, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			ChangeTimeslotAvailabilityByDayOfWeekRequest req = new Gson().fromJson(body, ChangeTimeslotAvailabilityByDayOfWeekRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			
			UUID id;
			Schedule s = null;
			try {
				id = UUID.fromString(req.arg1);
				s = getSchedule(id);
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
			boolean allDaysFlag = false;
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
			}else {
				allDaysFlag = true;
			}
			
			LocalTime startTime = null;
			LocalTime endTime = null;
			int startHour = 0;
			int startMin = 0;
			int endHour = 0;
			int endMin = 0;
			
			try {
				if(!(req.arg3.compareTo("") == 0 && req.arg4.compareTo("") == 0 && req.arg5.compareTo("")==0 && req.arg6.compareTo("")==0)) {
					startHour = Integer.parseInt(req.arg3);
					startMin = Integer.parseInt(req.arg4);
					endHour = Integer.parseInt(req.arg5);
					endMin = Integer.parseInt(req.arg6);
				}
			} catch (Exception e) {
				e.printStackTrace();
				respError += "Invalid number format ";
			}
			
			try {
				if(req.arg3.compareTo("") == 0 && req.arg4.compareTo("") == 0 && req.arg5.compareTo("")==0 && req.arg6.compareTo("")==0) {
					startTime = s.startTime;
				}else {
					startTime = LocalTime.of(startHour, startMin);
				}
			} catch (Exception e) {
				e.printStackTrace();
				respError += "Invalid start time ";
			}
			
			try {
				if(req.arg3.compareTo("") == 0 && req.arg4.compareTo("") == 0 && req.arg5.compareTo("")==0 && req.arg6.compareTo("")==0) {
					endTime = s.endTime;
				}else {
					endTime = LocalTime.of(endHour, endMin);
				}
			} catch (Exception e) {
				e.printStackTrace();
				respError += "Invalid end time ";
			}
			
			System.out.println("allDaysFlag " + allDaysFlag);
			ArrayList<TimeSlot> ts = new ArrayList<TimeSlot>();
			LocalDate curr = null;
			try {
				curr = s.startDate;
				if(allDaysFlag) {
					if(curr.getDayOfWeek() == DayOfWeek.SATURDAY) {
						curr = curr.plusDays(2);
					}
					
					if(curr.getDayOfWeek() == DayOfWeek.SUNDAY) {
						curr = curr.plusDays(1);
					}
					
					while(curr.isBefore(s.endDate.plusDays(1))) {
						ts.addAll(getTimeSlotsByDate(s.id, curr, startTime, endTime, s.duration));
						if(curr.getDayOfWeek() == DayOfWeek.FRIDAY) {
							curr = curr.plusDays(3);
						}else {
							curr = curr.plusDays(1);
						}
					}
				}else {
					while(curr.getDayOfWeek() != day) {
						curr = curr.plusDays(1);
					}
					
					while(curr.isBefore(s.endDate.plusDays(1))) {
						ts.addAll(getTimeSlotsByDate(s.id, curr, startTime, endTime, s.duration));
						curr = curr.plusDays(7);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
				respError += "Error finding timeslots ";
			}
			
			System.out.println("allDaysFlag " + allDaysFlag);
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
					if(allDaysFlag) {
						response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(id, "all", startTime, endTime, true, 200);
					}else {
						response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(id, day.toString(), startTime, endTime, true, 200);
					}
					responseJson.put("body", new Gson().toJson(response));
				}else {
					try {
						for(int i = 0; i < ts.size(); i++) {
							TimeSlot temp = ts.get(i);
							temp.isFree = false;
							daoTS.updateTimeSlot(temp);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					System.out.println("allDaysFlag " + allDaysFlag);
					if(allDaysFlag) {
						response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(id, "all", startTime, endTime, false, 200);
					}else {
						response = new ChangeTimeslotAvailabilityByDayOfWeekResponse(id, day.toString(), startTime, endTime, false, 200);
					}
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