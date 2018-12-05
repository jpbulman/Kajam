package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class ChangeTimeslotAvailabilityResponse {
	UUID id;
	LocalDate date;
	LocalTime time;
	boolean available; 
	int code;
	
	public ChangeTimeslotAvailabilityResponse(UUID scheduleID, LocalDate date, LocalTime time, boolean available, int httpCode) {
		id = scheduleID;
		this.date = date;
		this.time = time;
		this.available = available;
		code = httpCode;
	}
	
	public String toString() {
		return "Value("+id+","+date+","+time+","+available+","+code+")";
	}
}
