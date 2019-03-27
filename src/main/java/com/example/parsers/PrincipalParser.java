package com.example.parsers;

import com.example.model.Principal;
import com.google.gson.Gson;
import org.json.JSONObject;


public class PrincipalParser {
    public static Principal principalParser(String principalString) {

        Gson g = new Gson();
        Principal p = g.fromJson(principalString, Principal.class);
        return p;
    }
}
