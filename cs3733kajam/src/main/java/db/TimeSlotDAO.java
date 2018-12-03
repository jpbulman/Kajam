package db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.UUID;

import model.TimeSlot;

public class TimeSlotDAO {
	
	java.sql.Connection conn;

    public TimeSlotDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		System.out.println(e);
    		conn = null;
    	}
    }
    
    
    public TimeSlot getTimeSlot(UUID id) throws Exception {
        
        try {
            TimeSlot t = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM TimeSlots WHERE id=?;");
            ps.setString(1,  id.toString());
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                t = generateTimeSlot(resultSet);
            }
            resultSet.close();
            ps.close();
            
            return t;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting time slot: " + e.getMessage());
        }
    }
    
    // Get all time slots in a given schedule
    public ArrayList<TimeSlot> getAllTimeSlots(UUID scheduleID) throws Exception{
    	
        try {
        	ArrayList<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM TimeSlots WHERE scheduleID=?;");
            ps.setString(1,  scheduleID.toString());
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
            	timeSlots.add(generateTimeSlot(resultSet));
            }
            resultSet.close();
            ps.close();
            
            return timeSlots;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting time slots: " + e.getMessage());
        }
    }
    
    public boolean deleteTimeSlot(TimeSlot t) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM TimeSlots WHERE id = ?;");
            ps.setString(1, t.id.toString());
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete time slot: " + e.getMessage());
        }
    }
    
    
    public boolean addTimeSlot(TimeSlot t) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM TimeSlots WHERE id = ?;");
            ps.setString(1, t.id.toString());
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
            	//TimeSlot ts = generateTimeSlot(resultSet);
                resultSet.close();
                return false;
            }

            ps = conn.prepareStatement("INSERT INTO TimeSlots (id,scheduleID,startTime, "
            		+ "endTime,date,isFree) values(?,?,?,?,?,?);");
            ps.setString(1,  t.id.toString());
            ps.setString(2,  t.scheduleID.toString());
            ps.setTime(3, Time.valueOf(t.startTime));
            ps.setTime(4, Time.valueOf(t.endTime));
            ps.setDate(5, Date.valueOf(t.date));
            ps.setBoolean(6, t.isFree);
            ps.execute();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to insert schedule: " + e.getMessage());
        }
    }
    
    public boolean updateTimeSlot(TimeSlot t) throws Exception {
        try {
        	String query = "UPDATE TimeSlots SET startTime=?, endTime=?, date=?, isFree=? WHERE id=?;";
        	PreparedStatement ps = conn.prepareStatement(query);
            ps.setTime(1, Time.valueOf(t.startTime));
            ps.setTime(2, Time.valueOf(t.endTime));
            ps.setDate(3, Date.valueOf(t.date));
            ps.setBoolean(4, t.isFree);
            ps.setString(5,  t.id.toString());
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);
        } catch (Exception e) {
            throw new Exception("Failed to update time slot: " + e.getMessage());
        }
    }
    
    
    private TimeSlot generateTimeSlot(ResultSet resultSet) throws Exception {
    	String id = resultSet.getString("id");
        String scheduleID  = resultSet.getString("scheduleID");
        Time startTime = resultSet.getTime("startTime");
        Time endTime = resultSet.getTime("endTime");
        Date date = resultSet.getDate("date");
        boolean isFree = resultSet.getBoolean("isFree");
        
        
        return new TimeSlot (UUID.fromString(id),UUID.fromString(scheduleID), 
        		startTime.toLocalTime(), endTime.toLocalTime(), date.toLocalDate(), isFree);
    }
}
