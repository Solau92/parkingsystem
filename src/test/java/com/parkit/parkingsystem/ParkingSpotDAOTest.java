package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nl.altindag.log.LogCaptor;
import nl.altindag.log.model.LogEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

	@InjectMocks
	private static ParkingSpotDAO parkingSpotDAO;

	@Mock
	private static Connection connection;
	@Mock
	private static DataBaseConfig dataBaseConfig;
	@Mock
	private static PreparedStatement preparedStatement;
	@Mock
	private static ResultSet resultSet;

	LogCaptor logCaptor = LogCaptor.forName("ParkingSpotDAO");

	@Test
	void getParkingSpot_CarSucces_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(anyInt())).thenReturn(1);
		when(resultSet.getBoolean(anyInt())).thenReturn(true);
		when(resultSet.getString(anyInt())).thenReturn("CAR");

		// WHEN
		ParkingSpot parkingSpotGet = parkingSpotDAO.getParkingSpot(1);

		// THEN
		assertEquals(1, parkingSpotGet.getId());
		assertEquals(true, parkingSpotGet.isAvailable());
		assertEquals(ParkingType.CAR, parkingSpotGet.getParkingType());

	}

	@Test
	void getParkingSpot_NoResult_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);

		// WHEN
		ParkingSpot parkingSpotGet = parkingSpotDAO.getParkingSpot(1);

		// THEN
		assertEquals(0, parkingSpotGet.getId());
		assertNull(parkingSpotGet.getParkingType());
		assertFalse(parkingSpotGet.isAvailable());

	}

	@Test
	void getParkingSpot_ConnectionError_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenThrow(new SQLException());

		// WHEN
		ParkingSpot parkingSpotGet = parkingSpotDAO.getParkingSpot(1);

		// THEN
		assertEquals(0, parkingSpotGet.getId());
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Error fetching parking spot", logEvent.getMessage());

	}

	@Test
	void getNextAvailableSlot_CarSucces_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);

		when(resultSet.getInt(anyInt())).thenReturn(1);

		// WHEN
		int spotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertEquals(1, spotNumber);
	}

	@Test
	void getNextAvailableSlot_BikeSucces_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(true);
		when(resultSet.getInt(anyInt())).thenReturn(4);

		// WHEN
		int spotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertEquals(4, spotNumber);
	}

	@Test
	void getNextAvailableSlot_CarNoAvailable_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenThrow(new SQLException());

		// WHEN
		int spotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertEquals(-1, spotNumber);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Error fetching next available slot", logEvent.getMessage());
	}

	@Test
	void getNextAvailableSlot_CarNoResult_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);
		when(resultSet.next()).thenReturn(false);

		// WHEN
		int spotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// THEN
		assertEquals(-1, spotNumber);
	}

	@Test
	void upDateParking_BikeSucces_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		ParkingSpot parkingSpotToUpdate = new ParkingSpot(4, ParkingType.BIKE, true);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeUpdate()).thenReturn(1);

		// WHEN
		boolean isParkingSpotUpdated = parkingSpotDAO.updateParking(parkingSpotToUpdate);

		// THEN
		assertTrue(isParkingSpotUpdated);
	}

	@Test
	void upDateParking_BikeFail_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		ParkingSpot parkingSpotToUpdate = new ParkingSpot(4, ParkingType.BIKE, true);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeUpdate()).thenReturn(0);

		// WHEN
		boolean isParkingSpotUpdated = parkingSpotDAO.updateParking(parkingSpotToUpdate);

		// THEN
		assertFalse(isParkingSpotUpdated);
	}

	@Test
	void upDateParking_CarError_Test() throws ClassNotFoundException, SQLException {

		// GIVEN
		ParkingSpot parkingSpotToUpdate = new ParkingSpot(4, ParkingType.BIKE, true);
		when(dataBaseConfig.getConnection()).thenReturn(connection);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

		// WHEN
		boolean isParkingSpotUpdated = parkingSpotDAO.updateParking(parkingSpotToUpdate);

		// THEN
		assertFalse(isParkingSpotUpdated);
		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Error updating parking info", logEvent.getMessage());
	}
}
