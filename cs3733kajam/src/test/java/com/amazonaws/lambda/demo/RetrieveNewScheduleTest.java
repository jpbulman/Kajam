package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

import db.ScheduleDAO;
import model.Schedule;

public class RetrieveNewScheduleTest {


	private static final String SAMPLE_INPUT_STRING = "{\n" +
    		"    \"arg1\": \"1\"" + "}";
    
    Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
    
    //Tests getSchedule with invalid id
    @Test
    public void RetrieveNewSchedulevalidID() throws IOException {
    	RetrieveNewScheduleHandler handler = new RetrieveNewScheduleHandler();
    	
		UUID id1 = UUID.fromString("eea5770b-84ac-446f-bfdc-442f26fb0111");
       
		//Timestamp ts = new Timestamp(System.currentTimeMillis());
		//ts.setHours(LocalTime.now().getHour() + 5);

        ScheduleDAO dao = new ScheduleDAO();
		Schedule s = new Schedule(id1, "123456789",125, 30, 
				LocalTime.of(0, 0), LocalTime.of(3, 0),
				LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 10), 
				LocalDateTime.now().toString());
        
		try {
			dao.addSchedule(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());;
        OutputStream output = new ByteArrayOutputStream();
       
        handler.handleRequest(input, output, createContext("sample2"));
        String sampleOutputString = output.toString();
        
		try {
			dao.deleteSchedule(dao.getSchedule(id1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print("sampleOutputString " + sampleOutputString);
        Assert.assertTrue(sampleOutputString.contains("123456789"));
		
		
    }
}