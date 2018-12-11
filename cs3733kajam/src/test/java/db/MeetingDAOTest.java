package db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import model.Meeting;
import model.Schedule;

public class MeetingDAOTest {

	@Test
	public void testFind() {
	    MeetingDAO md = new MeetingDAO();
	    try {
	    	Meeting m = md.getMeeting(UUID.fromString("eea5770b-84ac-446f-bfdc-442f26fb2992"));
	    	System.out.println("Schedule " + m.name + " with id: " + m.id);
	    } catch (Exception e) {
	    	fail ("didn't work:" + e.getMessage());
	    }
	}
	
	@Test
	public void testFindInvalid() {
	    MeetingDAO md = new MeetingDAO();
	    try {
	    	Meeting m = md.getMeeting(UUID.fromString("eeeeeeee-84ac-446f-bfdc-442f26fb2992"));
	    	assertEquals(null, m);
	    } catch (Exception e) {
	    	fail ("didn't work:" + e.getMessage());
	    }
	}
	
	@Test
	public void testFindByTimeSlot() {
	    MeetingDAO md = new MeetingDAO();
	    try {
	    	Meeting m = md.getMeetingByTimeSlotID(UUID.fromString("eea5770b-84ac-446f-bfdc-442f26fb2992"));
	    	System.out.println("Schedule " + m.name + " with id: " + m.id);
	    	System.out.println(m.toString());
	    } catch (Exception e) {
	    	fail ("didn't work:" + e.getMessage());
	    }
	}
	
	@Test
	public void testUpdate() {
		MeetingDAO md = new MeetingDAO();
		try {
			Meeting m = md.getMeeting(UUID.fromString("eea5770b-84ac-446f-bfdc-442f26fb2992"));
			String newName = UUID.randomUUID().toString();
			m.name = newName;
			assertTrue(md.updateMeeting(m));
			m = md.getMeeting(UUID.fromString("eea5770b-84ac-446f-bfdc-442f26fb2992"));
			assertEquals(m.name, newName);
		}catch(Exception e) {
			fail("didn't work:"+e.getMessage());
		}
	}
	
	
	
}
