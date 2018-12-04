package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

public class GetScheduleHandlerTest {

    private static final String SAMPLE_INPUT_STRING = "{\n" + 
    		"    \"arg1\": \"eea5770b-84ac-446f-bfdc-442f26fb2992\",\n" + 
    		"    \"arg2\": \"81075\"" + "}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"name\\\":\\\"name\\\",\\\"startTime\\\":{\\\"hour\\\":3,\\\"minute\\\":0,\\\"second\\\":0,\\\"nano\\\":0},\\\"endTime\\\":{\\\"hour\\\":4,\\\"minute\\\":0,\\\"second\\\":0,\\\"nano\\\":0},\\\"startDate\\\":{\\\"year\\\":2018,\\\"month\\\":1,\\\"day\\\":22},\\\"endDate\\\":{\\\"year\\\":2018,\\\"month\\\":1,\\\"day\\\":26},\\\"meetingDuration\\\":15,\\\"id\\\":\\\"eea5770b-84ac-446f-bfdc-442f26fb2992\\\",\\\"httpCode\\\":200}\"}";    
    private static final String SAMPLE_INPUT_STRING2 =  "{\n" + 
    		"    \"arg1\": \"eea5770b-84ac-446f-bfdc-442f26fb299\",\n" + 
    		"    \"arg2\": \"81075\"" + "}";
    private static final String EXPECTED_OUTPUT_STRING2 = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid ID \\\",\\\"httpCode\\\":400}\"}";
    private static final String SAMPLE_INPUT_STRING3 = "{\n" + 
    		"    \"arg1\": \"eea5770b-84ac-446f-bfdc-442f26fb2992\",\n" + 
    		"    \"arg2\": \"82345\"" + "}";
    private static final String EXPECTED_OUTPUT_STRING3 = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid secretCode\\\",\\\"httpCode\\\":400}\"}";
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    //Tests getSchedule with valid inputs
    @Test
    public void testGetScheduleValid() throws IOException {
        GetScheduleHandler handler = new GetScheduleHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
        OutputStream output = new ByteArrayOutputStream();
        
        handler.handleRequest(input, output, createContext("sample2"));
        Assert.assertEquals(EXPECTED_OUTPUT_STRING, output.toString());
    }
    
    //Tests getSchedule with invalid id
    @Test
    public void testGetScheduleInvalidID() throws IOException {
        GetScheduleHandler handler = new GetScheduleHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());;
        OutputStream output = new ByteArrayOutputStream();
        
        handler.handleRequest(input, output, createContext("sample2"));
        Assert.assertEquals(EXPECTED_OUTPUT_STRING2, output.toString());
    }
    
    //Tests getSchedule with invalid secretCode
    @Test
    public void testGetScheduleInvalidCode() throws IOException {
        GetScheduleHandler handler = new GetScheduleHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING3.getBytes());;
        OutputStream output = new ByteArrayOutputStream();
        
        handler.handleRequest(input, output, createContext("sample2"));
        Assert.assertEquals(EXPECTED_OUTPUT_STRING3, output.toString());
    }

}
