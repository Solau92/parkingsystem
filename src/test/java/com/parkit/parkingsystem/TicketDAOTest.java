package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import nl.altindag.log.LogCaptor;
import nl.altindag.log.model.LogEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

	@InjectMocks
	private static TicketDAO ticketDAO;

	@Mock
	private static Connection connection;
	@Mock
	private static DataBaseConfig dataBaseConfig;
	@Mock
	private static PreparedStatement preparedStatement;
	@Mock
	private static ResultSet resultSet;	
 
	LogCaptor logCaptor = LogCaptor.forName("TicketDAO");

	// *****************//
	// Tests saveTicket //
	// *****************//
	
	@Test
	void saveTicket_CarSucces_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("WXYZ");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.execute()).thenReturn(true);
		
		//WHEN
		boolean isTicketSaved = ticketDAO.saveTicket(ticketToSave);
		
		//THEN
		assertTrue(isTicketSaved);
	}

	@Test
	void saveTicket_CarSuccessOutTimeNull_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("WXYZ");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(null);
		ticketToSave.setFareRate(1D);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.execute()).thenReturn(true);
		
		//WHEN
		boolean isTicketSaved = ticketDAO.saveTicket(ticketToSave);
		
		//THEN
		assertTrue(isTicketSaved);
	}
	
	@Test
	void saveTicket_BikeError_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("WXYZ");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		
		when(dataBaseConfig.getConnection()).thenThrow(new SQLException());

		//WHEN
		boolean isTicketSaved = ticketDAO.saveTicket(ticketToSave);
		
		//THEN
		assertFalse(isTicketSaved);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error saving ticket", logEvent.getMessage());
	}

	// *******************//
	// Tests updateTicket //
	// *******************//
	
	@Test
	void updateTicket_CarSucces_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("WXYZ");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.execute()).thenReturn(true);
		
		//WHEN
		boolean isTicketUpdated = ticketDAO.updateTicket(ticketToSave);
		
		//THEN
		assertTrue(isTicketUpdated);
	}

	@Test
	void updateTicket_CarError_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("WXYZ");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		
		when(dataBaseConfig.getConnection()).thenThrow(new SQLException());
		
		//WHEN
		boolean isTicketUpdated = ticketDAO.updateTicket(ticketToSave);
		
		//THEN
		assertFalse(isTicketUpdated);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error updating ticket", logEvent.getMessage());
	}
	
	// ****************//
	// Tests getTicket //
	// ****************//
	
	@Test
	void getTicket_CarSuccesOutTimeNotNull_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getString(anyInt())).thenReturn("CAR");
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getDouble(anyInt())).thenReturn(3.14);
		when(resultSet.getTimestamp(anyInt())).thenReturn(new Timestamp(0));
		
		//WHEN
		Ticket ticketGetTicket = ticketDAO.getTicket("WXYZ");
				
		//THEN
		assertEquals(1, ticketGetTicket.getId());
		assertEquals("WXYZ", ticketGetTicket.getVehicleRegNumber());
		assertEquals(3.14, ticketGetTicket.getPrice());
	}

	@Test
	void getTicket_CarSuccesOutTimeNull_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getString(anyInt())).thenReturn("CAR");
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getDouble(anyInt())).thenReturn(3.14);
		when(resultSet.getTimestamp(anyInt())).thenReturn(null);
		
		//WHEN
		Ticket ticketGetTicket = ticketDAO.getTicket("WXYZ");
				
		//THEN
		assertEquals(1, ticketGetTicket.getId());
		assertEquals("WXYZ", ticketGetTicket.getVehicleRegNumber());
		assertEquals(3.14, ticketGetTicket.getPrice());
	}

	@Test
	void getTicket_Error_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);
		
		//WHEN
		Ticket ticketGetTicket = ticketDAO.getTicket("WXYZ");
				
		//THEN
		assertNull(ticketGetTicket);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error fetching the ticket", logEvent.getMessage());
	}

	// ***********************************//
	// Tests getTicketVehicleNotInParking //
	// ***********************************//
	
	@Test
	void getTicketVehicleNotInParking_CarSuccesOutTimeNotNull_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getString(anyInt())).thenReturn("CAR");
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getDouble(anyInt())).thenReturn(3.14);
		when(resultSet.getTimestamp(anyInt())).thenReturn(new Timestamp(0));
		
		//WHEN
		Ticket ticketGetTicket = ticketDAO.getTicketVehicleNotInParking("WXYZ");
				
		//THEN
		assertEquals(1, ticketGetTicket.getId());
		assertEquals("WXYZ", ticketGetTicket.getVehicleRegNumber());
		assertEquals(3.14, ticketGetTicket.getPrice());
	}

	@Test
	void getTicketVehicleNotInParking_Error_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);
		
		//WHEN
		Ticket ticketGetTicket = ticketDAO.getTicketVehicleNotInParking("WXYZ");
				
		//THEN
		assertNull(ticketGetTicket);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error fetching the ticket", logEvent.getMessage());
	}	

	// ******************************************//
	// Tests isVehicleAlreadyInParkingInDataBase //
	// ******************************************//
	
	@Test
	void isVehicleAlreadyInParkingInDataBase_No_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(1)).thenReturn(0);
		
		//WHEN
		boolean isVehicleIsInParking = ticketDAO.isVehicleAlreadyInParkingInDataBase("WXYZ");
		
		//THEN
		assertFalse(isVehicleIsInParking);
	}

	@Test
	void isVehicleAlreadyInParkingInDataBase_Yes_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(1)).thenReturn(1);

		//WHEN
		boolean isVehicleIsInParking = ticketDAO.isVehicleAlreadyInParkingInDataBase("WXYZ");
		
		//THEN
		assertTrue(isVehicleIsInParking);
	}
	
	@Test
	void isVehicleAlreadyInParkingInDataBase_MoreThanOneResult_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(1)).thenReturn(2);
				
		//WHEN
		boolean isVehicleAlreadyInParking = ticketDAO.isVehicleAlreadyInParkingInDataBase("WXYZ");
		
		//THEN
		assertTrue(isVehicleAlreadyInParking);
	}

	@Test
	void isVehicleAlreadyInParkingInDataBase_SQLNoResult_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);
				
		//WHEN
		boolean isVehicleAlreadyInParking = ticketDAO.isVehicleAlreadyInParkingInDataBase("WXYZ");
		
		//THEN
		assertTrue(isVehicleAlreadyInParking);
	}
	
	@Test
	void isVehicleAlreadyInParkingInDataBase_ConnectionError_Test() throws ClassNotFoundException, SQLException {

		//GIVEN		
		when(dataBaseConfig.getConnection()).thenReturn(null);
		
		//WHEN
		boolean isVehicleAlreadyInParking = ticketDAO.isVehicleAlreadyInParkingInDataBase(null);
		
		//THEN
		assertTrue(isVehicleAlreadyInParking);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error checking vehicle in already in parking", logEvent.getMessage());	
	}
	
	// *************************************//
	// Tests numberOfTimesVehicleInDataBase //
	// *************************************//
	
	@Test
	void numberOfTimesVehicleInDataBase_OneOrMore_Test() throws ClassNotFoundException, SQLException {

		//GIVEN		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(1)).thenReturn(3);
		
		//WHEN
		int numberOfTimes = ticketDAO.numberOfTimesVehicleInDataBase("WXYZ");
		
		//THEN
		assertEquals(3, numberOfTimes);
	}
	
	@Test
	void numberOfTimesVehicleInDataBase_Zero_Test() throws ClassNotFoundException, SQLException {

		//GIVEN		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);
		
		//WHEN
		int numberOfTimes = ticketDAO.numberOfTimesVehicleInDataBase("WXYZ");
		
		//THEN
		assertEquals(0, numberOfTimes);
	}

	@Test
	void numberOfTimesVehicleInDataBase_Error_Test() throws ClassNotFoundException, SQLException {

		//GIVEN		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenThrow(new SQLException());
		
		//WHEN
		int numberOfTimes = ticketDAO.numberOfTimesVehicleInDataBase("WXYZ");
		
		//THEN
		assertEquals(0, numberOfTimes);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error extracting number of times vehicule in database", logEvent.getMessage());
	}
}