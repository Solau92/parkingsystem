package com.parkit.parkingsystem.service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
//        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

//        int inHour = ticket.getInTime().getHours();
//        int outHour = ticket.getOutTime().getHours();

        long diffMinutes = ChronoUnit.MINUTES.between(ticket.getInTime(), ticket.getOutTime());
        //long duration = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes();
        double duration = diffMinutes / 60.0 ;
          
        //TODO: Some tests are failing here. Need to check if this logic is correct

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
               ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}