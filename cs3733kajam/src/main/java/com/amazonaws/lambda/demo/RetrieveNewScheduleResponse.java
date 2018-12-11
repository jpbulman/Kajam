package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.UUID;

import model.Schedule;

public class RetrieveNewScheduleResponse {
	ArrayList<Schedule> schedulesList;
	int httpCode;
	
	public RetrieveNewScheduleResponse(ArrayList<Schedule> sArr, int httpCode) {
		//may have to do for loop
		this.schedulesList = sArr;
		this.httpCode = httpCode;
	}
	
	public String toString() {
		return "Value(" +  schedulesList + ")";
	}
}
