package org.example;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ReservationsMain {

    static final String JDBC_URL = "jdbc:mysql://localhost/reservation_system";
    static final String USER = "root@localhost";
    static final String PASSWORD = "password";

    public static void main(String[] args) {
        try(Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            ReservationSystem reservationSystem = new ReservationSystem();

            reservationSystem.makeReservation(connection);
            reservationSystem.cancelReservation(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
