package com.amazonaws.lambda.demo;

public class ErrorResponse {
	String error;
	int httpCode;
	
	public ErrorResponse(String error, int code) {
		this.error = error;
		httpCode = code;
	}
	
	public String toString() {
		return "Value(" + error + "," + httpCode + ")";
	}
}