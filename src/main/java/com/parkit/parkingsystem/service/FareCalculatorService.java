package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
//        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

//        int inHour = ticket.getInTime().getHours();
//        int outHour = ticket.getOutTime().getHours();

		// 1st
//        long diffMinutes = ChronoUnit.MINUTES.between(ticket.getInTime(), ticket.getOutTime());
//        double duration = diffMinutes / 60.0 ;

		// clément
//        long durationbis = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes();
//        mais donne des minutes, pas une fraction d'heure, donc revient au même que ChronoUnit ? 

//        System.out.println("diff min : " + diffMinutes + "duration : " + duration);
//        System.out.println("durationbis : " + durationbis);

		double duration = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes() / 60.0;
		//
		boolean utilisateurRecurent = isUtilisateurReccurent(ticket.getVehicleRegNumber());

		// TODO: Some tests are failing here. Need to check if this logic is correct

		if (duration < 0.5) {
			ticket.setPrice(0.0);
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				if (utilisateurRecurent) {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * 0.95);
				} else {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				}
				break;
			}
			case BIKE: {
				if (utilisateurRecurent) {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * 0.95);
				} else {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}

	private boolean isUtilisateurReccurent(String vehicleRegNumber) {

		return false;

	}

}