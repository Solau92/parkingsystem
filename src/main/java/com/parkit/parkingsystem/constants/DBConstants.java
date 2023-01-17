package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

//    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
//    Ajout FARE_RATE
    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, FARE_RATE) values(?,?,?,?,?,?)";
  
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";

//    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
//    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME desc limit 1";
//    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is null order by t.IN_TIME desc limit 1";
//    Ajout FARE_RATE
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE, t.FARE_RATE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is null order by t.IN_TIME desc limit 1";

// Vérifier si voiture déjà dans parking 
    public static final String CHECK_CAR_ALREADY_IN_PARKING = "select count(*)  from ticket t where t.VEHICLE_REG_NUMBER=? AND t.OUT_TIME is null";
//    public static final String CHECK_CAR_ALREADY_IN_PARKING = "select * from ticket t where t.VEHICLE_REG_NUMBER=? AND t.OUT_TIME is null";

//    // Vérifier si véhicule déjà venu
//    public static final String CHECK_VEHICULE_DEJA_VENU = "select count(*) from ticket t where t.VEHICLE_REG_NUMBER=?";
    
    // Compter le nombre de fois où le véhicule est déjà venu dans le parking
    public static final String NUMBER_TIMES_CAR_IN_PARKING = "select count(*) from ticket t where t.VEHICLE_REG_NUMBER=?";
}
