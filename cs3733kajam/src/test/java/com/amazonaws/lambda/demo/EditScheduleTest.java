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

import db.ScheduleDAO;
import model.Schedule;

public class EditScheduleTest {
    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid ID Invalid number format Invalid date No timeslot matches given parameters \\\",\\\"httpCode\\\":400}\"}";    
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
//    // Test create schedule with completely invalid inputs
//    @Test
//    public void testEditSchedule() throws IOException {
//    	EditScheduleHandler handler = new EditScheduleHandler();
//
//        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
//        OutputStream output = new ByteArrayOutputStream();
//
//        handler.handleRequest(input, output, createContext("sample"));
//
//        // TODO: validate output here if needed.
//        String sampleOutputString = output.toString();
//        Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
//    }
    
    //Tests with valid inputs
    @Test
    public void testEditSchedule2() throws Exception {
    	EditScheduleHandler handler = new EditScheduleHandler();
    	
    	ScheduleDAO dao = new ScheduleDAO();
    	UUID id = UUID.randomUUID();
    	Schedule s = new Schedule(id, "editTest",125,
    			30,LocalTime.of(0, 0), LocalTime.of(3, 0), 
    			LocalDate.of(2000, 1, 2), LocalDate.of(2000, 6, 1),
    			new Timestamp(System.currentTimeMillis()));
    	dao.addSchedule(s);
    	
        final String SAMPLE_INPUT_STRING2 = "{\n" + 
        		"    \"arg1\": \""+id.toString()+"\",\n" + 
        		"    \"arg2\": \"testName\",\n" + 
        		"    \"arg3\": \"\",\n" + 
        		"    \"arg4\": \"\",\n" + 
        		"    \"arg5\": \"\",\n" + 
        		"    \"arg6\": \"2000\",\n" + 
        		"    \"arg7\": \"6\",\n" +
        		"    \"arg8\": \"2\",\n" +
        		"}";
    	
    	InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));
        
        dao.deleteSchedule(s);
        
        String sampleOutputString = output.toString();
        Assert.assertTrue(sampleOutputString.contains("httpCode\\\":200"));
        System.out.println(sampleOutputString);
        
    }
}
