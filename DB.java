package org.example;

import java.sql.*;
import java.util.Scanner;

public class DB {

    private Connection connection = null;

    public DB() {
        final String connectionUrl = "jdbc:postgresql://localhost:5432/homework31012023";

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(connectionUrl, "postgres", "123456");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    protected void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    protected void deleteFlight() {
        Scanner scanner = new Scanner(System.in);

        viewFlights();

        System.out.println("Enter flight id to delete or enter 0 to exit");
        int flight_id = scanner.nextInt();
        if (flight_id == 0) {
            return;
        }

        String query = "DELETE FROM tb_flights WHERE id = ?";
        int records = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, flight_id);
            records = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        if (records == 1) {
            System.out.println("Successfully deleted flight #" + flight_id + "!");
        }
    }

    protected void modifyFlight() {
        Scanner scanner = new Scanner(System.in);

        viewFlights();
        viewCities();

        System.out.println("Enter flight id to modify or enter 0 to exit");
        int flight_id = scanner.nextInt();
        if (flight_id == 0) {
            return;
        }
        System.out.println("Choose departure city id for flight #" + flight_id);
        int departure_city_id = scanner.nextInt();
        System.out.println("Choose arrival city id for flight #" + flight_id);
        int arrival_city_id = scanner.nextInt();
        String query = """
                UPDATE tb_flights
                   SET departure_city_id = ?, arrival_city_id = ?
                 WHERE id = ?""";
        int records = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, departure_city_id);
            preparedStatement.setInt(2, arrival_city_id);
            preparedStatement.setInt(3, flight_id);
            records = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
        if (records == 1) {
            System.out.println("Successfully modified flight #" + flight_id + "!");
            System.out.println("Set departure city to ");
        }
    }

    protected void viewCities() {
        ResultSet resultSet;
        String query;
        try {
            query = "SELECT * FROM tb_cities";
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            System.out.println("Available cities: ");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id") + " - " + resultSet.getString("name"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    protected void createFlight() {
        // TODO: Add hours and minutes
        Scanner scanner = new Scanner(System.in);
        String query;

        viewCities();
        try {
            System.out.println("Enter id of a city to depart from: ");
            int departureCityId = scanner.nextInt();
            System.out.println("Enter date of departure (yyyy-MM-dd): ");
            Date departureDateTime = Date.valueOf(scanner.next());
            System.out.println("Enter id of a city to arrive to: ");
            int arrivalCityId = scanner.nextInt();
            System.out.println("Enter date of arrival (yyyy-MM-dd): ");
            Date arrivalDateTime = Date.valueOf(scanner.next());
            query = """
                    INSERT INTO tb_flights (id, departure_city_id, departure_time, arrival_city_id, arrival_time)
                    VALUES ((SELECT COALESCE(MAX(id) + 1, 1) FROM tb_flights), ?, ?, ?, ?)""";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, departureCityId);
            preparedStatement.setDate(2, departureDateTime);
            preparedStatement.setInt(3, arrivalCityId);
            preparedStatement.setDate(4, arrivalDateTime);
            int records = preparedStatement.executeUpdate();
            if (records == 1) {
                System.out.println("Flight successfully added!");
            } else {
                System.out.println("An error occurred...");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    protected void viewFlights() {
        String query = """
                SELECT f.id, cd.name departure_city, f.departure_time, ca.name arrival_city, f.arrival_time
                  FROM tb_flights f, tb_cities cd, tb_cities ca
                 WHERE f.departure_city_id = cd.id
                   AND f.arrival_city_id = ca.id""";
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                System.out.println("Flight #" + resultSet.getInt("id") +
                                " from " + resultSet.getString("departure_city") +
                                " at " + resultSet.getDate("departure_time") + " " + resultSet.getTime("departure_time") +
                                " to " + resultSet.getString("arrival_city") +
                                " at " + resultSet.getDate("arrival_time") + " " + resultSet.getTime("arrival_time"));
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
