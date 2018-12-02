package com.amazonaws.lambda.demo;

public class GetScheduleRequest {
	String arg1;
	String arg2;
	
	public GetScheduleRequest(String a1, String a2) {
		arg1 = a1;
		arg2 = a2;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + "," + arg2 + ")";
	}
}
