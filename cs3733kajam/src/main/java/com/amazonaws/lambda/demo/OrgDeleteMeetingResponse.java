package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class OrgDeleteMeetingResponse {
	UUID timeSlotID;
	int httpCode;
	
	public OrgDeleteMeetingResponse(UUID timeSlotid, int code) {
		this.timeSlotID = timeSlotid;
		this.httpCode = code;
	}

	@Override
	public String toString() {
		return "OrgDeleteMeetingResponse [timeSlotID=" + timeSlotID + ", httpCode=" + httpCode + "]";
	}	
}