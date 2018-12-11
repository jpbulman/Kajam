package com.amazonaws.lambda.demo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class ChangeTimeslotAvailabilityByDayOfWeekResponse {
	UUID id;
	String day;
	boolean available; 
	int code;
	
	public ChangeTimeslotAvailabilityByDayOfWeekResponse(UUID scheduleID, String day, LocalTime startTime, LocalTime endTime, boolean available, int httpCode) {
		id = scheduleID;
		this.day = day;
		this.available = available;
		code = httpCode;
	}
	
	public String toString() {
		return "Value("+id+","+day+","+available+","+code+")";
	}
}
