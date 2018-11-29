package model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Schedule {
	public UUID id;
	public String name;
	public int secretCode;
	public int duration;
	public LocalTime startTime;
	public LocalTime endTime;
	public LocalDate startDate;
	public LocalDate endDate;
	public Timestamp timestamp;
	
	public Schedule(UUID id, String name, int secretCode, int duration, LocalTime startTime, LocalTime endTime,
			LocalDate startDate, LocalDate endDate, Timestamp timestamp) {
		super();
		this.id = id;
		this.name = name;
		this.secretCode = secretCode;
		this.duration = duration;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timestamp = timestamp;
	}
	

}
