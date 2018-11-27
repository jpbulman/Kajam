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

import edu.wpi.cs.heineman.demo.AddRequest;
import edu.wpi.cs.heineman.demo.AddResponse;

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
				response = new ScheduleResponse(200); //TODO: add more parameters
				responseJson.put("body", new Gson().toJson(response));
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
			response = new ScheduleResponse(422);  // unable to process input
	        responseJson.put("body", new Gson().toJson(response));
	        processed = true;
	        body = null;
		}
		
		if (!processed) {
			//TODO: create a ScheduleRequest
			ScheduleRequest req = new Gson().fromJson(body, ScheduleRequest.class);
			logger.log(req.toString());
			
			//TODO: check the type of all the inputs
			//TODO: Verify that endTime comes after startTime and endDate comes after startDate
			LocalTime startTime = LocalTime.of(req.arg1, 0);
			LocalTime endTime = LocalTime.of(req.arg2, 0);
			LocalDate startDate = LocalDate.of(req.arg3, req.arg4, req.arg5);
			LocalDate endDate = LocalDate.of(req.arg6, req.arg7, req.arg8);
			
			//TODO: parse the rest of the inputs and add them to the schedule response

			//Parse Inputs
			/*double val1 = 0.0;
			try {
				val1 = Double.parseDouble(req.arg1);
			} catch (NumberFormatException e) {
				val1 = loadConstant(req.arg1);
			}

			double val2 = 0.0;
			try {
				val2 = Double.parseDouble(req.arg2);
			} catch (NumberFormatException e) {
				val2 = loadConstant(req.arg2);
			}*/

			// compute proper response
			ScheduleResponse resp = new ScheduleResponse(val1 + val2, 200);
	        responseJson.put("body", new Gson().toJson(resp));  
		}
		
        logger.log("end result:" + responseJson.toJSONString());
        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(responseJson.toJSONString());  
        writer.close();
    }

}
