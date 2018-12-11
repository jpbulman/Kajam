package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

import db.ScheduleDAO;
import db.TimeSlotDAO;
import model.TimeSlot;

public class ChangeTimeslotAvailabilityGeneralTest {

    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid ID Invalid number format Invalid number format Invalid start time Invalid end time Error finding timeslots \\\",\\\"httpCode\\\":400}\"}";    
    private static final String SAMPLE_INPUT_STRING2 = "{\n" + 
    		"    \"arg1\": \"070b8ca4-c38d-44fc-a31e-550518d98d7b\",\n" + 
    		"    \"arg2\": \"3\",\n" + 
    		"    \"arg3\": \"4\",\n" + 
    		"    \"arg4\": \"00\",\n" + 
    		"    \"arg5\": \"5\",\n" + 
    		"    \"arg6\": \"00\",\n" +
    		"    \"arg7\": \"unavailable\"\n" + 
    		"}";
    
    private static final String SAMPLE_INPUT_STRING3 = "{\n" + 
    		"    \"arg1\": \"070b8ca4-c38d-44fc-a31e-550518d98d7b\",\n" + 
    		"    \"arg2\": \"7\",\n" + 
    		"    \"arg3\": \"4\",\n" + 
    		"    \"arg4\": \"00\",\n" + 
    		"    \"arg5\": \"5\",\n" + 
    		"    \"arg6\": \"00\",\n" +
    		"    \"arg7\": \"unavailable\"\n" + 
    		"}";
    
    private static final String SAMPLE_INPUT_STRING4 = "{\n" + 
    		"    \"arg1\": \"070b8ca4-c38d-44fc-a31e-550518d98d7b\",\n" + 
    		"    \"arg2\": \"7\",\n" + 
    		"    \"arg3\": \"4\",\n" + 
    		"    \"arg4\": \"00\",\n" + 
    		"    \"arg5\": \"5\",\n" + 
    		"    \"arg6\": \"00\",\n" +
    		"    \"arg7\": \"available\"\n" + 
    		"}";
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    // Test change timeslot availability with completely invalid inputs
    @Test
    public void testChangeTimeslotAvailability() throws IOException {
        ChangeTimeSlotAvailabilityByDayOfWeek handler = new ChangeTimeSlotAvailabilityByDayOfWeek();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));

        // TODO: validate output here if needed.
        String sampleOutputString = output.toString();
        Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
    }
    
    //Tries to change timeslot availability on Wednesdays
    @Test
    public void testChangeTimeslotAvailabilityOnWednesdays() throws IOException {
    	ChangeTimeSlotAvailabilityByDayOfWeek handler = new ChangeTimeSlotAvailabilityByDayOfWeek();
    	
    	InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));
        
        Assert.assertTrue(output.toString().contains("false"));
        Assert.assertTrue(output.toString().contains("200"));
        
        TimeSlotDAO s = new TimeSlotDAO();
        TimeSlot t1 = null;
        TimeSlot t2 = null;
        TimeSlot t3 = null;
        try {
			t1 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 2), LocalTime.of(4, 00));
			t2 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 9), LocalTime.of(4, 00));
			t3 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 16), LocalTime.of(4, 00));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Assert.assertFalse(t1.isFree);
        Assert.assertFalse(t2.isFree);
        Assert.assertFalse(t3.isFree);
    }
    
    //Tries to change timeslot availability on All Days
    @Test
    public void testChangeTimeslotAvailabilityOnAllDays() throws IOException {
    	ChangeTimeSlotAvailabilityByDayOfWeek handler = new ChangeTimeSlotAvailabilityByDayOfWeek();
    	
    	InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING3.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));
        
        System.out.println(output.toString());
        
        Assert.assertTrue(output.toString().contains("200"));
        Assert.assertTrue(output.toString().contains("false"));
        
        TimeSlotDAO s = new TimeSlotDAO();
        TimeSlot t1 = null;
        TimeSlot t2 = null;
        TimeSlot t3 = null;
        try {
			t1 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 3), LocalTime.of(4, 00));
			t2 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 15), LocalTime.of(4, 00));
			t3 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 8), LocalTime.of(4, 00));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Assert.assertFalse(t1.isFree);
        Assert.assertFalse(t2.isFree);
        Assert.assertFalse(t3.isFree);
        
    	InputStream input2 = new ByteArrayInputStream(SAMPLE_INPUT_STRING4.getBytes());
        OutputStream output2 = new ByteArrayOutputStream();

        handler.handleRequest(input2, output2, createContext("sample"));
        
        Assert.assertTrue(output2.toString().contains("true"));
        Assert.assertTrue(output2.toString().contains("200"));
        
        try {
			t1 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 3), LocalTime.of(4, 00));
			t2 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 15), LocalTime.of(4, 00));
			t3 = s.getTimeSlotByDateTime(UUID.fromString("070b8ca4-c38d-44fc-a31e-550518d98d7b"), LocalDate.of(2019, 1, 8), LocalTime.of(4, 00));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Assert.assertTrue(t1.isFree);
        Assert.assertTrue(t2.isFree);
        Assert.assertTrue(t3.isFree);
    }
}
