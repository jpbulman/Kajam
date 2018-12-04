package com.amazonaws.lambda.demo;

import java.util.UUID;

public class DeleteScheduleResponse {

	UUID id;
	int httpCode;
	
	
	public DeleteScheduleResponse(UUID id, int httpCode) {
		super();
		this.id = id;
		this.httpCode = httpCode;
	}


	@Override
	public String toString() {
		return "DeleteScheduleResponse [id=" + id + ", httpCode=" + httpCode + "]";
	}
	
	
	
}
