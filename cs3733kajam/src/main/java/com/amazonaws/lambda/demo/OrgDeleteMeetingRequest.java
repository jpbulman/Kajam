package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class OrgDeleteMeetingRequest {
	String timeSlotID;
	String name;
	String id;
	
	public OrgDeleteMeetingRequest(String id, String timeSlotid, String name) {
		this.name = name;
		this.timeSlotID = timeSlotid;
		this.id = id;
	}
	public String toString() {
		return "Meeting(" + name + "," + timeSlotID + "," + id + ")";
	}
}
