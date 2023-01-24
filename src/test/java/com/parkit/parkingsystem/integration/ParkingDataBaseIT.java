package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	public static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach 
	public void setUpPerTest() throws Exception {
//		when(inputReaderUtil.readSelection()).thenReturn(1); // ParkingService > getVehicleType()
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
	}

	@AfterEach //@AfterAll
	public void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	void incomingCarIT() {
		
		// enlevé de BeforeEach
		when(inputReaderUtil.readSelection()).thenReturn(1); // ParkingService > getVehicleType()

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// TODO: check that a ticket is actually saved in DB and Parking table is
		// updated with availability

		Connection connection = null;
		Ticket ticket = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, true);

		try {
			connection = dataBaseTestConfig.getConnection();

			// Counts the number of line(s) / ticket(s) in database
			PreparedStatement ps1 = connection.prepareStatement("SELECT count(*) FROM TICKET t");
			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next()) {

				// Extracts tickets and parking attributes from database
				PreparedStatement ps2 = connection
						.prepareStatement("SELECT t.ID, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, "
								+ "t.PRICE, t.IN_TIME, t.OUT_TIME, t.FARE_RATE, p.TYPE, p.AVAILABLE "
								+ "FROM TICKET t, PARKING p " + "WHERE p.PARKING_NUMBER = t.PARKING_NUMBER");

				ResultSet rs2 = ps2.executeQuery();

				if (rs2.next()) {

					parkingSpot = new ParkingSpot(rs2.getInt(2), ParkingType.valueOf(rs2.getString(8)),
							rs2.getBoolean(9));

					ticket = new Ticket();
					ticket.setId(rs2.getInt(1));
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(rs2.getString(3));
					ticket.setPrice(rs2.getDouble(4));
					ticket.setInTime(rs2.getTimestamp(5).toLocalDateTime());
					ticket.setOutTime(rs2.getTimestamp(6) == null ? null : rs2.getTimestamp(6).toLocalDateTime());
					ticket.setFareRate(rs2.getDouble(7));
				}
				dataBaseTestConfig.closeResultSet(rs2);
				dataBaseTestConfig.closePreparedStatement(ps2);	
			}

			// Checks that there is only one line in the database
			assertEquals(1, rs1.getInt(1));

			// Checks the attributes of the ticket
			assertEquals(1, rs1.getInt(1));
			assertEquals(1, ticket.getId());
			assertEquals(1, ticket.getParkingSpot().getId());
			assertEquals("ABCDEF", ticket.getVehicleRegNumber());
			assertEquals(0.0, ticket.getPrice());
			assertNotNull(ticket.getInTime());
			assertNull(ticket.getOutTime());
			assertEquals(1.0, ticket.getFareRate());

			// Checks the attributes of the parkingSpot
			assertEquals(1, parkingSpot.getId());
			assertFalse(parkingSpot.isAvailable());
			assertEquals(ParkingType.CAR, parkingSpot.getParkingType());

			dataBaseTestConfig.closeResultSet(rs1);
			dataBaseTestConfig.closePreparedStatement(ps1);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dataBaseTestConfig.closeConnection(connection);
		}
	}

	@Test
	void incomingBikeIT() {
		
		// enlevé de BeforeEach
		when(inputReaderUtil.readSelection()).thenReturn(2); // ParkingService > getVehicleType()

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// TODO: check that a ticket is actually saved in DB and Parking table is
		// updated with availability

		Connection connection = null;
		Ticket ticket = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, true);

		try {
			connection = dataBaseTestConfig.getConnection();

			// Counts the number of line(s) / ticket(s) in database
			PreparedStatement ps1 = connection.prepareStatement("SELECT count(*) FROM TICKET t");
			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next()) {

				// Extracts tickets and parking attributes from database
				PreparedStatement ps2 = connection
						.prepareStatement("SELECT t.ID, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, "
								+ "t.PRICE, t.IN_TIME, t.OUT_TIME, t.FARE_RATE, p.TYPE, p.AVAILABLE "
								+ "FROM TICKET t, PARKING p " + "WHERE p.PARKING_NUMBER = t.PARKING_NUMBER");

				ResultSet rs2 = ps2.executeQuery();

				if (rs2.next()) {

					parkingSpot = new ParkingSpot(rs2.getInt(2), ParkingType.valueOf(rs2.getString(8)),
							rs2.getBoolean(9));

					ticket = new Ticket();
					ticket.setId(rs2.getInt(1));
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(rs2.getString(3));
					ticket.setPrice(rs2.getDouble(4));
					ticket.setInTime(rs2.getTimestamp(5).toLocalDateTime());
					ticket.setOutTime(rs2.getTimestamp(6) == null ? null : rs2.getTimestamp(6).toLocalDateTime());
					ticket.setFareRate(rs2.getDouble(7));
				}
				dataBaseTestConfig.closeResultSet(rs2);
				dataBaseTestConfig.closePreparedStatement(ps2);	
			}

			// Checks that there is only one line in the database
			assertEquals(1, rs1.getInt(1));

			// Checks the attributes of the ticket
			assertEquals(1, rs1.getInt(1));
			assertEquals(1, ticket.getId());
			assertEquals(4, ticket.getParkingSpot().getId());
			assertEquals("ABCDEF", ticket.getVehicleRegNumber());
			assertEquals(0.0, ticket.getPrice());
			assertNotNull(ticket.getInTime());
			assertNull(ticket.getOutTime());
			assertEquals(1.0, ticket.getFareRate());

			// Checks the attributes of the parkingSpot
			assertEquals(4, parkingSpot.getId());
			assertFalse(parkingSpot.isAvailable());
			assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());

			dataBaseTestConfig.closeResultSet(rs1);
			dataBaseTestConfig.closePreparedStatement(ps1);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dataBaseTestConfig.closeConnection(connection);
		}
	}
	
	@Test
	void exitingCarIT() {

//		// TODO: check that the fare generated and out time are populated correctly in
//		// the database
	
////      testParkingACar(); Mauvaise pratique ! 

		Connection connection = null;
		Ticket ticket = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		try {
			connection = dataBaseTestConfig.getConnection();
		
			// Writes one line in ticket database 
			// and updates the availability of the parkingSpot  
			PreparedStatement psInsertTicket = connection.prepareStatement("INSERT INTO TICKET"
					+ " (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, FARE_RATE) "
					+ " VALUES(1,'ABCDEF',0.0,LOCALTIMESTAMP()-INTERVAL 2 HOUR,null,1)");
			psInsertTicket.execute();
			
			PreparedStatement psInsertParkingSpot = connection.prepareStatement("UPDATE PARKING "
					+ " SET AVAILABLE = false"
					+ " WHERE PARKING_NUMBER = 1");
			psInsertParkingSpot.execute();
				
			dataBaseTestConfig.closePreparedStatement(psInsertTicket);
			dataBaseTestConfig.closePreparedStatement(psInsertParkingSpot);

			try {
				ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
				
				parkingService.processExitingVehicle();

			} catch (Exception e) {
				e.printStackTrace();
			}
	        		        
				// Extracts tickets and parking attributes from database
				PreparedStatement psGet = connection
						.prepareStatement("SELECT t.ID, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, "
								+ "t.PRICE, t.IN_TIME, t.OUT_TIME, t.FARE_RATE, p.TYPE, p.AVAILABLE "
								+ "FROM TICKET t, PARKING p " + "WHERE p.PARKING_NUMBER = t.PARKING_NUMBER");

				ResultSet rs = psGet.executeQuery();

				if (rs.next()) {

					parkingSpot = new ParkingSpot(rs.getInt(2), ParkingType.valueOf(rs.getString(8)),
							rs.getBoolean(9));

					ticket = new Ticket();
					ticket.setId(rs.getInt(1));
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(rs.getString(3));
					ticket.setPrice(rs.getDouble(4));
					ticket.setInTime(rs.getTimestamp(5).toLocalDateTime());
					ticket.setOutTime(rs.getTimestamp(6) == null ? null : rs.getTimestamp(6).toLocalDateTime());
					ticket.setFareRate(rs.getDouble(7));
				}
				
				// Checks the attributes of the ticket
				assertEquals(1, rs.getInt(1));
				assertEquals(1, ticket.getId());
				assertEquals(1, ticket.getParkingSpot().getId());
				assertEquals("ABCDEF", ticket.getVehicleRegNumber());
				assertNotNull(ticket.getInTime());
				assertNotNull(ticket.getOutTime());
				assertNotEquals(0.0, ticket.getPrice());
				assertEquals(1.5, ticket.getPrice());
				assertEquals(1.0, ticket.getFareRate());

				// Checks the attributes of the parkingSpot
				assertEquals(1, parkingSpot.getId());
				assertTrue(parkingSpot.isAvailable());
				assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
      
				dataBaseTestConfig.closePreparedStatement(psGet);
				dataBaseTestConfig.closeResultSet(rs);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dataBaseTestConfig.closeConnection(connection);
			}
	}
	
	@Test
	void exitingBikeIT() {

//		// TODO: check that the fare generated and out time are populated correctly in
//		// the database
	
////      testParkingACar(); Mauvaise pratique ! 

		Connection connection = null;
		Ticket ticket = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		try {
			connection = dataBaseTestConfig.getConnection();
		
			// Writes one line in ticket database 
			// and updates the availability of the parkingSpot  
			PreparedStatement psInsertTicket = connection.prepareStatement("INSERT INTO TICKET"
					+ " (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, FARE_RATE) "
					+ " VALUES(4,'ABCDEF',0.0,LOCALTIMESTAMP()-INTERVAL 2 HOUR,null,1)");
			psInsertTicket.execute();
			
			PreparedStatement psInsertParkingSpot = connection.prepareStatement("UPDATE PARKING "
					+ " SET AVAILABLE = false"
					+ " WHERE PARKING_NUMBER = 1");
			psInsertParkingSpot.execute();
				
			dataBaseTestConfig.closePreparedStatement(psInsertTicket);
			dataBaseTestConfig.closePreparedStatement(psInsertParkingSpot);

			try {
				ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
				
				parkingService.processExitingVehicle();

			} catch (Exception e) {
				e.printStackTrace();
			}
	        		        
				// Extracts tickets and parking attributes from database
				PreparedStatement psGet = connection
						.prepareStatement("SELECT t.ID, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, "
								+ "t.PRICE, t.IN_TIME, t.OUT_TIME, t.FARE_RATE, p.TYPE, p.AVAILABLE "
								+ "FROM TICKET t, PARKING p " + "WHERE p.PARKING_NUMBER = t.PARKING_NUMBER");

				ResultSet rs = psGet.executeQuery();

				if (rs.next()) {

					parkingSpot = new ParkingSpot(rs.getInt(2), ParkingType.valueOf(rs.getString(8)),
							rs.getBoolean(9));

					ticket = new Ticket();
					ticket.setId(rs.getInt(1));
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(rs.getString(3));
					ticket.setPrice(rs.getDouble(4));
					ticket.setInTime(rs.getTimestamp(5).toLocalDateTime());
					ticket.setOutTime(rs.getTimestamp(6) == null ? null : rs.getTimestamp(6).toLocalDateTime());
					ticket.setFareRate(rs.getDouble(7));
				}
				
				// Checks the attributes of the ticket
				assertEquals(1, rs.getInt(1));
				assertEquals(1, ticket.getId());
				assertEquals(4, ticket.getParkingSpot().getId());
				assertEquals("ABCDEF", ticket.getVehicleRegNumber());
				assertNotNull(ticket.getInTime());
				assertNotNull(ticket.getOutTime());
				assertNotEquals(0.0, ticket.getPrice());
				assertEquals(1.0, ticket.getPrice());
				assertEquals(1.0, ticket.getFareRate());

				// Checks the attributes of the parkingSpot
				assertEquals(4, parkingSpot.getId());
				assertTrue(parkingSpot.isAvailable());
				assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
      
				dataBaseTestConfig.closePreparedStatement(psGet);
				dataBaseTestConfig.closeResultSet(rs);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dataBaseTestConfig.closeConnection(connection);
			}
	}
	
	@Test
	void exitingCarLess30MinIT() {

//		// TODO: check that the fare generated and out time are populated correctly in
//		// the database
	
////      testParkingACar(); Mauvaise pratique ! 

		Connection connection = null;
		Ticket ticket = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		try {
			connection = dataBaseTestConfig.getConnection();
		
			// Writes one line in ticket database 
			// and updates the availability of the parkingSpot  
			PreparedStatement psInsertTicket = connection.prepareStatement("INSERT INTO TICKET"
					+ " (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, FARE_RATE) "
					+ " VALUES(1,'ABCDEF',0.0,LOCALTIMESTAMP()-INTERVAL 1 HOUR,null,1)");
			psInsertTicket.execute();
			
			PreparedStatement psInsertParkingSpot = connection.prepareStatement("UPDATE PARKING "
					+ " SET AVAILABLE = false"
					+ " WHERE PARKING_NUMBER = 1");
			psInsertParkingSpot.execute();
				
			dataBaseTestConfig.closePreparedStatement(psInsertTicket);
			dataBaseTestConfig.closePreparedStatement(psInsertParkingSpot);

			try {
				ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
				
				parkingService.processExitingVehicle();

			} catch (Exception e) {
				e.printStackTrace();
			}
	        		        
				// Extracts tickets and parking attributes from database
				PreparedStatement psGet = connection
						.prepareStatement("SELECT t.ID, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, "
								+ "t.PRICE, t.IN_TIME, t.OUT_TIME, t.FARE_RATE, p.TYPE, p.AVAILABLE "
								+ "FROM TICKET t, PARKING p " + "WHERE p.PARKING_NUMBER = t.PARKING_NUMBER");

				ResultSet rs = psGet.executeQuery();

				if (rs.next()) {

					parkingSpot = new ParkingSpot(rs.getInt(2), ParkingType.valueOf(rs.getString(8)),
							rs.getBoolean(9));

					ticket = new Ticket();
					ticket.setId(rs.getInt(1));
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(rs.getString(3));
					ticket.setPrice(rs.getDouble(4));
					ticket.setInTime(rs.getTimestamp(5).toLocalDateTime());
					ticket.setOutTime(rs.getTimestamp(6) == null ? null : rs.getTimestamp(6).toLocalDateTime());
					ticket.setFareRate(rs.getDouble(7));
				}
				
				// Checks the attributes of the ticket
				assertEquals(1, rs.getInt(1));
				assertEquals(1, ticket.getId());
				assertEquals(1, ticket.getParkingSpot().getId());
				assertEquals("ABCDEF", ticket.getVehicleRegNumber());
				assertNotNull(ticket.getInTime());
				assertNotNull(ticket.getOutTime());
				assertEquals(0.0, ticket.getPrice());
				assertEquals(1.0, ticket.getFareRate());

				// Checks the attributes of the parkingSpot
				assertEquals(1, parkingSpot.getId());
				assertTrue(parkingSpot.isAvailable());
				assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
      
				dataBaseTestConfig.closePreparedStatement(psGet);
				dataBaseTestConfig.closeResultSet(rs);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dataBaseTestConfig.closeConnection(connection);
			}
	}
	
	@Test
	void exitingCarMore24hIT() {

//		// TODO: check that the fare generated and out time are populated correctly in
//		// the database
	
////      testParkingACar(); Mauvaise pratique ! 

		Connection connection = null;
		Ticket ticket = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		try {
			connection = dataBaseTestConfig.getConnection();
		
			// Writes one line in ticket database 
			// and updates the availability of the parkingSpot  
			PreparedStatement psInsertTicket = connection.prepareStatement("INSERT INTO TICKET"
					+ " (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, FARE_RATE) "
					+ " VALUES(1,'ABCDEF',0.0,LOCALTIMESTAMP()-INTERVAL 26 HOUR,null,1)");
			psInsertTicket.execute();
			
			PreparedStatement psInsertParkingSpot = connection.prepareStatement("UPDATE PARKING "
					+ " SET AVAILABLE = false"
					+ " WHERE PARKING_NUMBER = 1");
			psInsertParkingSpot.execute();
				
			dataBaseTestConfig.closePreparedStatement(psInsertTicket);
			dataBaseTestConfig.closePreparedStatement(psInsertParkingSpot);

			try {
				ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
				
				parkingService.processExitingVehicle();

			} catch (Exception e) {
				e.printStackTrace();
			}
	        		        
				// Extracts tickets and parking attributes from database
				PreparedStatement psGet = connection
						.prepareStatement("SELECT t.ID, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, "
								+ "t.PRICE, t.IN_TIME, t.OUT_TIME, t.FARE_RATE, p.TYPE, p.AVAILABLE "
								+ "FROM TICKET t, PARKING p " + "WHERE p.PARKING_NUMBER = t.PARKING_NUMBER");

				ResultSet rs = psGet.executeQuery();

				if (rs.next()) {

					parkingSpot = new ParkingSpot(rs.getInt(2), ParkingType.valueOf(rs.getString(8)),
							rs.getBoolean(9));

					ticket = new Ticket();
					ticket.setId(rs.getInt(1));
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(rs.getString(3));
					ticket.setPrice(rs.getDouble(4));
					ticket.setInTime(rs.getTimestamp(5).toLocalDateTime());
					ticket.setOutTime(rs.getTimestamp(6) == null ? null : rs.getTimestamp(6).toLocalDateTime());
					ticket.setFareRate(rs.getDouble(7));
				}
				
				// Checks the attributes of the ticket
				assertEquals(1, rs.getInt(1));
				assertEquals(1, ticket.getId());
				assertEquals(1, ticket.getParkingSpot().getId());
				assertEquals("ABCDEF", ticket.getVehicleRegNumber());
				assertNotNull(ticket.getInTime());
				assertNotNull(ticket.getOutTime());
				assertEquals(37.5, ticket.getPrice());
				assertEquals(1.0, ticket.getFareRate());

				// Checks the attributes of the parkingSpot
				assertEquals(1, parkingSpot.getId());
				assertTrue(parkingSpot.isAvailable());
				assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
      
				dataBaseTestConfig.closePreparedStatement(psGet);
				dataBaseTestConfig.closeResultSet(rs);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dataBaseTestConfig.closeConnection(connection);
			}
	}
	
	@Test
	void exitingCarReccurentUserIT() {

//		// TODO: check that the fare generated and out time are populated correctly in
//		// the database
	
////      testParkingACar(); Mauvaise pratique ! 

		Connection connection = null;
		Ticket ticket = null;
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		try {
			connection = dataBaseTestConfig.getConnection();
		
			// Writes one line in ticket database 
			// and updates the availability of the parkingSpot  
			PreparedStatement psInsertTicket = connection.prepareStatement("INSERT INTO TICKET"
					+ " (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, FARE_RATE) "
					+ " VALUES(1,'ABCDEF',0.0,LOCALTIMESTAMP()-INTERVAL 2 HOUR,null,0.95)");
			psInsertTicket.execute();
			
			PreparedStatement psInsertParkingSpot = connection.prepareStatement("UPDATE PARKING "
					+ " SET AVAILABLE = false"
					+ " WHERE PARKING_NUMBER = 1");
			psInsertParkingSpot.execute();
				
			dataBaseTestConfig.closePreparedStatement(psInsertTicket);
			dataBaseTestConfig.closePreparedStatement(psInsertParkingSpot);

			try {
				ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
				when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
				
				parkingService.processExitingVehicle();

			} catch (Exception e) {
				e.printStackTrace();
			}
	        		        
				// Extracts tickets and parking attributes from database
				PreparedStatement psGet = connection
						.prepareStatement("SELECT t.ID, t.PARKING_NUMBER, t.VEHICLE_REG_NUMBER, "
								+ "t.PRICE, t.IN_TIME, t.OUT_TIME, t.FARE_RATE, p.TYPE, p.AVAILABLE "
								+ "FROM TICKET t, PARKING p " + "WHERE p.PARKING_NUMBER = t.PARKING_NUMBER");

				ResultSet rs = psGet.executeQuery();

				if (rs.next()) {

					parkingSpot = new ParkingSpot(rs.getInt(2), ParkingType.valueOf(rs.getString(8)),
							rs.getBoolean(9));

					ticket = new Ticket();
					ticket.setId(rs.getInt(1));
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(rs.getString(3));
					ticket.setPrice(rs.getDouble(4));
					ticket.setInTime(rs.getTimestamp(5).toLocalDateTime());
					ticket.setOutTime(rs.getTimestamp(6) == null ? null : rs.getTimestamp(6).toLocalDateTime());
					ticket.setFareRate(rs.getDouble(7));
				}
				
				// Checks the attributes of the ticket
				assertEquals(1, rs.getInt(1));
				assertEquals(1, ticket.getId());
				assertEquals(1, ticket.getParkingSpot().getId());
				assertEquals("ABCDEF", ticket.getVehicleRegNumber());
				assertNotNull(ticket.getInTime());
				assertNotNull(ticket.getOutTime());
				assertNotEquals(0.0, ticket.getPrice());
				assertEquals(1.42, ticket.getPrice());
				assertEquals(0.95, ticket.getFareRate());

				// Checks the attributes of the parkingSpot
				assertEquals(1, parkingSpot.getId());
				assertTrue(parkingSpot.isAvailable());
				assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
      
				dataBaseTestConfig.closePreparedStatement(psGet);
				dataBaseTestConfig.closeResultSet(rs);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dataBaseTestConfig.closeConnection(connection);
			}
	}
}
