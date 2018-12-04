package model;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
	public ArrayList<TimeSlot> timeSlots;
	
	public Schedule(UUID id, String name, int secretCode, int duration, LocalTime startTime, LocalTime endTime,
			LocalDate startDate, LocalDate endDate, Timestamp timestamp) {
		this.id = id;
		this.name = name;
		this.secretCode = secretCode;
		this.duration = duration;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timestamp = timestamp;
		timeSlots = new ArrayList<TimeSlot>();
		//generateTimeSlots();
	}
	
	// Create time slots for every week day in schedule, within start and end hours
	public void generateTimeSlots() {
		for(LocalDate d = startDate; d.isBefore(endDate.plusDays(1)); d = d.plusDays(1)) {
			DayOfWeek dow = d.getDayOfWeek();
			if(dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) {
				LocalTime t = startTime;
				while(t.isBefore(endTime)) {
					timeSlots.add(new TimeSlot(id, t, t.plusMinutes(duration),d,false));
					t = t.plusMinutes(duration);
				}
			}
			
		}
		System.out.println("Generated " + timeSlots.size() + " time slots.");
	}
}
