package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TimeSlot {
	public UUID id;
	public UUID scheduleID;
	public LocalTime startTime;
	public LocalTime endTime;
	public LocalDate date;
	public boolean isFree;
	public Meeting meeting;
	
	// Constructor to auto generate ID
	public TimeSlot(UUID scheduleID, LocalTime startTime, LocalTime endTime, LocalDate date, boolean isFree) {
		super();
		this.id = UUID.randomUUID();
		this.scheduleID = scheduleID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
		this.isFree = isFree;
		this.meeting = null;
	}
	
	// Constructor if ID is given
	public TimeSlot(UUID id, UUID scheduleID, LocalTime startTime, LocalTime endTime, LocalDate date, boolean isFree) {
		super();
		this.id = id;
		this.scheduleID = scheduleID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
		this.isFree = isFree;
		this.meeting = new Meeting();
	}

	public String toString() {
		return "TimeSlot [id=" + id + ", scheduleID=" + scheduleID + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", date=" + date + ", isFree=" + isFree + ", meeting=" + meeting + "]";
	}
	
	
}
