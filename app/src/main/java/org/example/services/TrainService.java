package org.example.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.entities.Train;

public class TrainService{
    private List<Train>trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String TRAIN_DB_PATH = "C:/Users/Lenovo/Desktop/Spring/Project/app/src/main/java/org/example/localdb/trains.json";

    public TrainService() throws IOException{
        File trains = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>(){});
    }

    public List<Train> searchTrains(String src,String dst)
    {
        return trainList.stream().filter(train->validTrain(train, src, dst)).collect(Collectors.toList());
    }

    private boolean validTrain(Train train, String src,String dst)
    {
        List<String> stationOrder = train.getStations();
        int srcIdx = stationOrder.indexOf(src.toLowerCase());
        int dstIdx = stationOrder.indexOf(dst.toLowerCase());

        return srcIdx != -1 && dstIdx!=-1 && srcIdx < dstIdx;
    }

    public void addTrain(Train newTrain)
    {
        Optional<Train> existingTrain = trainList.stream().filter(train->train.getTrainId().equals(newTrain.getTrainId())).findFirst();

        if(existingTrain.isPresent())
        {
            System.out.println("ALready present");
        }
        else{
            trainList.add(newTrain);
            saveTrainListToFile();
        }
    }
    public void updateTrain(Train updateTrain)
    {
        OptionalInt idx = IntStream.range(0,trainList.size()).filter(i-> trainList.get(i).getTrainId().equals(updateTrain.getTrainId())).findFirst();
        
        if(idx.isPresent())
        {
            trainList.set(idx.getAsInt(),updateTrain);
            saveTrainListToFile();
        }
        else{
            addTrain(updateTrain);
        }
    }
    private void saveTrainListToFile()
    {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}