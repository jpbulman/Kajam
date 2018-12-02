package com.amazonaws.lambda.demo;

public class ScheduleViewRequest {

	String arg1; // schedule ID
	String arg2; // year
	String arg3; // month
	String arg4; // day
	
	public ScheduleViewRequest(String a1, String a2, String a3, String a4) {
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
		arg4 = a4;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + "," + arg2 + ")";
	}
	
}
