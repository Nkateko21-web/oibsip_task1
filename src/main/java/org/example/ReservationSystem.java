package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Scanner;


public class ReservationSystem {
    static final String JDBC_URL = "jdbc:mysql://localhost/reservation_system";
    static final String USER = "root@localhost";
    static final String PASSWORD = "password";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            createTables(connection);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("1. Login");
                System.out.println("2. Reservation");
                System.out.println("3. Cancellation");
                System.out.println("4. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        login(connection);
                        break;
                    case 2:
                        makeReservation(connection);
                        break;
                    case 3:
                        cancelReservation(connection);
                        break;
                    case 4:
                        System.out.println("Exiting the system. Goodbye");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS reservations ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL,"
                    + "password VARCHAR(60) NOT NULL,"
                    + "train_number VARCHAR(10) NOT NULL,"
                    + "class_type VARCHAR(10) NOT NULL,"
                    + "date_of_journey DATE NOT NULL,"
                    + "source VARCHAR(50) NOT NULL,"
                    + "destination VARCHAR(50) NOT NULL,"
                    + "pnr_number VARCHAR(10) NOT NULL"
                    + ")";
            statement.executeUpdate(createTableQuery);
        }
    }

    public static void makeReservation(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your username: ");
            String username = scanner.next();
            System.out.println("Enter your password: ");
            String password = scanner.next();
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("Enter train number: ");
            String trainNumber = scanner.next();
            System.out.println("Enter class: ");
            String classType = scanner.next();
            System.out.println("Enter date of journey (yyyy-mm-dd): ");
            String dateOfJourney = scanner.next();
            System.out.println("Enter source: ");
            String source = scanner.next();
            System.out.println("Enter destination: ");
            String destination = scanner.next();
            String pnrNumber = generatePNR();

            String insertQuery = "INSERT INTO reservations (username, password, train_number, class_type, date_of_journey, source, destination, pnr_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, passwordHash);
                preparedStatement.setString(3, trainNumber);
                preparedStatement.setString(4, classType);
                preparedStatement.setString(5, dateOfJourney);
                preparedStatement.setString(6, source);
                preparedStatement.setString(7, destination);
                preparedStatement.setString(8, pnrNumber);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Reservation successful! Your PNR number is: " + pnrNumber);
                } else {
                    System.out.println("Reservation failed. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cancelReservation(Connection connection) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Please enter your pnr number: ");
            String pnrNumber = scanner.next();

            String selectQuery = "SELECT * FROM reservations WHERE pnr_number= ?";
            String deleteQuery = "DELETE FROM reservations WHERE pnr_number = ?";

            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                 PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                selectStatement.setString(1, pnrNumber);

                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    System.out.println("Reservation details:");
                    System.out.println("Username: " + resultSet.getString("username"));
                    System.out.println("Train number: " + resultSet.getString("train_number"));
                    System.out.println("Class type: " + resultSet.getString("class_type"));
                    System.out.println("Date of journey: " + resultSet.getString("date_of_journey"));
                    System.out.println("Source: " + resultSet.getString("source"));
                    System.out.println("Destination: " + resultSet.getString("destination"));

                    System.out.println("Do you want to cancel this reservation? (yes/no): ");
                    String confirm = scanner.next().toLowerCase();

                    if (confirm.equals("yes")) {
                        deleteStatement.setString(1, pnrNumber);
                        int rowsAffected = deleteStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("Reservation canceled successfully!");
                        } else {
                            System.out.println("Cancellation failed. Please try again.");
                        }
                    } else {
                        System.out.println("Reservation not canceled.");
                    }
                } else {
                    System.out.println("No reservation found with the provided PNR number.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generatePNR() {
        String generatedPnrNumber = String.valueOf((int)(Math.random() * 100000));
        return generatedPnrNumber;
    }

    public static void login(Connection connection) {

        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your username: ");
            String username = scanner.next();
            System.out.println("Enter your password: ");
            String password = scanner.next();

            String selectQuery = "SELECT * FROM reservations WHERE username = ?";

            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setString(1, username);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    String hashedPasswordFromDB = resultSet.getString("password");

                    // verify the entered password against the stored hash
                    if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                        System.out.println("Login successful!");

                    } else {
                        System.out.println("Login failed, incorrect password.");
                    }
                } else {
                    System.out.println("User not found. Please check your username.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
