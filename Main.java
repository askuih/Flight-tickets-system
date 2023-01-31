package org.example;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        DB db = new DB();
        Connection connection = db.getConnection();

        String username;
        String password;
        char userChoice;
        boolean canUserLogin = false;

        Authorisation authorisation;
        do {
            System.out.println("Enter 'Y' if you have an account, enter 'N' you don't");
            userChoice = scanner.next().toUpperCase().charAt(0);

            System.out.println("Enter your username: ");
            username = scanner.next();
            System.out.println("Enter your password: ");
            password = scanner.next();

            authorisation = new Authorisation(connection, username, password);

            switch (userChoice) {
                case 'Y' -> {
                    canUserLogin = authorisation.authorizeUser();
                    if (canUserLogin) {
                        System.out.println("Successful login as " + username + "!");
                    } else {
                        System.out.println("Invalid login or password... Try again!");
                        authorisation = null;
                    }
                }
                case 'N' -> {
                    canUserLogin = authorisation.registerUser();
                    if (canUserLogin) {
                        System.out.println("Account successfully created! Logging in...");
                    } else {
                        System.out.println("Failed to create an account...");
                        authorisation = null;
                    }
                }
            }
        } while ((userChoice != 'N' && userChoice != 'Y') || !canUserLogin);

        boolean isUserAdmin = authorisation.isUserAdmin();

        do {
            System.out.println("1 - View existing flights");
            System.out.println("2 - Modify flight");
            System.out.println("3 - Create flight");
            System.out.println("4 - Delete flight");
            System.out.println("0 - Exit");

            userChoice = scanner.next().toUpperCase().charAt(0);

            if (isUserAdmin) {
                switch (userChoice) {
                    case '1' -> db.viewFlights();
                    case '2' -> db.modifyFlight();
                    case '3' -> db.createFlight();
                    case '4' -> db.deleteFlight();
                }
            } else {
                switch (userChoice) {
                    case '1' -> db.viewFlights();
                }
            }
        } while (userChoice != '0');

        db.closeConnection();
    }
}