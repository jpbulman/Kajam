package com.amazonaws.lambda.demo;

public class GetScheduleRequest {
	String arg1;
	
	public GetScheduleRequest(String a1) {
		arg1 = a1;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + ")";
	}
}
