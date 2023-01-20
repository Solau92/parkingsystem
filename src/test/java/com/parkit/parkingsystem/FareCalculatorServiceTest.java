package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
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
	public void calculateFareCarOneHourTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareBikeOneHourTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareUnkownTypeTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN

		// THEN
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
		
		// Comment tester le default et donc IllegalArgumentException de calculateFare ??
	}

	@Test
	public void calculateFareCarWithFutureInTimeTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().plusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN

		// THEN
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));

	}
	@Test
	public void calculateFareBikeWithFutureInTimeTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().plusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN

		// THEN
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));

	}

	@Test
	public void calculateFareBikeWithLessThanOneHourAndMoreThan30MinParkingTimeTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), 0.75 * Fare.BIKE_RATE_PER_HOUR);

	}

	@Test
	public void calculateFareCarWithLessThanOneHourAndMoreThan30MinParkingTimeTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), 0.75 * Fare.CAR_RATE_PER_HOUR);

	}

	@Test
	public void calculateFareCarWithLessThan30MinParkingTimeTest() {

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
		assertEquals(ticket.getPrice(), 0.0);
	}

	@Test
	public void calculateFareBikeWithLessThan30MinParkingTimeTest() {

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
		assertEquals(ticket.getPrice(), 0.0);
	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTimeTest() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusDays(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(1);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), 24 * Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareCarRecurentUser() {

		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		// ?? ok et faudra tester aussi le setFareRate ??
		ticket.setFareRate(0.95);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), 0.95 * Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareBikeRecurentUserTest() {
		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setFareRate(0.95);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), 0.95 * Fare.BIKE_RATE_PER_HOUR);
	}
}
