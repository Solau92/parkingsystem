package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.constants.Rate;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;
	
	@BeforeAll
	public static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	public void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	void calculateFare_CarOneHour_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	void calculateFare_BikeOneHour_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	void calculateFare_BikeTicketOutTimeNull_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = null;
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN - THEN
		assertThrows(Exception.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	void calculateFare_NullType_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN - THEN
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));		
	}
	
	@Test
	void calculateFare_CarWithFutureInTime_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().plusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN - THEN
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}
	
	@Test
	void calculateFare_BikeWithFutureInTime_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().plusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN - THEN
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	void calculateFare_BikeWithLessThanOneHourAndMoreThan30MinParkingTime_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(0.75 * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	void calculateFare_CarWithLessThanOneHourAndMoreThan30MinParkingTime_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(Math.round(0.75 * Fare.CAR_RATE_PER_HOUR*100.0)/100.0, ticket.getPrice());		
	}

	@Test
	void calculateFare_CarWithLessThan30MinParkingTime_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(0.0, ticket.getPrice());
	}

	@Test
	void calculateFare_BikeWithLessThan30MinParkingTime_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusMinutes(15);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(0.0, ticket.getPrice());
	}

	@Test
	void calculateFare_CarWithMoreThanADayParkingTime_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusDays(2);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.NORMAL_USER);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(48 * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
	}

	@Test
	void calculateFare_CarRecurentUser_Test() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.RECURRENT_USER);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(Math.round(Rate.RECURRENT_USER * Fare.CAR_RATE_PER_HOUR*100.0)/100.0, ticket.getPrice());		
	}

	@Test
	void calculateFare_BikeRecurentUser_Test() {
		
		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(Rate.RECURRENT_USER);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(Rate.RECURRENT_USER * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
	}
}
