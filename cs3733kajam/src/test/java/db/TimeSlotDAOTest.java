package db;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.Test;

import model.Schedule;
import model.TimeSlot;

public class TimeSlotDAOTest {

	@Test
	public void testFind() {
	    TimeSlotDAO td = new TimeSlotDAO();
	    try {
	    	TimeSlot t = td.getTimeSlot(UUID.fromString("f344ade2-f7b2-4186-8f1f-65c9dabf185c"));
	    	System.out.println("TimeSlot with id: " + t.id);
	    } catch (Exception e) {
	    	fail ("didn't work:" + e.getMessage());
	    }
	}
	
	@Test
	public void testCreate() {
	    TimeSlotDAO td = new TimeSlotDAO();
	    try {
	    	// can add it
	    	String id = UUID.randomUUID().toString();
	    	TimeSlot timeSlot = new TimeSlot(UUID.fromString(id), UUID.fromString(id),
	    			LocalTime.of(12, 0), LocalTime.of(12, 30), LocalDate.of(2018, 12, 3), true);
	    	boolean b = td.addTimeSlot(timeSlot);
	    	System.out.println("add timeSlot: " + b);
	    	
	    	// can retrieve it
	    	TimeSlot s2 = td.getTimeSlot(UUID.fromString(id));
	    	System.out.println("S2:" + s2.id);
	    	
	    	// can delete it
	    	assertTrue (td.deleteTimeSlot(s2));
	    } catch (Exception e) {
	    	fail ("didn't work:" + e.getMessage());
	    }
	}
	
}
