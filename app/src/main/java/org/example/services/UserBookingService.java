package org.example.services;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

public class UserBookingService{
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private List<User>userList;
    private User user;
    private final String USER_FILE_PATH = "C:/Users/Lenovo/Desktop/Spring/Project/app/src/main/java/org/example/localdb/user.json";

    public UserBookingService(User user) throws IOException
    {
        this.user = user;
        loadUsers();
    }
    public UserBookingService() throws IOException{
        loadUsers();
    }

    private void loadUsers() throws IOException{
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>(){});
    }

    public Boolean loginUser(String inputName, String password)
    {
        Optional<User> foundUser = userList.stream().filter(user->{
            return user.getName().equalsIgnoreCase(inputName) && UserServiceUtil.checkPassword(password, user.getHashPassword());
        }).findFirst();
        return foundUser.isPresent();
    }
    public Boolean signUp(User user) throws IOException  
    {
        if(userList == null)
        {throw new IOException("User List is not initilaised");}

        userList.add(user);
        saveUsers();
        return Boolean.TRUE;
    }
    public void saveUsers() throws IOException{
        objectMapper.writeValue(new File(USER_FILE_PATH), userList);
    }

    public void fetchBookings()
    {
        Optional<User> userfetched = userList.stream().filter(user->{
            return user.getName().equalsIgnoreCase(this.user.getName()) && UserServiceUtil.checkPassword(this.user.getPassword(),user.getHashPassword());
        }).findFirst();
        if(userfetched.isPresent())
        userfetched.get().printTickets();
    }

    public Boolean cancelBooking(String ticketId)
    {
        if(ticketId == null || ticketId.isEmpty())
        {
            System.out.println("Ticket cannot be Null");
            return Boolean.FALSE;
        }

        boolean removed = this.user.getTicketsBooked().removeIf(ticket->ticket.getTicketId().equals(ticketId));

        if(removed)
        {
            System.out.println("Ticket Id "+ ticketId +" has been removed");
            return true;
        }
        else{
            System.out.println("ticket Id "+ ticketId +" cant be removed");
            return false;
        }
    }
    
    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public List<Train> getTrains(String src,String dst)
    {
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(src,dst);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    public Boolean bookTrainSeat(Train train,int r,int seat)
    {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();

            if(r>=0 && r<=seats.size() && seat>=0 && seat<=seats.get(r).size())
            {
                if(seats.get(r).get(seat)==0)
                {
                    seats.get(r).set(seat,1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch(IOException e)
        {
            return false;
        }
    }
}