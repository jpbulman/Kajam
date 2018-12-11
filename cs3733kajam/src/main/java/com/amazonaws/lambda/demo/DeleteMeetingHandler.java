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

import db.MeetingDAO;
//import db.ScheduleDAO;
import model.Meeting;


public class DeleteMeetingHandler implements RequestStreamHandler{


	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();
	
	
	boolean deleteMeeting(UUID timeSlotid, int secretCode) throws Exception {
		if (logger != null) { logger.log("in createMeeting"); }
		MeetingDAO dao = new MeetingDAO();
		Meeting exist;
		
		exist = dao.getMeetingByTimeSlotID(timeSlotid);
		if (exist == null) {
			//throw new NullPointerException();
			return false;
		} else if (exist.secretCode == secretCode) {
			dao.deleteMeeting(exist);
			return true;
		} else {
			return false;
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
		
		DeleteMeetingResponse response;
		
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
			response = new DeleteMeetingResponse(null, 0, 400);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}

		if (!processed) {
			DeleteMeetingRequest req = new Gson().fromJson(body, DeleteMeetingRequest.class);
			logger.log(req.toString());
			
			String respError = "";
			UUID val1 = null; // timeslot
			int val2 = 0;     // secretcode
			
			Meeting m;
			try {
				val2 = Integer.parseInt(req.secretCode);
			} catch (NumberFormatException e){
				respError = "Invalid input format";
			}
			try {
				val1 = UUID.fromString(req.timeSlotID);
			} catch (Exception e) {
				val1 = null;
				respError = "Invalid input format";
			}
			
			// compute proper response
			if(respError.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(respError, 400);
				responseJson.put("body", new Gson().toJson(resp));
			}else {
				try {
					if(deleteMeeting(val1, val2)) {
						DeleteMeetingResponse resp = new DeleteMeetingResponse(val1, val2, 200);
						responseJson.put("body", new Gson().toJson(resp));
					}else {
						ErrorResponse resp = new ErrorResponse("Invalid secretCode", 400);
						responseJson.put("body", new Gson().toJson(resp));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.log("------catch strange error test-----");
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
