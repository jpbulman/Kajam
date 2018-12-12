package com.amazonaws.lambda.demo;

import java.util.ArrayList;

import model.Schedule;

public class DeleteOldScheduleResponse {

	ArrayList<Schedule> schedulesList;

	public DeleteOldScheduleResponse(ArrayList<Schedule> schedulesList) {
		super();
		this.schedulesList = schedulesList;
	}

	@Override
	public String toString() {
		return "DeleteOldScheduleResponse [schedulesList=" + schedulesList + "]";
	}
	
	
	
}
