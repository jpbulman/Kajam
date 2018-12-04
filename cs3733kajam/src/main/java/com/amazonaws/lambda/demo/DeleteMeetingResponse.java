package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class DeleteMeetingResponse {
	UUID id;
	UUID timeSlotID;
	String name;
	int secretCode;
	int httpCode;
	
	public DeleteMeetingResponse(UUID id, UUID timeSlotid, String name, int secretCode, int code) {
		this.name = name;
		
		this.timeSlotID = timeSlotid;
		this.secretCode = secretCode;
		
		httpCode = code;
		
		id = UUID.randomUUID();
		
		Random random = new Random();
		secretCode = random.nextInt(89999) + 10000;
	}
	
	public String toString() {
		return "Meeting(" + name + "," + timeSlotID + "," + id + "," + secretCode + ")";
	}
}
