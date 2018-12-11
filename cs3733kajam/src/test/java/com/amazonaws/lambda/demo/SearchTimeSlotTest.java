package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

public class SearchTimeSlotTest {

    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid ID \\\",\\\"httpCode\\\":400}\"}";    
    private static final String SAMPLE_INPUT_STRING2 = "{\n" + 
    		"    \"id\": \"070b8ca4-c38d-44fc-a31e-550518d98d7b\",\n" + 
    		"    \"month\": \"1\",\n" +
    		"    \"year\": \"2019\",\n" +  
    		"    \"dayOfWeek\": \"1\",\n" + 
    		"    \"day\": \"\",\n" +  
    		"    \"hour\": \"4\",\n" +
    		"    \"minute\": \"0\",\n" +  
    		"}";
	
	
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    // Test create schedule with completely invalid inputs
    @Test
    public void testLambdaFunctionHandler() throws IOException {
        SearchTimeSlotHandler handler = new SearchTimeSlotHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));

        // TODO: validate output here if needed.
        String sampleOutputString = output.toString();
        Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
    }
    
    //Tests with valid inputs
    @Test
    public void testLambdaFunctionHandler2() throws IOException {
    	SearchTimeSlotHandler handler = new SearchTimeSlotHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));

        String sampleOutputString = output.toString();
        Assert.assertTrue(sampleOutputString.contains("httpcode\\\":200"));
        System.out.println(sampleOutputString);
    }
	
}
