package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class DeleteMeetingRequest {
	String timeSlotID;
	String secretCode;
	
	public DeleteMeetingRequest(String timeSlotid, String secretCode, String code) {
		this.timeSlotID = timeSlotid;
		this.secretCode = secretCode;
	}
	public String toString() {
		return "Meeting(" + "," + timeSlotID + "," + "," + secretCode + ")";
	}
}
