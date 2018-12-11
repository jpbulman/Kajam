package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.util.UUID;

public class EditScheduleResponse {
	UUID id;
	String name;
	LocalDate start;
	LocalDate end;
	int httpCode;
	
	public EditScheduleResponse(UUID id, String name, LocalDate start, LocalDate end, int code) {
		this.id = id;
		this.name = name;
		this.start = start;
		this.end = end;
		httpCode = code;
	}
	
	public String toString() {
		return "Value("+id.toString()+","+name+","+start.toString()+","+end.toString()+","+httpCode+")";
	}
}