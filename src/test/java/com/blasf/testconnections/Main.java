package com.blasf.testconnections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("192.168.0.108", 8080);
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        dos.writeUTF("RUNwinver");
        dos.flush();
        s.close();
    }
}
