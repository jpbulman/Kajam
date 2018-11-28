package com.amazonaws.lambda.demo;

public class ScheduleViewRequest {

	String arg1; // schedule ID?
	String arg2; // date
	
	public ScheduleViewRequest(String a1, String a2) {
		arg1 = a1;
		arg2 = a2;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + "," + arg2 + ")";
	}
	
}
