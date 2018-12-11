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
import java.util.Calendar;
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
import model.Schedule;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;

public class RetrieveOldScheduleHandler implements RequestStreamHandler {
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();

	boolean useRDS = true;

	ArrayList<Schedule> getSchedules(int n, String respError) throws Exception{
		if (logger != null) { logger.log("in getSchedule"); }
		ScheduleDAO dao = new ScheduleDAO();

		// check if present
		ArrayList<Schedule> underN = new ArrayList<Schedule>();
		ArrayList<Schedule> schedules = dao.getAllSchedules();
		if (schedules.size() == 0) {
			return underN;
		}
		//logger.log(ts.toString());
		for (Schedule s : schedules) {
			//Timestamp ts = new Timestamp(System.currentTimeMillis());
			//ts.setDate((LocalDate.now().getDayOfYear() - n));
			LocalDate ld = LocalDate.ofYearDay(LocalDate.now().getYear(), LocalDate.now().getDayOfYear() - n);
			if (s.endDate.isBefore(ld)) {
				underN.add(s);
			}
		}
		return underN;
	}
	
	

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
    	
//		FAKE fix this
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
		
		RetrieveNewScheduleResponse response;
		
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
			response = new RetrieveNewScheduleResponse(null, 422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			RetrieveNewScheduleRequest req = new Gson().fromJson(body, RetrieveNewScheduleRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			int temp = 0;
			ArrayList<Schedule> sArr = null;
			try {
				temp = Integer.parseInt(req.arg1);
			} catch (Exception e) {
				sArr = null;
				logger.log(req.arg1);
				respError += "Invalid num ";
			}
			if (temp != 0) {
				try {
					sArr = getSchedules(temp, respError);
				} catch (Exception e){
					sArr = null;
					respError += "My Error";
				}
			}
			
			// compute proper response
			if(respError.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(resp));
			}else {
				RetrieveNewScheduleResponse resp = new RetrieveNewScheduleResponse(sArr, 200);
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
