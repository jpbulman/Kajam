package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

import db.MeetingDAO;
import db.ScheduleDAO;
import model.Schedule;

public class DeleteScheduleTest {

    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":"
    		+ "{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\""
    		+ ":\"DELETE,GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":"
    		+ "\"{\\\"error\\\":\\\"Invalid input format\\\",\\\"httpCode\\\":400}\"}";    
    
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    // Test create schedule with completely invalid inputs
    @Test
    public void testDeleteScheduleHandler() throws IOException {
    	DeleteScheduleHandler handler = new DeleteScheduleHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));

        String sampleOutputString = output.toString();
        Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
    }
    
    @Test
    public void testDeleteMeetingHandler2() throws IOException {
    	ScheduleDAO dao = new ScheduleDAO();
    	
    	DeleteScheduleHandler handler = new DeleteScheduleHandler();
        
        String id = UUID.randomUUID().toString();
        Schedule s = new Schedule(UUID.fromString(id), "deleteTestName",125,
    			30,LocalTime.of(0, 0), LocalTime.of(3, 0), 
    			LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 10),
    			new Timestamp(System.currentTimeMillis()));
        try {
			dao.addSchedule(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        String SAMPLE_INPUT_STRING3 = "{\n" + 
        		"    \"arg1\": \""+id+"\"\n}";

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING3.getBytes());;
        OutputStream output = new ByteArrayOutputStream();
        
        handler.handleRequest(input, output, createContext("sample"));
        
        String sampleOutputString = output.toString();
        Assert.assertTrue(sampleOutputString.contains("httpCode\\\":200"));
    }

	
	
}
