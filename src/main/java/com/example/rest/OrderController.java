package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import com.example.security.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.example.parsers.LocationParser.locationParser;
import static com.example.parsers.OrderParser.orderParser;
import static com.example.parsers.OrderParser.orderToJson;
import static com.example.parsers.PrincipalParser.principalParser;
import static com.example.parsers.UsedProductParser.usedProductParser;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class OrderController {
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
    @Autowired
    LocationRepository locationRepository;

    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public ResponseEntity<?> getAllOrdersOrderByDateAsc() {
        return ResponseEntity.ok(orderRepository.findAllByOrderByDate());
    }

    @RequestMapping(path = "/order/find", method = RequestMethod.POST)
    public ResponseEntity<?> getOrder(@RequestBody String request) {

        JSONObject jsonOrder = new JSONObject(request);



        //return ResponseEntity.ok(orderToJson(orderRepository.getOne(jsonOrder.getLong("id"))).toString());

        Order order = orderRepository.getOne(jsonOrder.getLong("id"));

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id",order.getId());
        jsonObject.put("user",order.getUser());
        jsonObject.put("principal",order.getPrincipal());
        //jsonObject.put("products",order.getUsedProductList());

        JSONArray products = new JSONArray();

        order.getUsedProductList().forEach((us)->{
            Map<String,Object> product = new HashMap<>();
            Product p = productRepository.getProductById(us.getIdproduct());
            //product.put("product",us);
            product.put("palletes",((double)us.getQuanitity()/(double)p.getQuantityOnThePalette()));
            product.put("productID",us.getIdproduct());
            product.put("usedProductID",us.getId());
            product.put("quantity",us.getQuanitity());
            product.put("isPicked",us.isPicked());
            products.put(product);
        });

        jsonObject.put("products",products);

        return ResponseEntity.ok(jsonObject.toString());
    }

    @RequestMapping(path = "/createOrder", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody String orderRequest) {

        JSONObject jsonOrder = new JSONObject(orderRequest);
        Principal principal = principalParser(jsonOrder.get("principal").toString());

        Order order = new Order();
        order.setDate(new Date());
        order.setPrincipal(principalRepository.findByNip(principal.getNip()));

        List<UsedProduct> productList = usedProductParser(jsonOrder.getJSONArray("product").toString());
        if (checkLogicState(productList) == false) {
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

            List<UsedProduct> usedProductListToDelete = order.getUsedProductList();
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

    @RequestMapping(path = "/startCompletingOrder", method = RequestMethod.POST)
    public ResponseEntity<?> startCompletingOrder(@RequestBody String orderRequest) {
        JSONObject userJson = new JSONObject(orderRequest);
        User user = userRepository.getOne(Long.parseLong(userJson.get("id").toString()));
        Order order = orderRepository.getOne(Long.parseLong(userJson.get("orderID").toString()));
        if (order != null) {
            order.setUser(user);
            orderRepository.save(order);
            return ResponseEntity.ok(new JSONObject().put("Status", HttpStatus.OK).toString());
        } else {
            return ResponseEntity.ok(new JSONObject().put("Status", BAD_REQUEST.toString()).toString());
        }

    }

    @RequestMapping(path = "/finishCompletingOrder", method = RequestMethod.POST)
    public ResponseEntity<?> finishCompletingOrder(@RequestBody String orderRequest) {
        JSONObject orderJson = new JSONObject(orderRequest);

        Order order = orderRepository.getOne(Long.parseLong(orderJson.get("orderID").toString()));
        List<UsedProduct> usedProductList = usedProductParser(orderJson.get("usedProduct").toString());

        if (checkTheCorrectnessOfTheProducts(usedProductList, order.getUsedProductList())) {
            System.out.println(checkTheCorrectnessOfTheProducts(usedProductList, order.getUsedProductList()));
            List<Location> locationList = locationParser(orderJson.getJSONArray("location").toString());

            changeAllState(order.getUsedProductList(), locationList);
            order.setEndDate(new Date());
            orderRepository.save(order);
            // locationRepository.saveAll(locationList);


            return ResponseEntity.ok(new JSONObject().put("Status", OK).toString());
        } else {
            return ResponseEntity.ok(new JSONObject().put("Status", BAD_REQUEST).toString());
        }


    }

    @RequestMapping(path = "/completingOrder", method = RequestMethod.POST)
    public ResponseEntity<?> completingOrder(@RequestBody String orderRequest) throws JsonProcessingException {
        JSONObject orderJson = new JSONObject(orderRequest);

        Order order = orderRepository.getOne(Long.parseLong(orderJson.get("orderID").toString()));
        List<UsedProduct> usedProductList = usedProductParser(orderJson.get("usedProduct").toString());
        List<Location> locationList = locationParser(orderJson.getJSONArray("location").toString());
        changeState(order.getUsedProductList(), locationList, usedProductList);

        orderRepository.save(order);
        ObjectMapper objectMapper=new ObjectMapper();

//        String js=objectMapper.writeValueAsString(order);
        JSONObject jsonObject = orderToJson(order);
        return ResponseEntity.ok(jsonObject.toString());
    }


    @RequestMapping(path = "/finishPicking", method = RequestMethod.POST)
    public ResponseEntity<?> finishPicking(@RequestBody String orderRequest) {
        JSONObject orderJson = new JSONObject(orderRequest);

        Order order = orderRepository.getOne(Long.parseLong(orderJson.get("orderID").toString()));
        order.setEndDate(new Date());
        orderRepository.save(order);
        return ResponseEntity.ok(new JSONObject().put("Status", "Finished").toString());
    }

    private void changeState(List<UsedProduct> usedProductsInOrder, List<Location> pickedItems, List<UsedProduct> usedProductsFromRequest) {
        changeStatusOfPick(usedProductsInOrder, usedProductsFromRequest);
        changeAllState(usedProductsInOrder, pickedItems);

    }

    private void changeStatusOfPick(List<UsedProduct> usedProductsInOrder, List<UsedProduct> usedProductsFromRequest) {
        for (UsedProduct product : usedProductsInOrder) {
            for (UsedProduct usedProductInRequest : usedProductsFromRequest) {
                if (product.getIdproduct().equals(usedProductInRequest.getIdproduct())) {
                    product.setPicked(true);
                }
            }

        }
    }


    private void changeAllState(List<UsedProduct> usedProductList, List<Location> locationList) {
        List<Product> productList = findProducts(usedProductList);
        for (Product product : productList) {
            for (Location locationInWarehouse : product.getLocations()) {
                for (Location locationFormRequest : locationList) {
                    if (locationInWarehouse.getBarCodeLocation().equals(locationFormRequest.getBarCodeLocation())) {
                        locationInWarehouse.setAmountOfProduct(locationInWarehouse.getAmountOfProduct() - locationFormRequest.getAmountOfProduct());
                    }
                }
            }

        }
        for (Product product : productList) {
            for (Location locationInWarehouse : product.getLocations()) {
                System.out.println(locationInWarehouse.getAmountOfProduct() + " " + locationInWarehouse.getId());
            }
        }
    }

    private List<Product> findProducts(List<UsedProduct> usedProductList) {
        List<Product> productList = new ArrayList<>();
        for (UsedProduct p : usedProductList) {
            productList.add(productRepository.getOne(p.getIdproduct()));
        }
        return productList;

    }

    private boolean checkTheCorrectnessOfTheProducts
            (List<UsedProduct> usedProductListFromRequest, List<UsedProduct> usedProductListFromOrder) {
        for (UsedProduct requestProduct : usedProductListFromRequest) {
            boolean isInorder = false;
            for (UsedProduct orderProduct : usedProductListFromOrder) {
                if (requestProduct.getIdproduct() == orderProduct.getIdproduct()) {
                    isInorder = true;
                }
            }
            if (isInorder == false) {
                return false;
            }
        }
        return true;
    }


    private boolean chcekEditOrder(Order order, List<UsedProduct> productList) {
        for (UsedProduct oldProduct : order.getUsedProductList()) {
            Product p = productRepository.getOne(oldProduct.getIdproduct());
            p.setLogicState(p.getLogicState() + oldProduct.getQuanitity());
            productRepository.save(p);
        }
        return checkLogicState(productList);
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

    private boolean checkLogicState(List<UsedProduct> productList) {
        boolean isgoodAmountOFproduct = true;

        for (UsedProduct p : productList) {
            int requestAmount = 0;
            requestAmount = p.getQuanitity();
            int logic = productRepository.getOne(p.getIdproduct()).getLogicState();

            if (logic >= requestAmount) {
            } else {
                isgoodAmountOFproduct = false;
                return false;
            }
        }

        if (isgoodAmountOFproduct) {
            for (UsedProduct p : productList) {

                int requestAmount = 0;
                requestAmount = p.getQuanitity();
                Product product = productRepository.getOne(p.getIdproduct());
                product.setLogicState(product.getLogicState() - p.getQuanitity());
                productRepository.save(product);

            }
            return true;
        } else {
            return false;
        }
    }


}
