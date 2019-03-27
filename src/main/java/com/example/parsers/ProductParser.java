package com.example.parsers;

import com.example.model.Order;
import com.example.model.Product;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductParser {

    public static  Product productJSONParser(String product ){
        JSONObject jsonObject = new JSONObject(product);

        Gson g = new Gson();
        Product o = g.fromJson(product, Product.class);

        return o;
    }

    public static List<Product> productParser(String productArray) {
        List<Product>productList=new ArrayList<>();
        JSONArray jsonArray = new JSONArray(productArray);

        for (int i = 0; i < jsonArray.length(); i++) {
            Gson g = new Gson();
            Product p = g.fromJson( jsonArray.getJSONObject(i).toString(), Product.class);
            System.out.println(p.getId());
            productList.add(p);

        }
        return productList;
    }
}
