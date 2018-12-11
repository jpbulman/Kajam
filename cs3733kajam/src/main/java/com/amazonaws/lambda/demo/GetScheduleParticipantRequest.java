package com.amazonaws.lambda.demo;

public class GetScheduleParticipantRequest {
	String arg1;
	
	public GetScheduleParticipantRequest(String a1) {
		arg1 = a1;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + ")";
	}
}