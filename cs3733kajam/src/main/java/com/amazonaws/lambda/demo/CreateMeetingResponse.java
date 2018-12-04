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
		
		id = UUID.randomUUID();
		
		Random random = new Random();
		secretCode = random.nextInt(89999) + 10000;
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
