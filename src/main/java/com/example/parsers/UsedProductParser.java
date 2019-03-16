package com.example.parsers;

import com.example.model.Product;
import com.example.model.UsedProduct;
import com.google.gson.Gson;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class UsedProductParser {
    public static List<UsedProduct> usedProductParser(String productArray){

        List<UsedProduct> productList=new ArrayList<>();
        JSONArray jsonArray = new JSONArray(productArray);

        for (int i = 0; i < jsonArray.length(); i++) {
            Gson g = new Gson();
            UsedProduct p = g.fromJson( jsonArray.getJSONObject(i).toString(), UsedProduct.class);

            productList.add(p);

        }
        return productList;

    }
}
