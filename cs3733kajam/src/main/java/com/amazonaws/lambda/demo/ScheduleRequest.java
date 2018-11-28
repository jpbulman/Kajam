package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleRequest {
	String arg1;
	String arg2;
	String arg3;
	String arg4;
	String arg5;
	String arg6;
	String arg7;
	String arg8;
	String arg9;
	String arg10;
	
	public ScheduleRequest(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8, String a9, String a10) {
		arg1 = a1;
		arg2 = a2;
		arg3 = a3;
		arg4 = a4;
		arg5 = a5;
		arg6 = a6;
		arg7 = a7;
		arg8 = a8;
		arg9 = a9;
		arg10 = a10;
	}
	
	public String toString() {
		return "Schedule(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + "," + arg9 + "," + arg10 + ")";
	}
}
