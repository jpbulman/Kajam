package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;

import db.DatabaseUtil;
import db.ScheduleDAO;
import db.TimeSlotDAO;
import model.Schedule;
import model.TimeSlot;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class EditScheduleHandler implements RequestStreamHandler {
	
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();
	
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
		
		EditScheduleResponse response;
		
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
			response = new EditScheduleResponse(UUID.randomUUID(), "", LocalDate.now(), LocalDate.now(), 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			EditScheduleRequest req = new Gson().fromJson(body, EditScheduleRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			
			UUID id = null;
			Schedule s = null;
			try {
				id = UUID.fromString(req.arg1);
				s = getSchedule(id);
			}catch(Exception e) {
				respError += "Invalid ID ";
			}
			
			String scheduleName = "";
			if(req.arg2.compareTo("") != 0) {
				scheduleName = req.arg2;
			}
			
			LocalDate startDate = null;
			LocalDate endDate = null;
			int startYear = 0;
			int startMonth = 0;
			int startDay = 0;
			int endYear = 0;
			int endMonth = 0;
			int endDay = 0;
			
			if(req.arg3.compareTo("") != 0 && req.arg4.compareTo("") != 0 && req.arg5.compareTo("") != 0) {
				try {
					startYear = Integer.parseInt(req.arg3);
					startMonth = Integer.parseInt(req.arg4);
					startDay = Integer.parseInt(req.arg5);
				} catch (Exception e) {
					respError += "Invalid number format for start date ";
				}
				
				try {
					startDate = LocalDate.of(startYear, startMonth, startDay);
				} catch (Exception e) {
					respError += "Invalid startDate ";
				}
				
				if(startDate.isAfter(s.startDate)) {
					startDate = s.startDate;
					respError += "New start date is not before old start date ";
				}
			}
			
			if(req.arg6.compareTo("") != 0 && req.arg7.compareTo("") != 0 && req.arg8.compareTo("") != 0) {
				try {
					endYear = Integer.parseInt(req.arg6);
					endMonth = Integer.parseInt(req.arg7);
					endDay = Integer.parseInt(req.arg8);
				} catch (Exception e) {
					respError += "Invalid number format for end date ";
				}
				
				try {
					endDate = LocalDate.of(endYear, endMonth, endDay);
				} catch (Exception e) {
					respError += "Invalid endDate ";
				}
				
				if(endDate.isBefore(s.endDate)) {
					endDate = s.endDate;
					respError += "New end date is not after old end date ";
				}
			}
			
			// compute proper response
			if(respError.compareTo("") != 0) { // If there is an error in input
				ErrorResponse ErrResp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(ErrResp));
			
			}else {
				EditScheduleResponse resp = new EditScheduleResponse(id, scheduleName, startDate, endDate, 200);
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
