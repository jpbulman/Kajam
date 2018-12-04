package com.amazonaws.lambda.demo;

public class DeleteScheduleRequest {

	String id;

	public DeleteScheduleRequest(String id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {
		return "DeleteScheduleRequest [id=" + id + "]";
	}
	
}
