package com.amazonaws.lambda.demo;

public class DeleteScheduleRequest {

	String arg1;

	public DeleteScheduleRequest(String arg1) {
		super();
		this.arg1 = arg1;
	}

	@Override
	public String toString() {
		return "DeleteScheduleRequest [arg1=" + arg1 + "]";
	}
	
}
