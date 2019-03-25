package com.example.Counters;

import com.example.model.Location;

import java.util.List;

public class CounterPhysicalState {
    public static int countPhyscialState(List<Location> locationList){
        int count=0;
        for(Location l:locationList){
            //count+=l.getAmountOfProduct();
        }
        return count;
    }
}
