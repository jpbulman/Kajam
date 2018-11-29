package com.amazonaws.lambda.demo;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

public class ScheduleResponseTest extends TestCase {
	ScheduleResponse resp;
	
	protected void setup() {
		resp = new ScheduleResponse("Name", LocalTime.of(3, 0), LocalTime.of(5, 0), LocalDate.of(2017, 5, 12), LocalDate.of(2018, 3, 20), 15, 200);
	}
	
	@Test
	public void testId() {
		setup();
		assertNotNull(resp.id);
		assertEquals(resp.id, resp.getId());
	}
	
	@Test
	public void testSecretCode() {
		setup();
		assertTrue(resp.secretCode >=10000);
		assertTrue(resp.secretCode <100000);
		assertEquals(resp.secretCode, resp.getSecretCode());
	}
	
	@Test
	public void testString() {
		setup();
		assertEquals(resp.toString(), "Value(" + resp.name + "," + resp.startTime.toString() + "," + resp.endTime.toString() + "," + 
	resp.startDate.toString() + "," + resp.endDate.toString() + "," + resp.meetingDuration + "," + resp.id + "," + 
				resp.secretCode + ")");
	}
}
