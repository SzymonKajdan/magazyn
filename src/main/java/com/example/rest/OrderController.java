package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/order")
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
    @Autowired
    StaticProductRepository staticProductRepository;

    @RequestMapping(path = "/findAllOrderByDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllOrdersOrderByDateAsc() {
        return ResponseEntity.ok(orderRepository.findAllByOrderByDate());
    }

    @RequestMapping(path = "/findAllOrderByDateDsc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllOrdersOrderByDateDsc() {
        return ResponseEntity.ok(orderRepository.findAllByOrderByDate());
    }

    @RequestMapping(path = "/findNotEndedAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getNotEndedOrdersOrderByDateAsc() {
        return ResponseEntity.ok(orderRepository.findAllByEndDateOrderByDate(null));
    }

    @RequestMapping(path = "/make", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> makeOrder(@RequestBody String request) {

        JSONObject json = new JSONObject(request);
        //HashMap<Long,Integer> productsMap = (HashMap<Long, Integer>) json.get("products");

        System.out.println("Tutaj: " + json.getJSONArray("products"));

        JSONArray productsJsonArray = json.getJSONArray("products");

        double price = 0.0;
        ArrayList<UsedProduct> usedProductArrayList = new ArrayList<>();
        ArrayList<StaticProduct> staticProductArrayList = new ArrayList<>();

        for (int i = 0; i < productsJsonArray.length(); ++i) {

            Long id = productsJsonArray.getJSONObject(i).getLong("id");
            int quantity = productsJsonArray.getJSONObject(i).getInt("quantity");

            UsedProduct usedProduct = new UsedProduct();
            StaticProduct staticProduct = staticProductRepository.findById(id).get();
            usedProduct.setIdStaticProduct(id);
            usedProduct.setQuanitity(quantity);
            usedProduct.setPicked(false);

            usedProductArrayList.add(usedProduct);

            price += staticProduct.getPrice() * quantity;

            if (staticProduct.getLogicState() - quantity > 0) {
                staticProduct.setLogicState(staticProduct.getLogicState() - quantity);
                staticProductArrayList.add(staticProduct);
            } else {
                JSONObject jo = new JSONObject();

                jo.put("success", false);
                jo.put("status", "ERROR");
                jo.put("message", "BRAK_ILOSCI_PRODUKTU");

                return ResponseEntity.ok(jo.toString());
            }
        }

        String username;
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            username = userDetails.getUsername();

            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            Order o = new Order();
            o.setUsedProductList(usedProductArrayList);
            o.setPrice(price);
            o.setPrincipal(principalRepository.findById(json.getLong("principalID")).get());
            o.setUser(userRepository.findByUsername(username));
            o.setDate(new Date());
            //o.setDepartureDate(formatter.parse("2019-05-05"));
            //o.setEndDate(formatter.parse("2019-05-05"));

            staticProductRepository.saveAll(staticProductArrayList);
            usedProductRepository.saveAll(usedProductArrayList);
            orderRepository.save(o);

            JSONArray ja = new JSONArray();
            double palletes = 0.0;

            for (UsedProduct ud : usedProductArrayList) {
                JSONObject usedProductsJson = new JSONObject();

                StaticProduct sp = staticProductRepository.getOne(ud.getIdStaticProduct());

                usedProductsJson.put("productID", ud.getIdStaticProduct());
                usedProductsJson.put("palletes", (double) ud.getQuanitity() / (double) sp.getQuantityOnThePalette());
                palletes += (double) ud.getQuanitity() / (double) sp.getQuantityOnThePalette();

                ja.put(usedProductsJson);
            }

            JSONObject jo = new JSONObject();

            jo.put("success", true);
            //jo.put("order",o);
            jo.put("products", ja);
            jo.put("palletes", palletes);
            jo.put("id", o.getId());

            return ResponseEntity.ok(jo.toString());


        } catch (ClassCastException e) {
            username = "anonymousUser";

            JSONObject jo = new JSONObject();

            jo.put("success", false);
            jo.put("status", "ERROR");
            jo.put("message", "BLAD Z UZYTKOWNIKIEM");
        }
//        catch (ParseException e) {
//            e.printStackTrace();
//        }

        JSONObject jo = new JSONObject();

        jo.put("success", false);
        jo.put("status", "ERROR");
        jo.put("message", "BLAD NIEZNANY");
        return ResponseEntity.ok(jo.toString());
    }

    @RequestMapping(path = "/complete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> completingOrder(@RequestBody String request) {

        JSONObject json = new JSONObject(request);
        JSONObject returnJson = new JSONObject();

        long orderID = json.getLong("orderID");
        Order o = orderRepository.getOne(orderID);

        JSONArray jsonArray = json.getJSONArray("products");

        List<UsedProduct> usedProductList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();
        List<ProductIdWithQuantity> productIdWithQuantityList = new ArrayList<>();

        Map<Long,ProductIdWithQuantity> productIdWithQuantityMap = new HashMap<>();

        for(UsedProduct usedProduct: o.getUsedProductList()){
            ProductIdWithQuantity productIdWithQuantity = new ProductIdWithQuantity();
            productIdWithQuantity.setId(usedProduct.getIdStaticProduct());
            productIdWithQuantity.setQuantity(usedProduct.getQuanitity());
            productIdWithQuantityList.add(productIdWithQuantity);

            productIdWithQuantityMap.put(usedProduct.getIdStaticProduct(),new ProductIdWithQuantity(usedProduct.getIdStaticProduct(),usedProduct.getQuanitity()));

            usedProduct.setPicked(true);
            usedProductList.add(usedProduct);
        }

        for(int i=0; i<jsonArray.length();++i) {

            JSONObject jo = jsonArray.getJSONObject(i);
            Location l = locationRepository.findByBarCodeLocation(jo.getString("locationBarCode"));
            Product p = productRepository.findById(jo.getLong("productID")).get();
            int quantiy = jo.getInt("quantity");

            //System.out.println("XDD " + jsonArray.toString());
            //System.out.println("XDDDDDDDDDDDDDDD " + jsonArray.length());

            Long staticProductID = p.getStaticProduct().getId();
            //System.out.println("XDD " + staticProductID);
            productIdWithQuantityMap.get(staticProductID).setQuantity(productIdWithQuantityMap.get(staticProductID).getQuantity()-quantiy);

            if(productIdWithQuantityMap.get(staticProductID).getQuantity() == 0)
            {
                productIdWithQuantityMap.remove(staticProductID);
            }
            else if(productIdWithQuantityMap.get(staticProductID).getQuantity()<0){

                JSONObject jo2 = new JSONObject();

                jo2.put("success",false);
                jo2.put("status","ERROR");
                jo2.put("message","BLAD Z ILOSCIA PRODUKTU");
            }

            p.setState(p.getState()-quantiy);
            productList.add(p);
        }

        if(productIdWithQuantityMap.isEmpty()){

            usedProductRepository.saveAll(usedProductList);
            productRepository.saveAll(productList);
            //System.out.println("XDDDDDDDDDDDDD");

            returnJson.put("success",true);
            returnJson.put("status","OK");

            return ResponseEntity.ok(returnJson.toString());

        }
        else{
            returnJson.put("success",false);
            returnJson.put("status","ERROR");
            returnJson.put("message","PRODUKTY_SIE_NIE_ZGADZAJA");
            return ResponseEntity.ok(returnJson.toString());
        }
    }

    @RequestMapping(path = "/end", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> endOrder(@RequestBody String request) {

        JSONObject json = new JSONObject(request);
        JSONObject returnJson = new JSONObject();

        long orderID = json.getLong("orderID");

        Order o = orderRepository.getOne(orderID);
        o.setEndDate(new Date());

        orderRepository.save(o);

        returnJson.put("success", true);
        returnJson.put("status", "OK");
        return ResponseEntity.ok(returnJson.toString());
    }
}