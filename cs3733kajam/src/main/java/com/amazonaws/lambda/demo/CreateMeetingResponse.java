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
	
	public CreateMeetingResponse(UUID timeSlotid, String name, int code) {
		this.name = name;
		
		this.timeSlotID = timeSlotid;
		
		httpCode = code;
		
		this.id = UUID.randomUUID();
		
		Random random = new Random();
		this.secretCode = random.nextInt(89999) + 10000;
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
