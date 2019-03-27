package com.example.parsers;

import com.example.model.StaticProduct;
import com.google.gson.Gson;


public class StaticPorductPraser {
    public static StaticProduct staticProductParser(String stp) {
        Gson g = new Gson();
        StaticProduct sp = g.fromJson(stp, StaticProduct.class);
        return sp;
    }
}
