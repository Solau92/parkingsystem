package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
//            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));

            ps.setTimestamp(5, (Objects.isNull(ticket.getOutTime()) ? null : (Timestamp.valueOf(ticket.getOutTime().truncatedTo(ChronoUnit.SECONDS)))));

//            Timestamp tmsb = Timestamp.valueOf(ticket.getOutTime()); //// NullerPointerException
//            ps.setTimestamp(5, (ticket.getOutTime() == null)? null: (tmsb));  

//            Marche mais...
//            if (ticket.getOutTime() == null) {
//            	ps.setTimestamp(5, null);
//            } else {
//              Timestamp tmsb = Timestamp.valueOf(ticket.getOutTime());
//              ps.setTimestamp(5, (tmsb));  
//            }
 
//            Fare_Rate           
            if (hasVehicleAlreadyParkAtLeastOnceInThisParking(ticket.getVehicleRegNumber()) > 0) {
            	ps.setDouble(6, 0.95);
            } else {
            	ps.setDouble(6, 1);
            }
            
            return ps.execute();
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

	public int hasVehicleAlreadyParkAtLeastOnceInThisParking(String vehicleRegNumber) {
    	// Doit retourner le nombre de fois où le véhicule est venu dans le parking
    	
		int numberTimesVehicleInThisParking = 0;
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.NUMBER_TIMES_CAR_IN_PARKING);           
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
            	return rs.getInt(1);
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error ----",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        // ?? 
		return numberTimesVehicleInThisParking;
	}
	
    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
//                ticket.setInTime(rs.getTimestamp(4));
//                ticket.setOutTime(rs.getTimestamp(5));
                ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
                // Marche, mais revoir :
                if (rs.getTimestamp(5) != null) {
                    ticket.setOutTime(rs.getTimestamp(5).toLocalDateTime());
                }
                System.out.println("taux : " + rs.getDouble(7));
                ticket.setFareRate(rs.getDouble(7));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    // pour ParkingService
    public boolean isVehicleInParking(String vehicleRegNumber) {
    	
    	// Doit retourner true si véhicule déjà dans parking, false sinon
    	
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.CHECK_CAR_ALREADY_IN_PARKING);           
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
            	if (rs.getInt(1) == 0) {
            		return false;
            	} 
            	return true;
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error ----",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        // ?? 
		return true;  		
    }
    	   
    
    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
//          ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            Timestamp tms = Timestamp.valueOf(ticket.getOutTime());
            ps.setTimestamp(2, tms);
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }


}
