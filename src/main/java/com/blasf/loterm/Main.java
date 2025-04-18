package com.blasf.loterm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 8080; // Use 4040 as default port
        String dir = "C:\\Users\\" + System.getProperty("user.name") + "\\"; // Directory in which the commands are going to be run

        if(args.length == 1) { // Check if the user introduced a port
            try { // Check if the user introduced a valid port
                port = Integer.parseInt(args[0]); // Save the port to the port variable
                if(port <= 0 || port >= 9999) { // Check if the port is outside 0 or 9999 (invalid port)
                    System.err.println("Error: The specified port isn't available, using default 4040."); // Log the error
                    port = 4040; // Return to the default port
                }
            } catch (NumberFormatException e) { // The console argument isn't a number
                System.err.println("Error: The specified port isn't available, using default 4040."); // Log the error
            }
        }

        System.out.println("Started server at port: " + port); // Show the port of the server

        boolean exit = false; // Flag for closing the program

        while(!exit) {
            try {
                ServerSocket ss = new ServerSocket(port); // Create a server with that port
                System.out.println("Opened port " + port + ". Waiting for connection..."); // Log the status

                Socket s = ss.accept(); // Wait until a socket is sent to the server
                System.out.println("Connection received."); // Log status

                DataInputStream dis = new DataInputStream(s.getInputStream()); // Get the InputStream of the server for reading the request

                String request = dis.readUTF(); // Read the request
                System.out.println("Received: " + request); // Show the request

                ss.close(); // Close the server
                s.close(); // Close the socket
                dis.close(); // Close the reader

                // Now process the command
                if(request.startsWith("RUN")) {
                    String command = request.substring(3);
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(command.split(" "));
                    processBuilder.directory(new File(dir));
                    StringBuilder output = new StringBuilder();
                    try {
                        Process process = processBuilder.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            output.append(line).append("\n");
                        }
                        process.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if(request.startsWith("EXIT")) {
                    exit = true;
                } else if(request.startsWith("SDIR")) {
                    String newdir = request.substring(4); // Get the new directory

                    if(!new File(newdir).exists()) { // Check if the directory doesn't exists
                        System.err.println("Directory " + newdir + " doesn't exists, not changing it."); // Log the error
                        continue; // Skip the change code
                    }

                    dir = newdir; // Change the current directory
                } else {

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
