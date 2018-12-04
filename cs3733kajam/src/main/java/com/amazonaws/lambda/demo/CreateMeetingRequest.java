package com.amazonaws.lambda.demo;

import java.util.Random;
import java.util.UUID;

public class CreateMeetingRequest {
	String id;
	String timeSlotID;
	String name;
	String secretCode;
	
	//Participant: time date
		//can have 400 error because it'll be a manual input
	//organizer would just be time date, not verify secret code: would never know secret code of specific meeting.
		//organizer wouldn't ever get a 400 error because they are only pressing buttons.
	public CreateMeetingRequest(String id, String timeSlotid, String name, String secretCode) {
		this.name = name;
		this.timeSlotID = timeSlotid;
		this.secretCode = secretCode;
		this.id = id;
	}
	
	public String toString() {
		return "Meeting(" + name + "," + timeSlotID + "," + id + "," + secretCode + ")";
	}
}
