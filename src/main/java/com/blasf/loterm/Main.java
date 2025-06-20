package com.blasf.loterm; // Define the package name

import java.io.*; // Import I/O classes for reading/writing
import java.net.InetAddress; // For getting IP address
import java.net.ServerSocket; // For server socket operations
import java.net.Socket; // For handling client sockets
import java.net.UnknownHostException; // For catching unknown host exceptions
import java.util.concurrent.BlockingQueue; // For thread-safe command queue
import java.util.concurrent.LinkedBlockingQueue; // Concrete implementation of BlockingQueue
import java.util.concurrent.atomic.AtomicReference; // Mutable container for the current working directory

public class Main { // Main class definition

    private static boolean showCommands = false; // Whether to display commands being run
    private static boolean showLogs = true; // Whether to display logs in the console

    // Function for logging messages to standard output
    private static void log(String text) {
        if (showLogs) { // Only log if logging is enabled
            System.out.println(text); // Print message
        }
    }

    // Function for logging errors to standard error
    private static void err(String text) {
        if (showLogs) { // Only log errors if logging is enabled
            System.err.println(text); // Print error message
        }
    }

    // Method to get the local IP address of the server
    public static String getLocalIPAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost(); // Try to get local host address
            return localHost.getHostAddress(); // Return the IP address as string
        } catch (UnknownHostException e) { // Catch unknown host exception
            err("Error getting IP: " + e.getMessage()); // Log error message
            return null; // Return null if error occurs
        }
    }

    public static void main(String[] args) { // Main method, entry point

        int port = 4040; // Default port set to 4040

        // AtomicReference used to store the current working directory
        AtomicReference<String> dir = new AtomicReference<>("C:\\Users\\" + System.getProperty("user.name") + "\\");

        // If a port number is passed as first argument
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]); // Try to parse the port
                if (port <= 0 || port >= 9999) { // Validate the port range
                    err("Error: The specified port isn't available, using default 4040."); // Log if invalid
                    port = 4040; // Reset to default port
                }
            } catch (NumberFormatException e) { // Catch parsing error
                err("Error: Invalid port specified, using default 4040."); // Log and revert to default
                port = 4040; // Reset port
            }
        }

        // Check if second argument is used to enable command logging
        if (args.length >= 2) {
            if ("yes".equalsIgnoreCase(args[1])) { // If "yes", enable command logging
                showCommands = true;
            }
        }

        // Check if third argument disables logs
        if (args.length == 3) {
            if ("no".equalsIgnoreCase(args[2])) { // If "no", disable logs
                showLogs = false;
            }
        }

        // Log startup info: port and IP address
        log("Started server at port: " + port + " and IPv4: " + getLocalIPAddress());

        // BlockingQueue for handling commands in order
        BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

        // Boolean flag to tell threads when to stop (wrapped in array to mutate inside lambda)
        boolean[] exitFlag = {false};

        // Thread to process commands from the queue
        Thread commandHandler = new Thread(() -> {
            while (!exitFlag[0]) { // Keep running unless told to exit
                try {
                    String request = commandQueue.take(); // Block until a command is available

                    if (request.startsWith("RUN")) { // If command is to run something
                        String command = request.substring(3).trim(); // Extract the command after "RUN"

                        if (showCommands) { // Show command if enabled
                            log("Running: " + command);
                        }

                        // Use cmd.exe to run command through Windows shell
                        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
                        processBuilder.directory(new File(dir.get())); // Set working directory
                        processBuilder.redirectErrorStream(true); // Combine stdout and stderr

                        try {
                            Process process = processBuilder.start(); // Start the process

                            // Read the output of the command
                            try (InputStream is = process.getInputStream()) {
                                byte[] buffer = new byte[1024]; // Create buffer
                                int bytesRead;
                                while ((bytesRead = is.read(buffer)) != -1) { // Read until end
                                    System.out.write(buffer, 0, bytesRead); // Output to console
                                }
                                System.out.flush(); // Ensure output is written
                            }

                            process.waitFor(); // Wait for process to finish

                        } catch (IOException | InterruptedException e) { // Handle errors
                            err("Execution error: " + e.getMessage()); // Log error
                        }

                    } else if (request.startsWith("EXIT")) { // Handle exit command
                        exitFlag[0] = true; // Set exit flag to true
                    } else if (request.startsWith("MOVE")) { // Handle directory change
                        String newDir = request.substring(4).trim(); // Extract new directory
                        if (!new File(newDir).exists()) { // Check if it exists
                            err("Directory " + newDir + " doesn't exist, not changing it."); // Log error
                        } else {
                            dir.set(newDir); // Set new directory
                            log("Changed directory to: " + newDir); // Log change
                        }

                    } else { // If unknown command
                        err("Unknown command."); // Log error
                    }

                } catch (InterruptedException e) { // Handle thread interruption
                    err("Command queue interrupted: " + e.getMessage()); // Log error
                }
            }
        });

        commandHandler.start(); // Start the command processing thread

        // Try to open the server socket on the chosen port
        try (ServerSocket ss = new ServerSocket(port)) {
            log("Opened port " + port + ". Waiting for connections..."); // Log socket creation

            // Keep accepting connections until exit command is received
            while (!exitFlag[0]) {
                // Use try-with-resources to handle client socket
                try (Socket s = ss.accept(); // Accept incoming client
                     DataInputStream dis = new DataInputStream(s.getInputStream())) { // Get input stream

                    log("Connection received."); // Log connection

                    String request = dis.readUTF(); // Read the command sent by client
                    log("Received: " + request); // Log command

                    commandQueue.put(request); // Add command to processing queue

                } catch (IOException | InterruptedException e) { // Handle exceptions
                    err("Error handling connection: " + e.getMessage()); // Log error
                }
            }

        } catch (IOException e) { // Handle failure to open server socket
            throw new RuntimeException("Failed to open server socket on port " + port, e); // Rethrow as unchecked
        }
    }
}
