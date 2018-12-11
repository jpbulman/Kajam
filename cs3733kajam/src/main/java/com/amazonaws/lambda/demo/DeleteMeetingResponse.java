package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class DeleteMeetingResponse {
	UUID timeSlotID;
	int secretCode;
	int httpCode;
	
	public DeleteMeetingResponse(UUID timeSlotid, int secretCode, int code) {
		
		this.timeSlotID = timeSlotid;
		this.secretCode = secretCode;
		
		httpCode = code;
		
		this.secretCode = secretCode;
	}
	
	public String toString() {
		return "Meeting(" + "," + timeSlotID + "," + "," + secretCode + ")";
	}
}