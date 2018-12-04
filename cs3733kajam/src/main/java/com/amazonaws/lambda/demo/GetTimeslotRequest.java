package com.amazonaws.lambda.demo;

public class GetTimeslotRequest {
	String arg1; //schedule ID
	String arg2; //year
	String arg3; //month
	String arg4; //day
	String arg5; //hour
	String arg6; //min
	
	
	public GetTimeslotRequest(String a1, String a2, String a3, String a4, String a5, String a6) {
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
		arg4 = a4;
		arg5 = a5;
		arg6 = a6;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + ")";
	}
}
