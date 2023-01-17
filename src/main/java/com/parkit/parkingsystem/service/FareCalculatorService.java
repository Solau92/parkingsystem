package com.parkit.parkingsystem.service;

import java.time.Duration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	/**
	 * Calculates the fare of a given ticket and set the price of this ticket.
	 * @param ticket
	 * @throws IllegalArgumentException if ticket out time null or ticket out time is before ticket in time
	 * @throws IllegalArgumentException if the type of parking is unknown 
	 */
	public void calculateFare(Ticket ticket) {

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double duration = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes() / 60.0;

		if (duration < 0.5) {
			ticket.setPrice(0.0);
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * ticket.getFareRate());
				break;
			}
			case BIKE: {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * ticket.getFareRate());
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}

}