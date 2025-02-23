package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.UserBookingService;
import org.example.util.UserServiceUtil;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Running Train Booking System");

        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;

        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("There is something wrong with the system.");
            ex.printStackTrace();
            return;
        }

        while (option != 7) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");
            System.out.print("Enter your choice: ");

            // if (!scanner.hasNext()) {
            //     System.out.println("No input available. Exiting...");
            //     break;
            // }

            // String input = scanner.nextLine().trim();

            // if (input.isEmpty()) {
            //     System.out.println("No input detected. Please try again.");
            //     continue;
            // }

            // try {
            //     option = Integer.parseInt(input);
            // } catch (NumberFormatException e) {
            //     System.out.println("Invalid input. Please enter a number between 1 and 7.");
            //     continue;
            // }
            option = scanner.nextInt();

            Train trainSelectedForBooking = new Train();

            switch (option) {
                case 1:
                    System.out.print("Enter username to sign up: ");
                    if (!scanner.hasNextLine()) break;
                    String nameToSignUp = scanner.nextLine().trim();

                    System.out.print("Enter password to sign up: ");
                    if (!scanner.hasNextLine()) break;
                    String passwordToSignUp = scanner.nextLine().trim();

                    User userToSignup = new User(
                            nameToSignUp, passwordToSignUp,
                            UserServiceUtil.hashPassword(passwordToSignUp),
                            new ArrayList<>(), UUID.randomUUID().toString());

                    userBookingService.signUp(userToSignup);
                    System.out.println("Sign-up successful!");
                    break;

                case 2:
                    System.out.print("Enter username to login: ");
                    if (!scanner.hasNextLine()) break;
                    String nameToLogin = scanner.nextLine().trim();

                    System.out.print("Enter password: ");
                    if (!scanner.hasNextLine()) break;
                    String passwordToLogin = scanner.nextLine().trim();

                    User userToLogin = new User(
                            nameToLogin, passwordToLogin,
                            UserServiceUtil.hashPassword(passwordToLogin),
                            new ArrayList<>(), UUID.randomUUID().toString());

                    try {
                        userBookingService = new UserBookingService(userToLogin);
                        System.out.println("Login successful!");
                    } catch (IOException ex) {
                        System.out.println("Login failed.");
                    }
                    break;

                case 3:
                    System.out.println("Fetching your bookings...");
                    userBookingService.fetchBookings();
                    break;

                case 4:
                    System.out.print("Enter source station: ");
                    if (!scanner.hasNextLine()) break;
                    String source = scanner.nextLine().trim();

                    System.out.print("Enter destination station: ");
                    if (!scanner.hasNextLine()) break;
                    String dest = scanner.nextLine().trim();

                    List<Train> trains = userBookingService.getTrains(source, dest);

                    if (trains.isEmpty()) {
                        System.out.println("No trains available for the selected route.");
                        break;
                    }

                    System.out.println("\nAvailable Trains:");
                    for (int i = 0; i < trains.size(); i++) {
                        Train t = trains.get(i);
                        System.out.println((i + 1) + ". Train ID: " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("   Station: " + entry.getKey() + " Time: " + entry.getValue());
                        }
                    }

                    System.out.print("Select a train (Enter number): ");
                    if (!scanner.hasNextLine()) break;

                    try {
                        int trainIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                        if (trainIndex < 0 || trainIndex >= trains.size()) {
                            System.out.println("Invalid train selection.");
                            break;
                        }
                        trainSelectedForBooking = trains.get(trainIndex);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid train number.");
                    }
                    break;

                case 5:
                    System.out.println("Select a seat from the available seats:");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);

                    for (List<Integer> row : seats) {
                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }
                        System.out.println();
                    }

                    System.out.print("Enter the row number: ");
                    if (!scanner.hasNextLine()) break;

                    System.out.print("Enter the column number: ");
                    if (!scanner.hasNextLine()) break;

                    try {
                        int row = Integer.parseInt(scanner.nextLine().trim());
                        int col = Integer.parseInt(scanner.nextLine().trim());

                        System.out.println("Booking your seat...");
                        boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);

                        if (booked) {
                            System.out.println("Seat booked! Enjoy your journey.");
                        } else {
                            System.out.println("Seat booking failed.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter valid seat numbers.");
                    }
                    break;

                case 6:
                    System.out.print("Enter your ticket ID to cancel: ");
                    if (!scanner.hasNextLine()) break;
                    String ticketId = scanner.nextLine().trim();

                    if (ticketId.isEmpty()) {
                        System.out.println("Invalid ticket ID.");
                        break;
                    }

                    boolean cancelled = userBookingService.cancelBooking(ticketId);
                    if (cancelled) {
                        System.out.println("Your ticket has been successfully cancelled.");
                    } else {
                        System.out.println("Your ticket cannot be cancelled.");
                    }
                    break;

                case 7:
                    System.out.println("Exiting the app...");
                    break;

                default:
                    System.out.println("Invalid option. Please choose a number between 1 and 7.");
                    break;
            }
        }

        scanner.close();
    }
}
