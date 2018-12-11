package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

public class RetrieveOldScheduleResponse {
	ArrayList<UUID> schedulesList;

	public RetrieveOldScheduleResponse(ArrayList<UUID> schedules) {
		this.schedulesList = schedules;
	}
	
	public String toString() {
		return "Value(" +  schedulesList + ")";
	}
}
