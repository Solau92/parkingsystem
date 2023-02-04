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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;

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
		dataBasePrepareService.clearDataBaseEntries();
	}

	@BeforeEach
	public void setUpPerTest() throws Exception {
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
	}

	@AfterEach
	public void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	void incomingCarIT() throws ClassNotFoundException, SQLException {

		when(inputReaderUtil.readSelection()).thenReturn(1);

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1);

		// Checks the attributes of the ticket
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
	}

	@Test
	void incomingBikeIT() throws ClassNotFoundException, SQLException {

		when(inputReaderUtil.readSelection()).thenReturn(2);

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		Ticket ticket = ticketDAO.getTicket("ABCDEF");

		ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(4);

		// Checks the attributes of the ticket
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
	}

	@Test
	void exitingCarIT() throws Exception {

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		LocalDateTime inTime = LocalDateTime.of(2022, 02, 01, 11, 00, 00);
		parkingService.processIncomingVehicle(inTime);

		LocalDateTime outTime = inTime.plusHours(1);
		parkingService.processExitingVehicle(outTime);

		Ticket ticket = ticketDAO.getTicketVehicleNotInParking("ABCDEF");
		ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1);

		// Checks the attributes of the ticket
		assertEquals(1, ticket.getId());
		assertEquals(1, ticket.getParkingSpot().getId());
		assertEquals("ABCDEF", ticket.getVehicleRegNumber());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00), ticket.getInTime());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00).plusHours(1), ticket.getOutTime());
		assertEquals(1.5, ticket.getPrice());
		assertEquals(1.0, ticket.getFareRate());

		// Checks the attributes of the parkingSpot
		assertEquals(1, parkingSpot.getId());
		assertTrue(parkingSpot.isAvailable());
		assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
	}

	@Test
	void exitingBikeIT() throws Exception {

		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		LocalDateTime inTime = LocalDateTime.of(2022, 02, 01, 11, 00, 00);
		parkingService.processIncomingVehicle(inTime);

		LocalDateTime outTime = inTime.plusHours(1);
		parkingService.processExitingVehicle(outTime);

		Ticket ticket = ticketDAO.getTicketVehicleNotInParking("ABCDEF");
		ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(4);

		// Checks the attributes of the ticket
		assertEquals(1, ticket.getId());
		assertEquals(4, ticket.getParkingSpot().getId());
		assertEquals("ABCDEF", ticket.getVehicleRegNumber());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00), ticket.getInTime());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00).plusHours(1), ticket.getOutTime());
		assertEquals(1.0, ticket.getPrice());
		assertEquals(1.0, ticket.getFareRate());

		// Checks the attributes of the parkingSpot
		assertEquals(4, parkingSpot.getId());
		assertTrue(parkingSpot.isAvailable());
		assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
	}

	@Test
	void exitingCar_Less30Min_IT() throws Exception {

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		LocalDateTime inTime = LocalDateTime.of(2022, 02, 01, 11, 00, 00);
		parkingService.processIncomingVehicle(inTime);

		LocalDateTime outTime = inTime.plusMinutes(15);
		parkingService.processExitingVehicle(outTime);

		Ticket ticket = ticketDAO.getTicketVehicleNotInParking("ABCDEF");
		ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1);

		// Checks the attributes of the ticket
		assertEquals(1, ticket.getId());
		assertEquals(1, ticket.getParkingSpot().getId());
		assertEquals("ABCDEF", ticket.getVehicleRegNumber());
		assertNotNull(ticket.getInTime());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00), ticket.getInTime());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00).plusMinutes(15), ticket.getOutTime());
		assertEquals(0.0, ticket.getPrice());
		assertEquals(1.0, ticket.getFareRate());

		// Checks the attributes of the parkingSpot
		assertEquals(1, parkingSpot.getId());
		assertTrue(parkingSpot.isAvailable());
		assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
	}

	@Test
	void exitingCarMore24hIT() throws Exception {

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		LocalDateTime inTime = LocalDateTime.of(2022, 02, 01, 11, 00, 00);
		parkingService.processIncomingVehicle(inTime);

		LocalDateTime outTime = inTime.plusDays(1);
		parkingService.processExitingVehicle(outTime);

		Ticket ticket = ticketDAO.getTicketVehicleNotInParking("ABCDEF");
		ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1);

		// Checks the attributes of the ticket
		assertEquals(1, ticket.getId());
		assertEquals(1, ticket.getParkingSpot().getId());
		assertEquals("ABCDEF", ticket.getVehicleRegNumber());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00), ticket.getInTime());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00).plusDays(1), ticket.getOutTime());
		assertEquals(36.0, ticket.getPrice());
		assertEquals(1.0, ticket.getFareRate());

		// Checks the attributes of the parkingSpot
		assertEquals(1, parkingSpot.getId());
		assertTrue(parkingSpot.isAvailable());
		assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
	}

	@Test
	void exitingCarReccurentUserIT() throws Exception {

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		LocalDateTime inTime1 = LocalDateTime.of(2022, 02, 01, 11, 00, 00);
		parkingService.processIncomingVehicle(inTime1);
		LocalDateTime outTime1 = inTime1.plusHours(2);
		parkingService.processExitingVehicle(outTime1);

		parkingService.processIncomingVehicle(inTime1.plusDays(1));
		parkingService.processExitingVehicle(outTime1.plusDays(1));

		Ticket ticket = ticketDAO.getTicketVehicleNotInParking("ABCDEF");
		ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1);

		// Checks the attributes of the ticket
		assertEquals(2, ticket.getId());
		assertEquals(1, ticket.getParkingSpot().getId());
		assertEquals("ABCDEF", ticket.getVehicleRegNumber());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00).plusDays(1), ticket.getInTime());
		assertEquals(LocalDateTime.of(2022, 02, 01, 11, 00, 00).plusDays(1).plusHours(2), ticket.getOutTime());
		assertEquals(2.85, ticket.getPrice());
		assertEquals(0.95, ticket.getFareRate());

		// Checks the attributes of the parkingSpot
		assertEquals(1, parkingSpot.getId());
		assertTrue(parkingSpot.isAvailable());
		assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
	}

}
