package com.example.parsers;

import com.example.model.Palette;
import com.example.model.Product;
import com.google.gson.Gson;
import org.json.JSONObject;

public class PaletteParser {
    public static Palette paletteParser(String palleteString){


        Gson g = new Gson();
        Palette o = g.fromJson(palleteString, Palette.class);

        return o;
    }
}
