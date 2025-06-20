package com.blasf.testconnections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            try (Socket socket = new Socket("192.168.0.108", 8080)) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("RUNecho Connection " + i);
                dos.flush();
            } catch (IOException e) {
                System.err.println("Failed to send command " + i + ": " + e.getMessage());
            }
        }
    }
}
