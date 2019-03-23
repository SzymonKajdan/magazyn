package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

import static com.example.Counters.CounterPhysicalState.countPhyscialState;
import static com.example.parsers.OrderParser.orderParser;
import static com.example.parsers.PrincipalParser.*;
import static com.example.parsers.UsedProductParser.usedProductParser;

@RestController
public class MainRestService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PrincipalRepository principalRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UsedProductRepository usedProductRepository;

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public ResponseEntity<?> getAllOrdersOrderByDateAsc() {
        return ResponseEntity.ok(orderRepository.findAllByOrderByDate());
    }

    @RequestMapping(path = "/orders2", method = RequestMethod.GET)
    public ResponseEntity<?> getAllOrdersOrderByDateDsc() {
        return ResponseEntity.ok(orderRepository.findAllByOrderByDateDsc());
    }


    @RequestMapping(path = "/findProductByName", method = RequestMethod.POST)
    public ResponseEntity<?> findProductByName(@RequestBody String productRequest) {

        JSONObject jsonObject = new JSONObject(productRequest);

        List<Product> productList = productRepository.findByName(jsonObject.get("name").toString());


        return getProductsResponseEntity(productList);
    }

    @RequestMapping(path = "/findProductByProducer", method = RequestMethod.POST)
    public ResponseEntity<?> findProductByProducer(@RequestBody String productRequest) {

        JSONObject jsonObject = new JSONObject(productRequest);

        List<Product> productList = productRepository.findByProducer(jsonObject.get("producer").toString());


        return getProductsResponseEntity(productList);
    }

    @RequestMapping(path = "/findProductByBarcode", method = RequestMethod.POST)
    public ResponseEntity<?> findProductByBarcode(@RequestBody String productRequest) {

        JSONObject jsonObject = new JSONObject(productRequest);

        Product product = productRepository.findByBarCode(jsonObject.get("barcode").toString());

        JSONObject tmpJsonObejct = new JSONObject();
        if (product != null) {


            createJsonResponse(product, tmpJsonObejct);


        } else {
            tmpJsonObejct.put("error", "noProductInWarehouse");
        }
        return ResponseEntity.ok(tmpJsonObejct.toString());
    }

    private ResponseEntity<?> getProductsResponseEntity(List<Product> productList) {
        JSONArray jsonArrayToResponse = new JSONArray();
        if (productList.size() != 0) {
            for (Product p : productList) {
                JSONObject tmpJsonObejct = new JSONObject();
                createJsonResponse(p, tmpJsonObejct);
                jsonArrayToResponse.put(tmpJsonObejct);
            }

        } else {
            jsonArrayToResponse.put(new JSONObject().put("error", "noProductInWarehouse"));
        }
        return ResponseEntity.ok(jsonArrayToResponse.toString());
    }


    private void createJsonResponse(Product product, JSONObject tmpJsonObejct) {
        int logicAmount = product.getLogicState();
        tmpJsonObejct.put("logicState", logicAmount);

        int phsycialState = countPhyscialState(product.getLocations());
        tmpJsonObejct.put("physicalSate", phsycialState);
        tmpJsonObejct.put("name", product.getName());
        tmpJsonObejct.put("location", product.getLogicState());
        tmpJsonObejct.put("barcode", product.getBarCode());
        tmpJsonObejct.put("exprDate", product.getExprDate());
        tmpJsonObejct.put("producer", product.getProducer());
        tmpJsonObejct.put("id", product.getId());
        tmpJsonObejct.put("price", product.getPrice());
    }


}
