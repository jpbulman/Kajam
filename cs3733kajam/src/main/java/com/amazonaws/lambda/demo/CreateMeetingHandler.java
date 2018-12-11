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
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;

import db.MeetingDAO;
import db.ScheduleDAO;
//import db.ScheduleDAO;
import model.Meeting;
import model.Schedule;

public class CreateMeetingHandler implements RequestStreamHandler {
	public LambdaLogger logger = null;

	// handle to our s3 storage
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
			.withRegion("us-east-2").build();

	boolean useRDS = true;
	
	
	boolean createMeeting(UUID id, UUID timeSlotid, String name, int secretCode) throws Exception {
		if (logger != null) { logger.log("in createMeeting"); }
		MeetingDAO dao = new MeetingDAO();
		
		// check if present
		Meeting exist = dao.getMeeting(id);
		if (exist == null) {
			Meeting meeting = new Meeting(id, timeSlotid, name, secretCode);
			dao.addMeeting(meeting);
			return true;
		} else {
			return false;
		}
	}
	
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
		
		CreateMeetingResponse response;
		
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
			response = new CreateMeetingResponse(null,  "", 400);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			//TODO: create a ScheduleRequest
			CreateMeetingRequest req = new Gson().fromJson(body, CreateMeetingRequest.class);
			logger.log(req.toString());
		
			UUID val1 = null; // timeslot
			int val2 = 0;     // secretcode
			UUID val3 = null; // id
			
			String r = "";

	
			try {
				val1 = UUID.fromString(req.timeSlotID);
			} catch (Exception e) {
				val1 = null;
				r = "Invalid input format";
			}
			
			
			
			// compute proper response
			if(r.compareTo("") != 0) { // If there is an error in input
				ErrorResponse resp = new ErrorResponse(r, 400);
				responseJson.put("body", new Gson().toJson(resp));
			
			}else {
				
				try {
					CreateMeetingResponse resp = new CreateMeetingResponse(val1, req.name, 200);
					if (createMeeting(resp.id, val1, req.name, resp.secretCode)) {
						responseJson.put("body", new Gson().toJson(resp)); 
					} else { // could add schedule to DB
						ErrorResponse ErrResp = new ErrorResponse("Unable to create meeting", 400);
						responseJson.put("body", new Gson().toJson(ErrResp));
					}
				} catch (Exception e) {
					ErrorResponse ErrResp = new ErrorResponse("Unable to create meeting: " + e.getMessage(), 400);
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