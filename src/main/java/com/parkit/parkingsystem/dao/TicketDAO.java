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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * Save in database a ticket given in parameter.
	 * 
	 * @param ticket
	 * @return true if the SQL statement execution was ok (means that the first
	 *         result is a ResultSet object)
	 * @return false otherwise (if the first result of the SQL query is an update
	 *         count or there is no result) or there was an error fetching the next available slot
	 */
	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
			ps.setTimestamp(5, (Objects.isNull(ticket.getOutTime()) ? null
					: (Timestamp.valueOf(ticket.getOutTime().truncatedTo(ChronoUnit.SECONDS)))));
			ps.setDouble(6, ticket.getFareRate());
			return ps.execute();
		} catch (Exception ex) {
			logger.error("Error connection ?", ex);
		} 
		finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * Creates a ticket object from date base with the informations found in the
	 * base with the vehicleRegNumber given
	 * 
	 * @param vehicleRegNumber
	 * @return a ticket
	 * @return null if ??
	 */
	public Ticket getTicket(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ticket = new Ticket();
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
				// Marche, mais revoir : (rs.setTimestamp(5, (Objects.isNull(ticket.getOutTime()) ? null : (Timestamp.valueOf(ticket.getOutTime().truncatedTo(ChronoUnit.SECONDS)))));
				if (rs.getTimestamp(5) != null) { // if outTime not null
					ticket.setOutTime(rs.getTimestamp(5).toLocalDateTime());
				}							
				ticket.setFareRate(rs.getDouble(7));
			} else {
//				throw new Exception("Ticket not found");
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);

		} catch (Exception ex) {
			logger.error("Error fetching the ticket", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return ticket;
	}

	/**
	 * Updates in database the information of a given ticket in database a ticket
	 * given in parameter.
	 * 
	 * @param ticket
	 * @return true if the SQL statement execution was ok
	 * @return false if error saving ticket info 
	 */
	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, Timestamp.valueOf(ticket.getOutTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			dataBaseConfig.closeConnection(con);
		}
		return false;
	}

	/**
	 * Searches in data base if a vehicle is already in parking, given is regNumber
	 * 
	 * @param vehicleRegNumber
	 * @return true if the vehicle is in parking (which means that a went in but not
	 *         already out)
	 * @return false if the vehicle has never been in the parking, or if it went out
	 */
	public boolean isVehicleAlreadyInParkingInDataBase(String vehicleRegNumber) {

		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.CHECK_CAR_ALREADY_IN_PARKING);
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				
				return rs.getInt(1) != 0;
//				if (rs.getInt(1) == 0) {
//					return false;
//				}
//				return true;
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error connection", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return true;
	}

	/**
	 * Searches in data base how many times the vehicle has already parked in
	 * parking, given is regNumber
	 * 
	 * @param vehicleRegNumber
	 * @return the number of times (0 if never)
	 */
	public int numberOfTimesVehicleInDataBase(String vehicleRegNumber) {

		int numberTimesVehicleInThisParking = 0;
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.NUMBER_TIMES_CAR_IN_PARKING);
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return numberTimesVehicleInThisParking;
	}

}
