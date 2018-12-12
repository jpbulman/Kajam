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

public class RetrieveOldScheduleTest {


	private static final String SAMPLE_INPUT_STRING = "{\n" +
    		"    \"arg1\": \"0\"" + "}";
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    //Tests getSchedule with invalid id
    @Test
    public void RetrieveOldSchedulevalidID() throws IOException {
    	RetrieveOldScheduleHandler handler = new RetrieveOldScheduleHandler();
    	
        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
        OutputStream output = new ByteArrayOutputStream();
        
        handler.handleRequest(input, output, createContext("sample2"));
        String sampleOutputString = output.toString();
        Assert.assertTrue(sampleOutputString.contains("httpCode\\\":200"));
    }
}