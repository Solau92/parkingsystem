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
import java.util.Date;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
//    private static void setUp() {
	public static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
//    private void setUpPerTest() {
	public void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void calculateFareCar() {

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
	public void calculateFareBike() {

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
//    	Date inTime = new Date();
//        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
//        Date outTime = new Date();
//        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
//
//        ticket.setInTime(inTime);
//        ticket.setOutTime(outTime);
//        ticket.setParkingSpot(parkingSpot);
//        fareCalculatorService.calculateFare(ticket);
//        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareUnkownType() {

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

//    	Date inTime = new Date();
//        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
//        Date outTime = new Date();
//        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
//
//        ticket.setInTime(inTime);
//        ticket.setOutTime(outTime);
//        ticket.setParkingSpot(parkingSpot);
//        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithFutureInTime() {

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

//        Date inTime = new Date();
//        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
//        Date outTime = new Date();
//        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
//
//        ticket.setInTime(inTime);
//        ticket.setOutTime(outTime);
//        ticket.setParkingSpot(parkingSpot);
//        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithLessThanOneHourAndMoreThan30MinParkingTime() {

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

//    	Date inTime = new Date();
//        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
//        Date outTime = new Date();
//        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
//
//        ticket.setInTime(inTime);
//        ticket.setOutTime(outTime);
//        ticket.setParkingSpot(parkingSpot);
//        fareCalculatorService.calculateFare(ticket);
//        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
	}

	@Test
	public void calculateFareCarWithLessThanOneHourAndMoreThan30MinParkingTime() {

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

//    	Date inTime = new Date();
//        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
//        Date outTime = new Date();
//        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
//
//        ticket.setInTime(inTime);
//        ticket.setOutTime(outTime);
//        ticket.setParkingSpot(parkingSpot);
//        fareCalculatorService.calculateFare(ticket);
//        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThan30MinParkingTime() {

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
	public void calculateFareBikeWithLessThan30MinParkingTime() {

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
	public void calculateFareCarWithMoreThanADayParkingTime() {

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

//    	Date inTime = new Date();
//        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
//        Date outTime = new Date();
//        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
//
//        ticket.setInTime(inTime);
//        ticket.setOutTime(outTime);
//        ticket.setParkingSpot(parkingSpot);
//        fareCalculatorService.calculateFare(ticket);
//        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
	}

	@Test
	public void calculateFareCarRecurentUser() {

//		fail("Not yet implemented");

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
	public void calculateFareBikeRecurentUser() {
		// GIVEN
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime outTime = LocalDateTime.now();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		// ?? ok et faudra tester aussi le "setFareRate" ??
		ticket.setFareRate(0.95);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), 0.95 * Fare.BIKE_RATE_PER_HOUR);
	}
}
