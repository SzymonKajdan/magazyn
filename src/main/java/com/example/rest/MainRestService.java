package com.example.rest;

import com.example.model.*;
import com.example.parsers.ProductParser;
import com.example.repository.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.parsers.OrderParser.orderParser;
import static com.example.parsers.PrincipalParser.*;
import static com.example.parsers.ProductParser.productParser;
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

    @RequestMapping(path = "/createOrder", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody String orderRequest) {

        JSONObject jsonOrder = new JSONObject(orderRequest);
        Principal principal = principalParser(jsonOrder.get("principal").toString());

        Order order = new Order();
        order.setDate(new Date());
        order.setPrincipal(principalRepository.findByNip(principal.getNip()));

        List<UsedProduct> productList = usedProductParser(jsonOrder.getJSONArray("product").toString());
        if (chcekState(productList) == false) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("status", "failed");
            return ResponseEntity.ok(jsonObject.toString());
        } else {


            order.setPrice(countTheAmountOfTheOrder(productList));
            order.setUsedProductList(productList);

            return sendGoodCreateOrderResponse(order, productList);
        }
    }

    private ResponseEntity<?> sendGoodCreateOrderResponse(Order order, List<UsedProduct> productList) {
        usedProductRepository.saveAll(productList);
        orderRepository.save(order);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", order.getId());
        jsonObject.put("status", "success");
        return ResponseEntity.ok(jsonObject.toString());
    }


    @RequestMapping(path = "/editOrder", method = RequestMethod.POST)
    public ResponseEntity<?> editOrder(@RequestBody String orderRequest) {

        JSONObject jsonOrder = new JSONObject(orderRequest);
        //  System.out.println(jsonOrder.get("order"));

        Order order = orderRepository.getOne(orderParser(jsonOrder.get("order").toString()).getId());
        //System.out.println(order.getId());
        List<UsedProduct> productList = usedProductParser(jsonOrder.getJSONArray("product").toString());

        if (chcekEditOrder(order, productList) == true) {


            //usedProductRepository.deleteAll(order.getUsedProductList());

            List<UsedProduct>usedProductListToDelete=order.getUsedProductList();
            order.setUsedProductList(null);
            usedProductRepository.deleteAll(usedProductListToDelete);
            order.setUsedProductList(productList);
            order.setPrice(countTheAmountOfTheOrder(productList));
            return sendGoodCreateOrderResponse(order, productList);
        } else {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("status", "failed");
            return ResponseEntity.ok(jsonObject.toString());
        }
    }

    private boolean chcekEditOrder(Order order, List<UsedProduct> productList) {
        for (UsedProduct olditem : order.getUsedProductList()) {

            for (UsedProduct newitem : productList) {

                if (newitem.getIdproduct() == olditem.getIdproduct()) {


                    List<UsedProduct> usedProductList = new ArrayList<>();
                    usedProductList.add(newitem);
                    System.out.println("jestem tutaj ");
                    if (newitem.getQuanitity() == olditem.getQuanitity()) {
                    } else if (chcekState(usedProductList) == false) {
                        return false;

                    }
                }
            }

        }
        return true;
    }

    private double countTheAmountOfTheOrder(List<UsedProduct> productList) {
        double price = 0;
        for (UsedProduct p : productList) {
            Product product = productRepository.getOne(p.getIdproduct());
            if (product != null) {
                price += product.getPrice() * p.getQuanitity();
            }
        }
        price += price * 0.23;
        return price;
    }

    private boolean chcekState(List<UsedProduct> usedProducts) {


        for (UsedProduct p : usedProducts) {
            int requestAmount = 0;
            requestAmount = p.getQuanitity();
            Product product = productRepository.getOne(p.getIdproduct());
            int magazineAmount = 0;
            for (Location location : product.getLocation()) {
                magazineAmount += location.getAmountOfProduct();
            }
            if (requestAmount > magazineAmount) {
                return false;
            }
        }
        return true;
    }
}
