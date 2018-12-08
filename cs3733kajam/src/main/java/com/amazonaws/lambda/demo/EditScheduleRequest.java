package com.amazonaws.lambda.demo;

public class EditScheduleRequest {
	String arg1;//schedule id
	String arg2;//schedule name
	String arg3;//startYear
	String arg4;//startMonth
	String arg5;//startDay
	String arg6;//endYear
	String arg7;//endMonth
	String arg8;//endYear
	
	public EditScheduleRequest(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8) {
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
		arg4 = a4;
		arg5 = a5;
		arg6 = a6;
		arg7 = a7;
		arg8 = a8;
	}
	
	public String toString() {
		return "Value(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + ")";
	}
}
