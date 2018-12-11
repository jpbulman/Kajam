package model;

import java.util.UUID;

public class Meeting {

	public UUID id;
	public UUID timeSlotID;
	public String name;
	public int secretCode;
	
	public Meeting(UUID id, UUID timeSlotID, String name, int secretCode) {
		super();
		this.id = id;
		this.timeSlotID = timeSlotID;
		this.name = name;
		this.secretCode = secretCode;
	}
	
	public Meeting() {
		id = null;
		timeSlotID = null;
		name = "";
		secretCode = 0;
	}

	@Override
	public String toString() {
		return "Meeting [id=" + id + ", timeSlotID=" + timeSlotID + ", name=" + name + ", secretCode=" + secretCode
				+ "]";
	}
	
}
