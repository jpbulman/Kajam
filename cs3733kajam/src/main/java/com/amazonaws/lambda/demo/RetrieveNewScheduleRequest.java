package com.amazonaws.lambda.demo;

public class RetrieveNewScheduleRequest {
	String arg1; // N hours
	
	public RetrieveNewScheduleRequest(String a1) {
		arg1 = a1;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + ")";
	}
}
