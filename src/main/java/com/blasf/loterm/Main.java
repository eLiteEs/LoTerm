package com.blasf.loterm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Main {
    private static boolean showCommands = false;
    private static boolean showLogs = true;
    
    private static void log(String text) { // Function for logging messages
        if(showLogs) { // Check if the user wants to see the logs
            System.out.println(text); // Show the message
        }
    }
    
    private static void err(String text) { // Function for logging errors
        if(showLogs) { // Check if the user wants to see the logs
            System.err.println(text); // Show the error
        }
    }
    
    public static void main(String[] args) {
        int port = 8080; // Use 4040 as default port
        String dir = "C:\\Users\\" + System.getProperty("user.name") + "\\"; // Directory in which the commands are going to be run

        if(args.length >= 1) { // Check if the user introduced a port
            try { // Check if the user introduced a valid port
                port = Integer.parseInt(args[0]); // Save the port to the port variable
                if(port <= 0 || port >= 9999) { // Check if the port is outside 0 or 9999 (invalid port)
                    err("Error: The specified port isn't available, using default 4040."); // Log the error
                    port = 4040; // Return to the default port
                }
            } catch (NumberFormatException e) { // The console argument isn't a number
                err("Error: The specified port isn't available, using default 4040."); // Log the error
            }
        }

        if(args.length >= 2) { // Check if the user wants to show the commands to be run
            if(Objects.equals(args[1], "yes")) { // The user said yes
                showCommands = true;
            }
        }

        if(args.length == 3) { // Check if the user wants to show the logs
            if(Objects.equals(args[2], "no")) { // The user said no
                showLogs = false;
            }
        }

        log("Started server at port: " + port); // Show the port of the server

        boolean exit = false; // Flag for closing the program

        while(!exit) {
            try {
                ServerSocket ss = new ServerSocket(port); // Create a server with that port
                log("Opened port " + port + ". Waiting for connection..."); // Log the status

                Socket s = ss.accept(); // Wait until a socket is sent to the server
                log("Connection received."); // Log status

                DataInputStream dis = new DataInputStream(s.getInputStream()); // Get the InputStream of the server for reading the request

                String request = dis.readUTF(); // Read the request
                log("Received: " + request); // Show the request

                ss.close(); // Close the server
                s.close(); // Close the socket
                dis.close(); // Close the reader

                // Now process the command
                if(request.startsWith("RUN")) {
                    String command = request.substring(3); // Get the command of the request

                    if(showCommands) { // The user wants to see the commands being runned
                        System.out.println(command); // Show the command
                    }

                    ProcessBuilder processBuilder = new ProcessBuilder(); // Create a process builder
                    processBuilder.command(command.split(" ")); // Set the command on the process builder
                    processBuilder.directory(new File(dir)); // Set the running directory for running the command
                    try {
                        Process process = processBuilder.start(); // Start the command
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // Get the reader of the process

                        // Read line by line the output
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }

                        process.waitFor(); // Wait to the command to finish
                    } catch (IOException | InterruptedException e) {
                        err("Error: \n" + e.getMessage()); // Display the error
                    }
                } else if(request.startsWith("EXIT")) { // Exit the program
                    exit = true; // Turn on the flag to exit the loop
                } else if(request.startsWith("MOVE")) { // Change the current directory
                    String newDir = request.substring(4); // Get the new directory

                    if(!new File(newDir).exists()) { // Check if the directory doesn't exist
                        err("Directory " + newDir + " doesn't exists, not changing it."); // Log the error
                        continue; // Skip the change code
                    }

                    dir = newDir; // Change the current directory
                } else { // Command couldn't be found
                    err("Unknown command."); // Log the error
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
