package org.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BookingHandler{
    private final Socket clientSocket;

    public BookingHandler(Socket socket)
    {
        this.clientSocket=socket;
    }
    public void run()
    {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

            String request = in.readLine();
            System.out.println("Recieved Request "+request);

            if("BOOKED".equalsIgnoreCase(request))
            {
                out.println("Booking confirmed");
            }else{
                out.println("Invalid request");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}