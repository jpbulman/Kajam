package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

public class CreateMeetingResponse {
	UUID id;
	UUID timeSlotID;
	String name;
	int secretCode;
	int httpCode;
	
	public CreateMeetingResponse(UUID id, UUID timeSlotid, String name, int secretCode, int code) {
		this.name = name;
		
		this.timeSlotID = timeSlotid;
		this.secretCode = secretCode;
		
		httpCode = code;
		
		this.id = UUID.randomUUID();
		
		this.secretCode = secretCode;
	}
	
	//TODO: figure out the format for this return
	public String toString() {
		return "Value(" + name + "," + id + "," + timeSlotID + "," + id + "," + secretCode + ")";
	}
	
	public int getSecretCode() {
		return secretCode;
	}

	public UUID getId() {
		return id;
	}

}
