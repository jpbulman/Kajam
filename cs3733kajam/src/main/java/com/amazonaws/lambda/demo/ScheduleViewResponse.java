package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

public class ScheduleViewResponse {
	UUID id;
	LocalDate mon;
	LocalDate tues;
	LocalDate wed;
	LocalDate thurs;
	LocalDate fri;
	int httpCode;
	
	public ScheduleViewResponse(UUID id, LocalDate Mon, LocalDate Tues, LocalDate Wed, LocalDate Thurs, LocalDate Fri, int code) {
		this.id = id;
		
		this.mon = Mon;
		this.tues = Tues;
		this.wed = Wed;
		this.thurs = Thurs;
		this.fri = Fri;
		
		httpCode = code;
	}
	
	//TODO: figure out the format for this return
	public String toString() {
		return "Value(" + id + "," + mon.toString() + "," + tues.toString() + "," + 
	wed.toString() + "," + thurs.toString() + "," + fri.toString() + ")";
	}
}
