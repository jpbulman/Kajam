package com.amazonaws.lambda.demo;

public class ChangeTimeslotAvailabilityByDayOfWeekRequest {
	String arg1; //Schedule id
	String arg2; //Day of Week
	String arg3; //start hour
	String arg4; //start min
	String arg5; //end hour
	String arg6; //end min
	String arg7; //availability
	
	public ChangeTimeslotAvailabilityByDayOfWeekRequest(String a1, String a2, String a3, String a4, String a5, String a6, String a7) {
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
		arg4 = a4;
		arg5 = a5;
		arg6 = a6;
		arg7 = a7;
	}
	
	public String toString() {
		return "Value(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + ")";
	}
}
