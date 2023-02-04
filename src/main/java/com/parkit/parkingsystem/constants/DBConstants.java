package com.parkit.parkingsystem.constants;
/**
 * Contains all the SQL queries used in the app
 *
 */
public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String GET_PARKING_SPOT = "select p.PARKING_NUMBER, p.AVAILABLE, p.TYPE from parking p where p.parking_number =?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, FARE_RATE) values(?,?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    public static final String GET_TICKET_VEHICLE_IN_PARKING = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE, t.FARE_RATE p from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is null order by t.IN_TIME desc limit 1";
    public static final String GET_TICKET_VEHICLE_OUT_OF_PARKING = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE, t.FARE_RATE p from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? and t.OUT_TIME is not null order by t.IN_TIME desc limit 1";
    
    public static final String CHECK_CAR_ALREADY_IN_PARKING = "select count(*)  from ticket t where t.VEHICLE_REG_NUMBER=? AND t.OUT_TIME is null";
    public static final String NUMBER_TIMES_CAR_IN_PARKING = "select count(*) from ticket t where t.VEHICLE_REG_NUMBER=?";
}
