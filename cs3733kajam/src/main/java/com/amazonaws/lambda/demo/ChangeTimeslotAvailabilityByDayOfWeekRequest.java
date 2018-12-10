package com.amazonaws.lambda.demo;

public class ChangeTimeslotAvailabilityByDayOfWeekRequest {
	String arg1; //Schedule id
	String arg2; //Day of Week
	String arg3; //availability
	
	public ChangeTimeslotAvailabilityByDayOfWeekRequest(String a1, String a2, String a3) {
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
	}
	
	public String toString() {
		return "Value(" + arg1 + "," + arg2 + "," + arg3 + ")";
	}
}
