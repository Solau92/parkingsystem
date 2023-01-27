package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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

	/* *************************************** 
	 * Tests processIncomingVehicle
	 ****************************************/

	@Test
	void processIncomingVehicle_NoSpotAvailable_Test() {
		
		// Cas où parkingSpot = null || parkingSpot.getId <= 0
		try {
			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(2);

			when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(logger);

			// WHEN

			// THEN
			parkingService.processIncomingVehicle();
			verify(logger, Mockito.times(2)).error(anyString(), any(Throwable.class));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	void processIncomingVehicle_CarOk_Test() {

		try {
			when(inputReaderUtil.readSelection()).thenReturn(1);

			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			parkingService.processIncomingVehicle();
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void processIncomingVehicle_CarUpdateError_Test() {

		try {
			when(inputReaderUtil.readSelection()).thenReturn(1);

			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

			parkingService.processIncomingVehicle();
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void processIncomingVehicle_WrongVehicleTypeInput_Test() {

		try {
			when(inputReaderUtil.readSelection()).thenReturn(3);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(logger);

			parkingService.processIncomingVehicle();
			verify(logger, Mockito.times(2)).error(anyString(), any(Throwable.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void processIncomingVehicle_VehicleAlreadyInParking_Test() {

		try {
			when(inputReaderUtil.readSelection()).thenReturn(1);

			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(ticketDAO.isVehicleAlreadyInParkingInDataBase(anyString())).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(logger);

			parkingService.processIncomingVehicle();
			verify(logger, Mockito.times(1)).error(anyString(), any(Throwable.class));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void processIncomingVehicle_VehicleAlreadyParkedInParking_Test() {

		try {

			when(inputReaderUtil.readSelection()).thenReturn(1);

			when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
			when(ticketDAO.numberOfTimesVehicleInDataBase(anyString())).thenReturn(2);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(logger);

			parkingService.processIncomingVehicle();
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * *************************************** 
	 * Tests processExitingVehicle
	 ****************************************/

	public void setUpPerNormalTest() {

		try {
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

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	// Checks that the method updateParking was use once in "normal" case
	@Test
	void processExitingVehicle_Ok_Test() {
		setUpPerNormalTest();
		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	public void setUpPerTicketNullTest() {

		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			when(ticketDAO.getTicket(anyString())).thenReturn(null);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(logger);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	void processExitingVehicle_TicketNull_Test() {
		setUpPerTicketNullTest();
		parkingService.processExitingVehicle();
		verify(logger, Mockito.times(1)).error(anyString(), any(Throwable.class));
	}

	public void setUpPerUpDateTicketFalseTest() {

		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			LocalDateTime inTime = LocalDateTime.now().minusHours(1);
			ticket.setInTime(inTime);
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			ticket.setFareRate(1.0);
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.setLogger(logger);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	void processExitingVehicle_PbUpdateTicket_Test() {
		setUpPerUpDateTicketFalseTest();
		parkingService.processExitingVehicle();
		verify(logger, Mockito.times(1)).error(anyString(), any(Throwable.class));
	}

}
