package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.constants.Rate;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;
import nl.altindag.log.model.LogEvent;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;
	@Mock
	private static Logger logger;

	@Captor
	ArgumentCaptor<Ticket> ticketCaptor;

	@Captor
	ArgumentCaptor<ParkingSpot> parkingSpotCaptor;

	LogCaptor logCaptor = LogCaptor.forName("ParkingService");

	// *****************************//
	// Tests processIncomingVehicle //
	// *****************************//

	@Test
	void processIncomingVehicle_CarOk_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true); // *******************
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).saveTicket(ticketCaptor.capture());
		Ticket ticketSaved = ticketCaptor.getValue();
		assertEquals("ABCDEF", ticketSaved.getVehicleRegNumber());
	}

	@Test
	void processIncomingVehicle_BikeOk_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSpot(ParkingType.BIKE)).thenReturn(4);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true); // *******************
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).saveTicket(ticketCaptor.capture());
		Ticket ticketSaved = ticketCaptor.getValue();
		assertEquals("ABCDEF", ticketSaved.getVehicleRegNumber());
	}

	@Test
	void processIncomingVehicle_NoSpotAvailable_Test() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSpot(any(ParkingType.class))).thenReturn(-1);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(0)).saveTicket(ticketCaptor.capture());

		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(2, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Error fetching next available parking slot", logEvent.getMessage());
		logEvent = logEvents.get(1);
		assertEquals("Unable to process incoming vehicle", logEvent.getMessage());
	}

	@Test
	void processIncomingVehicle_CarUpdateError_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(0)).saveTicket(ticketCaptor.capture());

		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Unable to process incoming vehicle", logEvent.getMessage());
	}

	@Test
	void processIncomingVehicle_WrongVehicleTypeInput_Test() {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(3);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(0)).saveTicket(ticketCaptor.capture());

		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(2, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Error parsing user input for type of vehicle", logEvent.getMessage());
		logEvent = logEvents.get(1);
		assertEquals("Unable to process incoming vehicle", logEvent.getMessage());

	}

	@Test
	void processIncomingVehicle_CarVehicleAlreadyInParking_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR)).thenReturn(4);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.isVehicleAlreadyInParkingInDataBase(anyString())).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(0)).saveTicket(ticketCaptor.capture());

		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Unable to process incoming vehicle", logEvent.getMessage());
	}

	@Test
	void processIncomingVehicle_BikeVehicleAlreadyParkedInParkingBefore_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.numberOfTimesVehicleInDataBase(anyString())).thenReturn(2);
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).saveTicket(ticketCaptor.capture());
		Ticket ticketSaved = ticketCaptor.getValue();
		assertEquals(Rate.RECURRENT_USER, ticketSaved.getFareRate());
	}

	@Test
	void processIncomingVehicle_BikeVehicleNeverParkedInParkingBefore_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.numberOfTimesVehicleInDataBase(anyString())).thenReturn(0);
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).saveTicket(ticketCaptor.capture());
		Ticket ticketSaved = ticketCaptor.getValue();
		assertEquals(Rate.NORMAL_USER, ticketSaved.getFareRate());
	}

	// ****************************//
	// Tests processExitingVehicle //
	// ****************************//

	@Test
	void processExitingVehicle_CarOk_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setFareRate(1.0);

		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processExitingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(parkingSpotCaptor.capture());
		ParkingSpot parkingSpotUpdated = parkingSpotCaptor.getValue();
		assertEquals(true, parkingSpotUpdated.isAvailable());

		verify(ticketDAO, Mockito.times(1)).updateTicket(ticketCaptor.capture());
		Ticket ticketUpdated = ticketCaptor.getValue();
		assertNotNull(ticketUpdated.getOutTime());
	}

	@Test
	void processExitingVehicle_TicketNull_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket(anyString())).thenReturn(null);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processExitingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(parkingSpotCaptor.capture());
		verify(ticketDAO, Mockito.times(0)).updateTicket(ticketCaptor.capture());

		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Unable to process exiting vehicle", logEvent.getMessage());
	}

	@Test
	void processExitingVehicle_BikePbUpdateTicket_Test() throws Exception {

		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setFareRate(1.0);
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// WHEN
		parkingService.processExitingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(0)).updateParking(parkingSpotCaptor.capture());
		verify(ticketDAO, Mockito.times(1)).updateTicket(ticketCaptor.capture());

		List<LogEvent> logEvents = logCaptor.getLogEvents();
		assertEquals(1, logEvents.size());
		LogEvent logEvent = logEvents.get(0);
		assertEquals("Unable to process exiting vehicle", logEvent.getMessage());
	}

}
