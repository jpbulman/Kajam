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
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid ID Invalid number format Invalid date Error retrieving timeslots \\\",\\\"httpCode\\\":400}\"}";    
    private static final String SAMPLE_INPUT_STRING2 = "{\n" + 
    		"    \"arg1\": \"9944800c-15d9-43e1-b58c-8b81c55a26bb\",\n" + 
    		"    \"arg2\": \"2018\",\n" + 
    		"    \"arg3\": \"12\",\n" + 
    		"    \"arg4\": \"10\",\n" + 
    		"    \"arg5\": \"1\",\n" + 
    		"    \"arg6\": \"00\",\n" +
    		"    \"arg7\": \"2\",\n" +
    		"    \"arg8\": \"00\",\n" +
    		"    \"arg9\": \"unavailable\"\n" + 
    		"}";
    
    private static final String SAMPLE_INPUT_STRING3 = "{\n" + 
    		"    \"arg1\": \"9944800c-15d9-43e1-b58c-8b81c55a26bb\",\n" + 
    		"    \"arg2\": \"2018\",\n" + 
    		"    \"arg3\": \"12\",\n" + 
    		"    \"arg4\": \"10\",\n" + 
    		"    \"arg5\": \"1\",\n" + 
    		"    \"arg6\": \"00\",\n" +
    		"    \"arg7\": \"2\",\n" +
    		"    \"arg8\": \"00\",\n" +
    		"    \"arg9\": \"available\"\n" + 
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
    	
    	InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));
        
        InputStream input2 = new ByteArrayInputStream(SAMPLE_INPUT_STRING3.getBytes());
        OutputStream output3 = new ByteArrayOutputStream();

        handler.handleRequest(input2, output3, createContext("sample"));
        
        InputStream input3 = new ByteArrayInputStream(SAMPLE_INPUT_STRING3.getBytes());
        OutputStream output5 = new ByteArrayOutputStream();

        handler.handleRequest(input3, output5, createContext("sample"));
        
        Assert.assertTrue(output.toString().contains("false"));
        Assert.assertTrue(output.toString().contains("200"));
        Assert.assertTrue(output3.toString().contains("true"));
        Assert.assertTrue(output3.toString().contains("200"));
        Assert.assertTrue(output5.toString().contains("true"));
        Assert.assertTrue(output5.toString().contains("200"));
    }
}
