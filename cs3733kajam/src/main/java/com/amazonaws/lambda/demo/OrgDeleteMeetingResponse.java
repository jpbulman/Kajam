package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class OrgDeleteMeetingResponse {
	UUID timeSlotID;
	String name;
	int httpCode;
	UUID id;
	
	public OrgDeleteMeetingResponse(UUID id, UUID timeSlotid, String name, int code) {
		this.name = name;
		
		this.timeSlotID = timeSlotid;
		
		this.httpCode = code;
		this.id = id;
	}
	
	public String toString() {
		return "Meeting(" + name + "," + timeSlotID + "," + id + ")";
	}
}
