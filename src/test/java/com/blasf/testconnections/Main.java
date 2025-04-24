package com.blasf.testconnections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        for(int i = 0; i < 10; i++) {
            Socket s = new Socket("192.168.1.130", 8080);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            dos.writeUTF("RUNecho a");
            dos.flush();
            s.close();
        }
    }
}
