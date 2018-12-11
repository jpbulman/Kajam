package com.amazonaws.lambda.demo;

import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;
import java.time.LocalDate;

public class GetScheduleParticipantResponse {
	String name;
	LocalTime startTime;
	LocalTime endTime;
	LocalDate startDate;
	LocalDate endDate;
	int meetingDuration;
	UUID id;
	int httpCode;

	public GetScheduleParticipantResponse(String name, LocalTime startTime, LocalTime endTime, LocalDate startDate, 
			LocalDate endDate, int meetingDuration, UUID id, int code) {
		this.name = name;
		
		this.startTime = startTime;
		this.endTime = endTime;
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.meetingDuration = meetingDuration;
		
		httpCode = code;
		
		this.id = id;
	}
	
	public String toString() {
		return "Value(" + name + "," + startTime.toString() + "," + endTime.toString() + "," + 
	startDate.toString() + "," + endDate.toString() + "," + meetingDuration + "," + id + ")";
	}

	public UUID getId() {
		return id;
	}
}