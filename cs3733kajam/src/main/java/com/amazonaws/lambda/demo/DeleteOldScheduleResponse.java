package com.amazonaws.lambda.demo;

import java.util.ArrayList;

import model.Schedule;

public class DeleteOldScheduleResponse {

	ArrayList<Schedule> schedulesList;
	int httpCode;
	
	
	public DeleteOldScheduleResponse(ArrayList<Schedule> schedulesList, int httpCode) {
		super();
		this.schedulesList = schedulesList;
		this.httpCode = httpCode;
	}


	@Override
	public String toString() {
		return "DeleteOldScheduleResponse [schedulesList=" + schedulesList + ", httpCode=" + httpCode + "]";
	}
	
	
	
}
