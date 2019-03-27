package com.example.parsers;

import com.example.model.Principal;
import com.example.model.Supply;
import com.google.gson.Gson;
import org.json.JSONObject;

public class SupplyParser {
    public static Supply supplyParser(String supplyString) {
        Gson g = new Gson();
        Supply p = g.fromJson(supplyString, Supply.class);
        return p;
    }

}
