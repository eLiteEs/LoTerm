package com.blasf.testconnections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        //for (int i = 0; i < 10; i++) {
            try (Socket socket = new Socket("127.0.1.1", 7575)) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("RUNmank -s");
                dos.flush();
            } catch (IOException e) {
                System.err.println("Failed to send command " + 0 + ": " + e.getMessage());
            }
        //}
    }
}
