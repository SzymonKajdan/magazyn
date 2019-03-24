package com.example.parsers;

import com.example.model.Order;
import com.example.model.Product;
import com.example.repository.ProductRepository;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderParser {

    @Autowired private static ProductRepository productRepository;

    public static Order orderParser(String orderJson) {

        JSONObject jsonObject = new JSONObject(orderJson);

        Gson g = new Gson();
        Order o = g.fromJson(orderJson, Order.class);

        return o;
    }

    public static JSONObject orderToJson(Order order){

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",order.getId());
        jsonObject.put("user",order.getUser());
        jsonObject.put("principal",order.getPrincipal());
        jsonObject.put("products",order.getUsedProductList());

//        JSONArray products = new JSONArray();
//
//        order.getUsedProductList().forEach((us)->{
//            Map<String,Object> product = new HashMap<>();
//            Product p = productRepository.getProductById(us.getIdproduct());
//            product.put("product",us);
//            product.put("palletes",((double)us.getQuanitity()/(double)p.getQuantityOnThePalette()));
//            product.put("productID",us.getIdproduct());
//            product.put("quantity",us.getQuanitity());
//            product.put("isPicked",us.isPicked());
//            products.put(product);
//        });
//
//        jsonObject.put("products",products);

        return  jsonObject;

    }


}
