package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class ChangeTimeslotAvailabilityResponse {
	UUID id;
	LocalDate date;
	LocalTime startTime;
	LocalTime endTime;
	boolean available; 
	int code;
	
	public ChangeTimeslotAvailabilityResponse(UUID scheduleID, LocalDate date, LocalTime starttime, LocalTime endtime, boolean available, int httpCode) {
		id = scheduleID;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.available = available;
		code = httpCode;
	}
	
	public String toString() {
		return "Value("+id+","+date+","+startTime+","+endTime+","+available+","+code+")";
	}
}