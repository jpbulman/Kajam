package com.amazonaws.lambda.demo;

public class DeleteOldScheduleRequest {
	String arg1; // N days

	public DeleteOldScheduleRequest(String arg1) {
		super();
		this.arg1 = arg1;
	}

	@Override
	public String toString() {
		return "DeleteOldScheduleRequest [arg1=" + arg1 + "]";
	}
}
