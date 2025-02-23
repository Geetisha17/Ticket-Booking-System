package org.example.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8010;

    public void run()
    {
        try {
            Socket socket = new Socket(SERVER_ADDRESS , SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("BOOKED");

            String response = in.readLine();
            System.out.println(Thread.currentThread().getName()+" Server response "+response);

            socket.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        int clientcnt=5;

        for(int i=0;i<clientcnt;i++)
        {
            Thread clientThread = new Thread((Runnable) new Client(),"Client-"+(i+1));
            clientThread.start();
        }
    }
}