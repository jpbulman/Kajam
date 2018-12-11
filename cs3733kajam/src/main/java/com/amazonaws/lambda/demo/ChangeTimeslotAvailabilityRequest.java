package com.amazonaws.lambda.demo;

public class ChangeTimeslotAvailabilityRequest {
	String arg1; //Schedule id
	String arg2; //year
	String arg3; //month
	String arg4; //day
	String arg5; //start hour
	String arg6; //start min
	String arg7; //end hour
	String arg8; //end min
	String arg9; //availability
	
	public ChangeTimeslotAvailabilityRequest(String a1, String a2, String a3, String a4, String a5, String a6, String a7) {
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
		arg4 = a4;
		arg5 = a5;
		arg6 = a6;
		arg7 = a7;
	}
	
	public String toString() {
		return "Value(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + "," + arg9 + ")";
	}
}