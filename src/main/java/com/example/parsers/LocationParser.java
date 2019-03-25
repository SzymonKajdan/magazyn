package com.example.parsers;


import com.example.model.Location;
import com.google.gson.Gson;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class LocationParser {
    public static List<Location> locationParser(String locationArray){

        List<Location> locationList=new ArrayList<>();
        JSONArray jsonArray = new JSONArray(locationArray);

        for (int i = 0; i < jsonArray.length(); i++) {
            Gson g = new Gson();
            Location l = g.fromJson( jsonArray.getJSONObject(i).toString(), Location.class);

            locationList.add(l);

        }
        return locationList;

    }
}
