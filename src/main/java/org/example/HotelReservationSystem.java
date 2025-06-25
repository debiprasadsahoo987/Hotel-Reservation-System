package org.example;

import java.net.ConnectException;
import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "root";


    public void callHotel() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established");
            while (true) {
                showMenu(connection);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void showMenu(Connection connection) throws InterruptedException {
        System.out.println();
        System.out.println("Hotel Reservation System");
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Reserve a room");
        System.out.println("2. View Reservations");
        System.out.println("3. Get room number");
        System.out.println("4. Update Reservation");
        System.out.println("5. Delete Reservation");
        System.out.println("0. Exit");
        System.out.println("Choose an option from above: ");

        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                reserveRoom(connection, scanner);
                break;
            case 2:
                viewReservations(connection);
                break;
            case 3:
                getRoomNumber(connection, scanner);
                break;
            case 4:
                updateReservation(connection, scanner);
                break;
            case 5:
                deleteReservation(connection, scanner);
                break;
            case 0:
                exit();
                scanner.close();
                return;
            default:
                System.out.println("Invalid Choice. Try Again!!!");
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scanner.next();

            String query = "INSERT INTO reservations (guest_name, room_number, contact_number) " + "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(query);

                if (affectedRows > 0) {
                    System.out.println("Reservation Successful!!!");
                } else {
                    System.out.println("Reservation failed...");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection connection) {
        String query = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";


        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            System.out.println("Current Reservations: ");
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.print(resultSet.getString(i) + " ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter Reservation ID: ");
            int reservationID = scanner.nextInt();
            System.out.println("Enter Guest name: ");
            String guestName = scanner.next();

            String query = "SELECT room_number FROM reservations " + "WHERE reservation_id = " + reservationID + " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationID + " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given details.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateReservation (Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter the reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if(!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.println("Enter new Contact number: ");
            String newContactNumber = scanner.next();

            String query = "UPDATE reservations SET guest_name = '" + newGuestName + "', " + "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" +newContactNumber + "' " + "WHERE reservation_id = " +reservationId;

            try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(query);

                if(affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
            }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static  void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter Reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if(!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String query = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(query);

                if(affectedRows > 0 ){
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try{
            String query = "SELECT reservation_id FROM reservations WHERE resrvation_id = " + reservationId;

            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)){
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("Closed the Program!");
    }
}
