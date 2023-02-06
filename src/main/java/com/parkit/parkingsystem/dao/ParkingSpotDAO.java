package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParkingSpotDAO {
	
	private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	/**
	 * Returns a ParkingSpot object from data base with the information found in the
	 * base with the parking number given
	 * @param parkingNumber
	 * @return a ParkingSpot
	 */
	public ParkingSpot getParkingSpot(int parkingId) {
		Connection con = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_PARKING_SPOT);
			ps.setInt(1, parkingId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				parkingSpot.setId(rs.getInt(1));
				parkingSpot.setAvailable(rs.getBoolean(2));
				parkingSpot.setParkingType(ParkingType.valueOf(rs.getString(3)));
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error fetching parking spot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return parkingSpot;
	}

	/**
	 * Returns the number of an available slot, or -1 if no slot available.
	 * @param parkingType
	 * @return -1 if no slot available
	 * @return the parking number chosen (minimum number)
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public int getNextAvailableSlot(ParkingType parkingType) {
		Connection con = null;
		int result = -1;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
			ps.setString(1, parkingType.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			dataBaseConfig.closeResultSet(rs);
			dataBaseConfig.closePreparedStatement(ps);
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
		} finally {
			dataBaseConfig.closeConnection(con);
		}
		return result;
	}

	/**
	 * Updates in database the availability of a given parking spot given in
	 * parameter.
	 * @param parkingSpot
	 * @return true if one row was updated, false otherwise
	 */
	public boolean updateParking(ParkingSpot parkingSpot) {
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			ps.setBoolean(1, parkingSpot.isAvailable());
			ps.setInt(2, parkingSpot.getId());
			int updateRowCount = ps.executeUpdate();
			dataBaseConfig.closePreparedStatement(ps);
			return (updateRowCount == 1);
		} catch (Exception ex) {
			logger.error("Error updating parking info", ex);
			return false;
		} finally {
			dataBaseConfig.closeConnection(con);
		}
	}

}
