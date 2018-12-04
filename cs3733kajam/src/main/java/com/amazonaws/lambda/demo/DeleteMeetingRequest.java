package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class DeleteMeetingRequest {
	String id;
	String timeSlotID;
	String name;
	String secretCode;
	
	public DeleteMeetingRequest(String id, String timeSlotid, String name, String secretCode, String code) {
		this.name = name;
		this.timeSlotID = timeSlotid;
		this.secretCode = secretCode;
		this.id = id;
	}
	public String toString() {
		return "Meeting(" + name + "," + timeSlotID + "," + id + "," + secretCode + ")";
	}
}
