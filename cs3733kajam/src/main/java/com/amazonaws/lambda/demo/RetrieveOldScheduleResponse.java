package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

import model.Schedule;

public class RetrieveOldScheduleResponse {
	ArrayList<Schedule> schedulesList;

	public RetrieveOldScheduleResponse(ArrayList<Schedule> schedules) {
		this.schedulesList = schedules;
	}
	
	public String toString() {
		return "Value(" +  schedulesList + ")";
	}
}
