package com.amazonaws.lambda.demo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class GetTimeslotParticipantResponse {
	UUID id;
	LocalDate date;
	LocalTime time;
	boolean closed;
	boolean free;
	String meetingName;
	int httpCode;
	
	public GetTimeslotParticipantResponse(UUID id, LocalDate date, LocalTime time, boolean closed, boolean free, String meetingName, int httpCode) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.closed = closed;
		this.free = free;
		this.meetingName = meetingName;
		this.httpCode = httpCode;
	}
	
	public String toString() {
		return "Value("+id+","+date.toString()+","+time.toString()+","+closed+","+free+","+meetingName+","+httpCode+")";
	}
}
