package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

public class ScheduleViewResponse {
	String name;
	LocalTime startTime;
	LocalTime endTime;
	LocalDate startDate;
	LocalDate endDate;
	int meetingDuration;
	int secretCode;
	int httpCode;
	
	public ScheduleViewResponse(String name, LocalTime startTime, LocalTime endTime, LocalDate startDate, 
			LocalDate endDate, int meetingDuration, int secret, int code) {
		this.name = name;
		
		this.startTime = startTime;
		this.endTime = endTime;
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.meetingDuration = meetingDuration;
		
		httpCode = code;
		secretCode = secret;
	}
	
	//TODO: figure out the format for this return
	public String toString() {
		return "Value(" + name + "," + startTime.toString() + "," + endTime.toString() + "," + 
	startDate.toString() + "," + endDate.toString() + "," + meetingDuration + "," + 
				secretCode + ")";
	}
}
