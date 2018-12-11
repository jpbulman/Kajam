package com.amazonaws.lambda.demo;

public class SearchTimeSlotRequest {

	String id; // schedule id
	String month; // month
	String year; // year
	String dayOfWeek; // day of week
	String day; // day
	String hour;
	String minute;
	
	public SearchTimeSlotRequest(String id, String month, String year, String dayOfWeek, String day, String hour,
			String minute) {
		super();
		this.id = id;
		this.month = month;
		this.year = year;
		this.dayOfWeek = dayOfWeek;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}

	@Override
	public String toString() {
		return "SearchTimeSlotRequest [id=" + id + ", month=" + month + ", year=" + year + ", dayOfWeek=" + dayOfWeek
				+ ", day=" + day + ", hour=" + hour + ", minute=" + minute + "]";
	}
	
	
	
	
	
	
	
	
	
}
