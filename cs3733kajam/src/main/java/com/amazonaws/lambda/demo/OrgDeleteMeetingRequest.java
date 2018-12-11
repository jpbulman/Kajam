package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class OrgDeleteMeetingRequest {
	String timeSlotID;
	
	public OrgDeleteMeetingRequest(String timeSlotid) {
		this.timeSlotID = timeSlotid;
	}

	@Override
	public String toString() {
		return "OrgDeleteMeetingRequest [timeSlotID=" + timeSlotID + "]";
	}
}