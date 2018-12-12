package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.Test;

public class ScheduleTest {

	@Test
	public void testGenerateTimeSlots() {
	    Schedule s = new Schedule(UUID.randomUUID(), "test", 123, 30, 
	    		LocalTime.of(12, 0), LocalTime.of(16, 0), LocalDate.of(2018, 12, 03),
	    		LocalDate.of(2018, 12, 10),LocalDateTime.now().toString());
	    s.generateTimeSlots();
	    assertEquals(s.timeSlots.size(),48);
	}
	
}
