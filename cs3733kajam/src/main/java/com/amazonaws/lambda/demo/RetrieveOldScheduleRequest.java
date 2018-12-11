package com.amazonaws.lambda.demo;

public class RetrieveOldScheduleRequest {
	String arg1; // N days
	
	public RetrieveOldScheduleRequest(String a1) {
		arg1 = a1;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + ")";
	}
}
