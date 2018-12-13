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
import java.util.Iterator;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;

import db.ScheduleDAO;
import db.TimeSlotDAO;
import model.Schedule;
import model.TimeSlot;

public class SearchTimeSlotHandler implements RequestStreamHandler {
	
	public LambdaLogger logger = null;

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
	
	ArrayList<TimeSlot> getAllTimeSlots(UUID id) throws Exception{
		if (logger != null) { logger.log("in getAllTimeSlots"); }
		TimeSlotDAO dao = new TimeSlotDAO();
		
		// check if present
		ArrayList<TimeSlot> exist = dao.getAllTimeSlots(id);
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
		
		SearchTimeSlotResponse response;
		
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
			response = new SearchTimeSlotResponse(null, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			SearchTimeSlotRequest req = new Gson().fromJson(body, SearchTimeSlotRequest.class);
			logger.log(req.toString());
			
			Schedule s;
			String respError = "";
			boolean nullFlag;
			try {
				UUID id = UUID.fromString(req.id);
				s = getSchedule(id);
				nullFlag = false;
			}catch(Exception e) {
				e.printStackTrace();
				s = null;
				respError = "Invalid ID ";
				nullFlag = true;
			}
			
			int year , month, dayOfWeek, day, hour, minute;
			year = month = dayOfWeek = day = hour = minute = -1;
			DayOfWeek dow = null;
			LocalTime time = null;
			
			if(!nullFlag) {
				if(req.year.compareTo("") != 0) {
					try {
						year = Integer.parseInt(req.year);
					}catch(Exception e) {
						e.printStackTrace();
						respError += "Invalid year";
					}
				}
				
				if(req.month.compareTo("") != 0) {
					try {
						month = Integer.parseInt(req.month);
					}catch(Exception e) {
						e.printStackTrace();
						respError += "Invalid month ";
					}
				}
				
				if(req.dayOfWeek.compareTo("") != 0) {
					try {
						dayOfWeek = Integer.parseInt(req.dayOfWeek);
						if(dayOfWeek == 1) {
							dow = DayOfWeek.MONDAY;
						}
						else if(dayOfWeek == 2) {
							dow = DayOfWeek.TUESDAY;
						}
						else if(dayOfWeek == 3) {
							dow = DayOfWeek.WEDNESDAY;
						}
						else if(dayOfWeek == 4) {
							dow = DayOfWeek.THURSDAY;
						}
						else if(dayOfWeek == 5) {
							dow = DayOfWeek.FRIDAY;
						}
					}catch(Exception e) {
						e.printStackTrace();
						respError += "Invalid day of week ";
					}
				}
				
				if(req.day.compareTo("") != 0) {
					try {
						day = Integer.parseInt(req.day);
					}catch(Exception e) {
						e.printStackTrace();
						respError += "Invalid day";
					}
				}
				
				if(req.hour.compareTo("") != 0 && req.minute.compareTo("") != 0) {
					try {
						hour = Integer.parseInt(req.hour);
						minute = Integer.parseInt(req.minute);
						time = LocalTime.of(hour, minute);
					}catch(Exception e) {
						e.printStackTrace();
						respError += "Invalid day";
					}
				}
			}
			
			if(respError.compareTo("")!= 0) {
				ErrorResponse resp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(resp));
			}
			else {
				try {
					ArrayList<TimeSlot> ts = getAllTimeSlots(UUID.fromString(req.id));
					
					// check year
					if(year != -1) {
						for(Iterator<TimeSlot> i = ts.iterator(); i.hasNext();) {
							TimeSlot t = i.next();
							if(t.date.getYear() != year) {
								i.remove();
							}
						}
					}
					
					// check month
					if(month != -1) {
						for(Iterator<TimeSlot> i = ts.iterator(); i.hasNext();) {
							TimeSlot t = i.next();
							if(t.date.getMonthValue() != month) {
								i.remove();
							}
						}
					}
					
					// check dayOfWeek
					if(dow != null) {
						for(Iterator<TimeSlot> i = ts.iterator(); i.hasNext();) {
							TimeSlot t = i.next();
							if(t.date.getDayOfWeek() != dow) {
								i.remove();
							}
						}
					}
					
					// check day
					if(day != -1) {
						for(Iterator<TimeSlot> i = ts.iterator(); i.hasNext();) {
							TimeSlot t = i.next();
							if(t.date.getDayOfMonth() != day) {
								i.remove();
							}
						}
					}
					
					// check time
					if(time != null) {
						for(Iterator<TimeSlot> i = ts.iterator(); i.hasNext();) {
							TimeSlot t = i.next();
							if(t.startTime != time) {
								i.remove();
							}
						}
					}
					
					response = new SearchTimeSlotResponse(ts, 200);
					responseJson.put("body", new Gson().toJson(response));
					
				}catch(Exception e) {
					ErrorResponse resp = new ErrorResponse("Error in finding time slots", 400);
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
