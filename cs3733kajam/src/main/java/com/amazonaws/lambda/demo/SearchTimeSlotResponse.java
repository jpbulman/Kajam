package com.amazonaws.lambda.demo;

import java.util.ArrayList;

import model.TimeSlot;

public class SearchTimeSlotResponse {

	ArrayList<TimeSlot> timeSlots;
	int httpcode;

	public SearchTimeSlotResponse(ArrayList<TimeSlot> timeSlots, int httpcode) {
		super();
		this.timeSlots = timeSlots;
		this.httpcode = httpcode;
	}

	@Override
	public String toString() {
		return "SearchTimeSlotResponse [timeSlots=" + timeSlots + ", httpcode=" + httpcode + "]";
	}
	
	
	
}
