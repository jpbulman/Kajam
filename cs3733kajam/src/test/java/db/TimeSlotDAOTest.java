package db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.Test;

import com.amazonaws.lambda.demo.LambdaFunctionHandler;

import model.Schedule;
import model.TimeSlot;

public class TimeSlotDAOTest {

	@Test
	public void testFind() {
	    TimeSlotDAO td = new TimeSlotDAO();
	    try {
	    	TimeSlot t = td.getTimeSlot(UUID.fromString("9be6e94a-b57d-4cf6-939c-289328895598"));
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
	
	@Test
	public void testFindAll() {
		TimeSlotDAO td = new TimeSlotDAO();
		
		try {
			ArrayList<TimeSlot> timeSlots = td.getAllTimeSlots(UUID.fromString("9de8607b-c846-4207-92e6-b0d4a110fbad"));
			assertEquals(timeSlots.size(), 20);
			
		} catch(Exception e) {
			fail ("didn't work:" + e.getMessage());
		}
	}
	
}
