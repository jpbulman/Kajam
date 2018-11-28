package com.amazonaws.lambda.demo;

import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;
import java.time.LocalDate;

public class ScheduleResponse {
	String name;
	LocalTime startTime;
	LocalTime endTime;
	LocalDate startDate;
	LocalDate endDate;
	int meetingDuration;
	int secretCode;
	UUID id;
	int httpCode;

	public ScheduleResponse(String name, LocalTime startTime, LocalTime endTime, LocalDate startDate, 
			LocalDate endDate, int meetingDuration, int code) {
		this.name = name;
		
		this.startTime = startTime;
		this.endTime = endTime;
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.meetingDuration = meetingDuration;
		
		httpCode = code;
		
		id = UUID.fromString(name);//TODO:figure out how to make this unique with schedules with the same name
		
		Random random = new Random();
		secretCode = random.nextInt(89999) + 10000;
	}
	
	//TODO: figure out the format for this return
	public String toString() {
		return "Value(" + name + "," + startTime.toString() + "," + endTime.toString() + "," + 
	startDate.toString() + "," + endDate.toString() + "," + meetingDuration + "," + id + "," + 
				secretCode + ")";
	}
}
