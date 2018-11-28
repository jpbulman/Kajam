package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalTime;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;

import db.DatabaseUtil;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LambdaFunctionHandler implements RequestStreamHandler {
	
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();

	boolean useRDS = true;

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    	
    	// FAKE fix this
    	/*try {
			DatabaseUtil.connect();
			System.out.println("SUCCESS!");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
    	
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
				//response = new ScheduleResponse("", LocalTime.now(), LocalTime.now(), LocalDate.now(), LocalDate.now(), 0, 200); //TODO: add more parameters
				String s = "DONE";
				responseJson.put("body", new Gson().toJson(s));
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
			//TODO: create a ScheduleRequest
			ScheduleRequest req = new Gson().fromJson(body, ScheduleRequest.class);
			logger.log(req.toString());
			
			int val1 = 0;
			int val2 = 0;
			int val3 = 0;
			int val4 = 0;
			int val5 = 0;
			int val6 = 0;
			int val7 = 0;
			int val8 = 0;
			int val9 = 0;
			
			String r = null;
			
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
			
			
			LocalTime startTime = LocalTime.of(val1, 0);
			LocalTime endTime = LocalTime.of(val2, 0);
			if(startTime.compareTo(endTime) >= 0) {
				r = "endTime is before startTime";
			}
			
			LocalDate startDate = LocalDate.of(val3, val4, val5);
			LocalDate endDate = LocalDate.of(val6, val7, val8);
			if(startDate.compareTo(endDate) > 0) {
				r = "endDate is before startDate";
			}
			
			// compute proper response
			if(r != null) {
				responseJson.put("body", new Gson().toJson(r));
			}else {
				ScheduleResponse resp = new ScheduleResponse(req.arg1, startTime, endTime, startDate, endDate, val9, 200);
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
