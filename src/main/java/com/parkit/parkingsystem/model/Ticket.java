package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
//    private Date inTime;
//    private Date outTime;
//    modif sur getters et setters 
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    
//    ajouté
    private double fareRate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

	/**
	 * @return the fareRate
	 */
	public double getFareRate() {
		return fareRate;
	}

	/**
	 * @param fareRate the fareRate to set
	 */
	public void setFareRate(double fareRate) {
		this.fareRate = fareRate;
	}
    
    
}
