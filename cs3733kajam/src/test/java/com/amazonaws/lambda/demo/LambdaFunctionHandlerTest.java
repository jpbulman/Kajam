package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerTest {

    private static final String SAMPLE_INPUT_STRING = "{\"foo\": \"bar\"}";
    private static final String EXPECTED_OUTPUT_STRING = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"error\\\":\\\"Invalid input format Invalid start date Invalid end date\\\",\\\"httpCode\\\":400}\"}";    
    private static final String SAMPLE_INPUT_STRING2 = "{\n" + 
    		"    \"arg1\": \"name\",\n" + 
    		"    \"arg2\": \"3\",\n" + 
    		"    \"arg3\": \"4\",\n" + 
    		"    \"arg4\": \"2018\",\n" + 
    		"    \"arg5\": \"1\",\n" + 
    		"    \"arg6\": \"22\",\n" + 
    		"    \"arg7\": \"2018\",\n" + 
    		"    \"arg8\": \"1\",\n" + 
    		"    \"arg9\": \"26\",\n" + 
    		"    \"arg10\": \"15\"\n" + 
    		"}";
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    // Test create schedule with completely invalid inputs
    @Test
    public void testLambdaFunctionHandler() throws IOException {
        LambdaFunctionHandler handler = new LambdaFunctionHandler();

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
        LambdaFunctionHandler handler = new LambdaFunctionHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));

        String sampleOutputString = output.toString();
        Assert.assertTrue(sampleOutputString.contains("httpCode\\\":200"));
        System.out.println(sampleOutputString);
    }
    
    //Tests getSchedule
    @Test
    public void testGetSchedule() throws IOException {
    	LambdaFunctionHandler handler = new LambdaFunctionHandler();
        GetScheduleHandler handler2 = new GetScheduleHandler();

        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING2.getBytes());;
        OutputStream output = new ByteArrayOutputStream();

        handler.handleRequest(input, output, createContext("sample"));

        String sampleOutputString = output.toString();
        
        //String id = output.toString().substring(output.toString().lastIndexOf(",", output.toString().lastIndexOf(",")-1)+10,output.toString().lastIndexOf(",")-2);
        //String secretCode = output.toString().substring(output.toString().lastIndexOf(",", output.toString().lastIndexOf(",", output.toString().lastIndexOf(",")-1)+14)+10, output.toString().lastIndexOf(",", output.toString().lastIndexOf(",")-1));
        String id = "eea5770b-84ac-446f-bfdc-442f26fb2992";
        String secretCode = "81075";
        System.out.println("secretCode " + secretCode);
        String expectedoutput = "{\"headers\":{\"Access-Control-Allow-Origin\":\"*\",\"Access-Control-Allow-Methods\":\"GET,POST,OPTIONS\",\"Content-Type\":\"application\\/json\"},\"body\":\"{\\\"name\\\":\\\"name\\\",\\\"startTime\\\":{\\\"hour\\\":3,\\\"minute\\\":0,\\\"second\\\":0,\\\"nano\\\":0},\\\"endTime\\\":{\\\"hour\\\":4,\\\"minute\\\":0,\\\"second\\\":0,\\\"nano\\\":0},\\\"startDate\\\":{\\\"year\\\":2018,\\\"month\\\":1,\\\"day\\\":22},\\\"endDate\\\":{\\\"year\\\":2018,\\\"month\\\":1,\\\"day\\\":26},\\\"meetingDuration\\\":15,\\\"id\\\":\\\"eea5770b-84ac-446f-bfdc-442f26fb2992\\\",\\\"httpCode\\\":200}\"}";
        String s = "{\n" + 
        		"    \"arg1\": \"" +  id + "\",\n" + 
        		"    \"arg2\": \"" + secretCode + "\",\n" + "}";
        InputStream input2 = new ByteArrayInputStream(s.getBytes());;
        OutputStream output2 = new ByteArrayOutputStream();
        
        handler2.handleRequest(input2, output2, createContext("sample2"));
        Assert.assertEquals(expectedoutput, output2.toString());
    }

}
