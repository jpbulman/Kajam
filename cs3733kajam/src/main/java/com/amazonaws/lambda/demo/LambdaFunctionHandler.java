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
import model.Schedule;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LambdaFunctionHandler implements RequestStreamHandler {
	
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();

	boolean useRDS = true;
	
	
	boolean createSchedule(UUID id, String name, int secretCode, int duration, LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate) throws Exception {
		if (logger != null) { logger.log("in createSchedule"); }
		ScheduleDAO dao = new ScheduleDAO();
		
		// check if present
		Schedule exist = dao.getSchedule(id);
		if (exist == null) {
			Timestamp ts = new Timestamp(System.currentTimeMillis()); // create timestamp based on current time and date
			Schedule schedule = new Schedule (id, name, secretCode, duration, startTime, endTime, startDate, endDate, ts);
			return dao.addSchedule(schedule);
		} else {
			return false;
			//return dao.updateConstant(constant);
		}
	}
	
	

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    	
    	// FAKE fix this
//    	try {
//			DatabaseUtil.connect();
//			System.out.println("SUCCESS!");
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
    	
    	LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler of RequestStreamHandler");

		JSONObject headerJson = new JSONObject();
		headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?
		headerJson.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
	    headerJson.put("Access-Control-Allow-Origin",  "*");
	    
		JSONObject responseJson = new JSONObject();
		responseJson.put("headers", headerJson);
		
		ScheduleResponse response;
		
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
			response = new ScheduleResponse("", LocalTime.now(), LocalTime.now(), LocalDate.now(), LocalDate.now(), 0, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			ScheduleRequest req = new Gson().fromJson(body, ScheduleRequest.class);
			logger.log(req.toString());
			
			int val1 = 0; // start time
			int val2 = 0; // end time
			int val3 = 0; // start year
			int val4 = 0; // start month
			int val5 = 0; // start day
			int val6 = 0; // end year
			int val7 = 0; // end month
			int val8 = 0; // end day
			int val9 = 0; // duration
			
			String r = "";
			LocalTime startTime = null;
			LocalTime endTime = null;
			LocalDate startDate = null;
			LocalDate endDate = null;
			
			try {
				val1 = Integer.parseInt(req.arg2);
				val2 = Integer.parseInt(req.arg3);
				val3 = Integer.parseInt(req.arg4);
				val4 = Integer.parseInt(req.arg5);
				val5 = Integer.parseInt(req.arg6);
				val6 = Integer.parseInt(req.arg7);
				val7 = Integer.parseInt(req.arg8);
				val8 = Integer.parseInt(req.arg9);
				val9 = Integer.parseInt(req.arg10);
			} catch (NumberFormatException e){
				r = "Invalid input format";
			}
			
			try {
				startTime = LocalTime.of(val1, 0);
			} catch (Exception e){
				r += " Invalid start time"; 
			}
			
			try {
				endTime = LocalTime.of(val2, 0);
			} catch (Exception e) {
				r += " Invalid end time";
			}
			
			try {
				startDate = LocalDate.of(val3, val4, val5);
			} catch (Exception e) {
				r += " Invalid start date";
			}
			
			try {
				endDate = LocalDate.of(val6, val7, val8);
			} catch (Exception e) {
				r += " Invalid end date";
			}
			
			if(r.compareTo("") == 0) {
				if(startTime.compareTo(endTime) >= 0) {
					r += " endTime is before startTime";
				}
				if(startDate.compareTo(endDate) > 0) {
					r += " endDate is before startDate";
				}
			}

			
			// compute proper response
			if(r.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(r, 400);
				responseJson.put("body", new Gson().toJson(resp));
			
			}else {
				
				try {
					ScheduleResponse resp = new ScheduleResponse(req.arg1, startTime, endTime, startDate, endDate, val9, 200);
					if (createSchedule(resp.getId(), req.arg1, resp.getSecretCode(), val9, startTime, endTime, startDate, endDate)) {
						responseJson.put("body", new Gson().toJson(resp)); 
					} else { // could not add schedule to DB
						ErrorResponse ErrResp = new ErrorResponse("Unable to create schedule", 400);
						responseJson.put("body", new Gson().toJson(ErrResp));
					}
				} catch (Exception e) {
					ErrorResponse ErrResp = new ErrorResponse("Unable to create schedule: " + e.getMessage(), 400);
					responseJson.put("body", new Gson().toJson(ErrResp));
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
