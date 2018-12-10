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

public class ChangeTimeslotAvailabilityTest {

    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid ID Invalid number format Invalid date No timeslot matches given parameters \\\",\\\"httpCode\\\":400}\"}";    
    private static final String SAMPLE_INPUT_STRING2 = "{\n" + 
    		"    \"arg1\": \"9944800c-15d9-43e1-b58c-8b81c55a26bb\",\n" + 
    		"    \"arg2\": \"2018\",\n" + 
    		"    \"arg3\": \"12\",\n" + 
    		"    \"arg4\": \"10\",\n" + 
    		"    \"arg5\": \"1\",\n" + 
    		"    \"arg6\": \"00\",\n" + 
    		"    \"arg7\": \"unavailable\",\n" + 
    		"}";
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    // Test create schedule with completely invalid inputs
    @Test
    public void testChangeTimeslotAvailability() throws IOException {
        ChangeTimeslotAvailability handler = new ChangeTimeslotAvailability();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));

        // TODO: validate output here if needed.
        String sampleOutputString = output.toString();
        Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
    }
    
    //Tests with valid inputs
    @Test
    public void testChangeTimeslotAvailability2() throws IOException {
    	ChangeTimeslotAvailability handler = new ChangeTimeslotAvailability();
    	
    	InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));
        
        ChangeTimeslotAvailabilityResponse output2 = new ChangeTimeslotAvailabilityResponse(UUID.fromString("9944800c-15d9-43e1-b58c-8b81c55a26bb"), LocalDate.of(2018, 12, 10), LocalTime.of(1, 00), false, 200);
        
        String sampleOutputString = output.toString();
        Assert.assertTrue(sampleOutputString.contains("code\\\":200"));
        System.out.println(sampleOutputString);
    }
}
