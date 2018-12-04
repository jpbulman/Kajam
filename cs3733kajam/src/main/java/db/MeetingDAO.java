package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import model.Meeting;

public class MeetingDAO {

	java.sql.Connection conn;

    public MeetingDAO() {
    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		System.out.println(e);
    		conn = null;
    	}
    }
    
    // Access meeting by meeting ID
    public Meeting getMeeting(UUID id) {
        
        try {
            Meeting m = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meetings WHERE id=?;");
            ps.setString(1,  id.toString());
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                m = generateMeeting(resultSet);
            }
            resultSet.close();
            ps.close();
            
            return m;

        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
            //throw new Exception("Failed in getting meeting: " + e.getMessage());
        }
    }
    
    // Access meeting by time slot ID
    public Meeting getMeetingByTimeSlotID(UUID timeSlotID) {
    	try {
            Meeting m = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meetings WHERE timeSlotID=?;");
            ps.setString(1,  timeSlotID.toString());
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                m = generateMeeting(resultSet);
            }
            resultSet.close();
            ps.close();
            
            return m;

        } catch (Exception e) {
        	e.printStackTrace();
            //throw new Exception("Failed in getting meeting: " + e.getMessage());
        	return null;
        }
    }
    
    public boolean deleteMeeting(Meeting m) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Meetings WHERE id = ?;");
            ps.setString(1, m.id.toString());
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete meeting: " + e.getMessage());
        }
    }
    
    
    public boolean addMeeting(Meeting m) throws Exception {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Meetings WHERE id = ?;");
            ps.setString(1, m.id.toString());
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
            	//TimeSlot ts = generateTimeSlot(resultSet);
                resultSet.close();
                return false;
            }

            ps = conn.prepareStatement("INSERT INTO Meetings (id,timeSlotID,name, secretCode) "
            		+ "values(?,?,?,?);");
            ps.setString(1,  m.id.toString());
            ps.setString(2,  m.timeSlotID.toString());
            ps.setString(3, m.name);
            ps.setInt(4, m.secretCode);
            ps.execute();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to insert meeting: " + e.getMessage());
        }
    }
    
    public boolean updateMeeting(Meeting m) throws Exception {
        try {
        	String query = "UPDATE Meetings SET name=?, secretCode=? WHERE id=?;";
        	PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, m.name);
            ps.setInt(2, m.secretCode);
            ps.setString(3,  m.id.toString());
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);
        } catch (Exception e) {
            throw new Exception("Failed to update meeting: " + e.getMessage());
        }
    }
    
    
    
    private Meeting generateMeeting(ResultSet resultSet) throws Exception {
    	String id = resultSet.getString("id");
        String timeSlotID  = resultSet.getString("timeSlotID");
        String name = resultSet.getString("name");
        int secretCode = resultSet.getInt("secretCode");
        
        return new Meeting (UUID.fromString(id),UUID.fromString(timeSlotID), name, secretCode);
    }
	
}
