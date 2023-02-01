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

	@Test
	void saveTicket_Succes_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("TOTO");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.execute()).thenReturn(true);
		
		//WHEN
		boolean result = ticketDAO.saveTicket(ticketToSave);
		
		//THEN
		assertTrue(result);
	}

	@Test
	void saveTicket_outTimeNull_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("TOTO");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(null);
		ticketToSave.setFareRate(1D);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.execute()).thenReturn(true);
		
		//WHEN
		boolean result = ticketDAO.saveTicket(ticketToSave);
		
		//THEN
		assertTrue(result);
	}
	
	@Test
	void saveTicket_Error_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("TOTO");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		
		when(dataBaseConfig.getConnection()).thenThrow(new SQLException());

		//WHEN
		ticketDAO.saveTicket(ticketToSave);
		
		//THEN
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error connection ?", logEvent.getMessage());

	}
		
	@Test
	void updateTicket_Succes_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("TOTO");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.execute()).thenReturn(true);
		
		//WHEN
		Boolean result = ticketDAO.updateTicket(ticketToSave);
		
		//THEN
		assertTrue(result);
	}

	@Test
	void updateTicket_Error_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		Ticket ticketToSave = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticketToSave.setParkingSpot(parkingSpot);
		ticketToSave.setVehicleRegNumber("TOTO");
		ticketToSave.setPrice(2D);
		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
		ticketToSave.setFareRate(1D);
		
		when(dataBaseConfig.getConnection()).thenThrow(new SQLException());
		
		//WHEN
		Boolean result = ticketDAO.updateTicket(ticketToSave);
		
		//THEN
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error saving ticket info", logEvent.getMessage());
	}
//	@Test
//	void updateTicketErrorTest() throws SQLException, ClassNotFoundException {
//		
//		//GIVEN
//		Ticket ticketToSave = new Ticket();
//		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
//		ticketToSave.setParkingSpot(parkingSpot);
//		ticketToSave.setVehicleRegNumber("TOTO");
//		ticketToSave.setPrice(2D);
//		ticketToSave.setInTime(LocalDateTime.now().minusMinutes(15L));
//		ticketToSave.setOutTime(LocalDateTime.now().plusMinutes(5L));
//		ticketToSave.setFareRate(1D);
//		
//		when(dataBaseConfig.getConnection()).thenThrow(new SQLException());
//		
//		//WHEN
//		Boolean result = ticketDAO.updateTicket(ticketToSave);
//		
//		//THEN
//		assertFalse(result);
//	}
//
	
	// **-**
	@Test
	void getTicket_SuccesOutTimeNotNull_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getString(anyInt())).thenReturn("CAR");
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getDouble(anyInt())).thenReturn(3.14);
		// ?? **-** Rajout Time // Mais comment faire in et out différents ?
		when(resultSet.getTimestamp(anyInt())).thenReturn(new Timestamp(0));
		
		//WHEN
		Ticket ticketGetTicket = ticketDAO.getTicket("TOTO");
				
		//THEN
		assertEquals(1, ticketGetTicket.getId());
		assertEquals("TOTO", ticketGetTicket.getVehicleRegNumber());
		assertEquals(3.14, ticketGetTicket.getPrice());
	}

	// **-**
	@Test
	void getTicket_SuccesOutTimeNull_Test() throws SQLException, ClassNotFoundException {
		
		//GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getString(anyInt())).thenReturn("CAR");
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getDouble(anyInt())).thenReturn(3.14);
		// ??
		when(resultSet.getTimestamp(anyInt())).thenReturn(null);
		
		//WHEN
		Ticket ticketGetTicket = ticketDAO.getTicket("TOTO");
				
		//THEN
		assertEquals(1, ticketGetTicket.getId());
		assertEquals("TOTO", ticketGetTicket.getVehicleRegNumber());
		assertEquals(3.14, ticketGetTicket.getPrice());
	}
	
	@Test
	void isVehicleAlreadyInParkingInDataBase_Yes_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(1)).thenReturn(0);
		
		//WHEN
		boolean result = ticketDAO.isVehicleAlreadyInParkingInDataBase("ABCDEF");
		
		//THEN
		assertFalse(result);
	}

	@Test
	void isVehicleAlreadyInParkingInDataBase_No_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);
		
		//WHEN
		boolean result = ticketDAO.isVehicleAlreadyInParkingInDataBase("AAAAAA");
		
		//THEN
		assertTrue(result);
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
		boolean result = ticketDAO.isVehicleAlreadyInParkingInDataBase("AAAAAA");
		
		//THEN
		assertTrue(result);
	}
	
	@Test
	void isVehicleAlreadyInParkingInDataBase_ConnectionError_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		
		when(dataBaseConfig.getConnection()).thenReturn(null);
		
		//WHEN
		ticketDAO.isVehicleAlreadyInParkingInDataBase(null);
		
		//THEN
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error connection", logEvent.getMessage());
	
	}
	
	@Test
	void numberOfTimesVehicleInDataBase_OneOrMore_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(1)).thenReturn(3);
		
		//WHEN
		int result = ticketDAO.numberOfTimesVehicleInDataBase("ABCDEF");
		
		//THEN
		assertEquals(3, result);
	}
	
	@Test
	void numberOfTimesVehicleInDataBase_Zero_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);
		
		//WHEN
		int result = ticketDAO.numberOfTimesVehicleInDataBase("ABCDEF");
		
		//THEN
		assertEquals(0, result);
	}

	@Test
	void numberOfTimesVehicleInDataBase_Error_Test() throws ClassNotFoundException, SQLException {

		//GIVEN
		
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenThrow(new SQLException());
		
		//WHEN
		ticketDAO.numberOfTimesVehicleInDataBase("ABCDEF");
		
		//THEN
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		
        LogEvent logEvent = logEvents.get(0);
		assertEquals("Error", logEvent.getMessage());
	}
}