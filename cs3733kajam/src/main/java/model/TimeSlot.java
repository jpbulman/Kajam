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
	
	public TimeSlot(UUID id, UUID scheduleID, LocalTime startTime, LocalTime endTime, LocalDate date, boolean isFree) {
		super();
		this.id = id;
		this.scheduleID = scheduleID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.date = date;
		this.isFree = isFree;
	}
}
