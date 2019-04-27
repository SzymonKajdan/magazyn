package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import com.example.security.model.User;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
    @Autowired
    UsedProductLotRepository usedProductLotRepository;


    @RequestMapping(path = "/checkUserActiveOrder", method = RequestMethod.GET)
    public ResponseEntity<?> checkUserActiveOrder() {
        String username;

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);


        Order order = orderRepository.findByEndDateIsNullAndUser(user);
        System.out.println(order);
        if (order != null) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", order.getId());
            System.out.println(jsonObject.toString());
            return ResponseEntity.ok(jsonObject.toString());
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @RequestMapping(path = "/statsOfOrdersEndedByUser", method = RequestMethod.GET)
    public ResponseEntity<?> statsOfOrdersEndedByUser() {
        String username;
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);
        JSONArray statsArray = createStatsArray(user);

        return ResponseEntity.ok(statsArray.toString());
    }

    private JSONArray createStatsArray(User user) {
        JSONArray statsArray = new JSONArray();
        for (int i = 0; i < 7; i++) {
            DateTime dateTimeStart = new DateTime().withHourOfDay(0).withMinuteOfHour(0).withMinuteOfHour(0).withSecondOfMinute(0);
            dateTimeStart = dateTimeStart.minusDays(i);

            DateTime dateTimeEnd = new DateTime().withHourOfDay(23).withMinuteOfHour(59).withMinuteOfHour(59).withSecondOfMinute(59);
            dateTimeEnd = dateTimeEnd.minusDays(i);

            List<Order> orders = orderRepository.findAllByEndDateBetweenAndUser(dateTimeStart.toDate(), dateTimeEnd.toDate(), user);

            JSONObject jsonObjectDay = new JSONObject();

            jsonObjectDay.put("Day", createDayJson(orders, dateTimeStart));

            statsArray.put(jsonObjectDay);
        }
        return statsArray;
    }

    private JSONObject createDayJson(List<Order> orders, DateTime dateTime) {
        JSONObject day = new JSONObject();


        double palletes = orders.stream().mapToDouble(x -> countPalletes(x.getUsedProductList())).sum();
        long sumPositions = orders.stream().mapToLong(x -> x.getUsedProductList().size()).sum();

        String date = new String();
        date = dateTime.toString(DateTimeFormat.forPattern("dd.MM.yyyy"));
        day.put("amountOfOrders", orders.size());
        day.put("palettes", palletes);
        day.put("amountOfPositions", sumPositions);
        day.put("date", date);

        return day;
    }

    @RequestMapping(path = "/amountOfNotEndedOrders", method = RequestMethod.GET)
    public ResponseEntity<?> amountOfNotEndedOrders() {
        List<Order> ordersNotEnded = orderRepository.findAllByUserIsNullAndEndDateIsNull();
        int size = ordersNotEnded.size();
        JSONObject response = new JSONObject();
        response.put("amount", size);

        return ResponseEntity.ok(size);
    }

    @RequestMapping(path = "/cancelMakingOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> cancelMakingOrder(@RequestBody String request) {
        String username;

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);


        JSONObject jsonId = new JSONObject(request);

        if (jsonId.isNull("id")) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", "ERROR");
            returnJson.put("message", "Nie podano id");
            returnJson.put("success", false);

            return ResponseEntity.ok(returnJson.toString());
        }

        Long id = jsonId.getLong("id");

        JSONObject returnJson = new JSONObject();

        Order order = orderRepository.getOne(id);

        if (order != null) {

            List<StaticProduct> sp_list = new ArrayList<>();

            for (UsedProduct ud : order.getUsedProductList()) {
                if(ud.getPickedQuanitity()>0){
                    returnJson.put("success", false);
                    returnJson.put("message", "Nie wszystkie produkty zostaly odlozone");

                    return ResponseEntity.ok(returnJson.toString());
                }

                StaticProduct sp = staticProductRepository.findById(ud.getIdStaticProduct()).get();

                sp.setLogicState(sp.getLogicState()+ud.getQuanitity());
                sp_list.add(sp);
            }

            order.setUser(null);
            orderRepository.save(order);

            staticProductRepository.saveAll(sp_list);

            //orderRepository.delete(order);

            returnJson.put("success", true);
            return ResponseEntity.ok(returnJson.toString());

        } else {
            returnJson.put("success", false);
            returnJson.put("message", "Taki order nie istnieje");

            return ResponseEntity.ok(returnJson.toString());
        }
    }

    // cos

    @RequestMapping(path = "/findAllOrderByDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllOrdersOrderByDateAsc() {
        List<Order> orders = orderRepository.findAllByOrderByDate();

        JSONArray jsonArrayOrders = new JSONArray();
        for (Order order : orders) {
            JSONArray products = new JSONArray();

            JSONObject orderObject = orderToJSON(order);
            for (UsedProduct usedProduct : order.getUsedProductList()) {

                products.put(addProductsToOrder(usedProduct));
            }
            orderObject.put("products", products);
            jsonArrayOrders.put(orderObject);
        }

        return ResponseEntity.ok(jsonArrayOrders.toString());
    }

    @RequestMapping(path = "/findAllOrderByDateDsc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllOrdersOrderByDateDsc() {
        List<Order> orders = orderRepository.findAllByOrderByDateDsc();
        return createJsonArrayOfOrders(orders);
    }

    private ResponseEntity<?> createJsonArrayOfOrders(List<Order> orders) {
        JSONArray jsonArrayToResponse = new JSONArray();
        for (Order oneOrderNotEnded : orders) {

            JSONObject orderObject = orderToJSON(oneOrderNotEnded);

            jsonArrayToResponse.put(orderObject);
        }

        return ResponseEntity.ok(jsonArrayToResponse.toString());
    }

    @RequestMapping(path = "/findNotEndedAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getNotEndedOrdersOrderByDateAsc() {
        List<Order> ordersNotEnded = orderRepository.findAllByEndDateOrderByDate(null);
        return createJsonArrayOfOrders(ordersNotEnded);
    }

    @RequestMapping(path = "/getInfoAboutOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getInfoAboutOrder(@RequestBody String oderId) {
        String username;

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        username = userDetails.getUsername();
        User user = userRepository.findByUsername(username);


        JSONObject jsonId = new JSONObject(oderId);

        if (jsonId.isNull("id")) {
            JSONObject returnJson = new JSONObject();
            returnJson.put("status", "ERROR");
            returnJson.put("message", "Nie podano id");
            returnJson.put("success", false);

            return ResponseEntity.ok(returnJson.toString());
        }

        Long id = jsonId.getLong("id");

        if (orderRepository.existsById(id)) {

            Order order = orderRepository.getOne(id);
            order.setUser(user);
            System.out.println(order.getUser());
            userRepository.save(user);

            JSONArray products = new JSONArray();

            JSONObject orderObject = orderToJSON(order);
            for (UsedProduct usedProduct : order.getUsedProductList()) {

                products.put(addProductsToOrder(usedProduct));

            }
            orderObject.put("products", products);
            return ResponseEntity.ok(orderObject.toString());
        }

        JSONObject returnJson = new JSONObject();
        returnJson.put("status", "ERROR");
        returnJson.put("message", "Brak takiego zamowienia");
        returnJson.put("success", false);

        return ResponseEntity.ok(returnJson.toString());
    }

    @RequestMapping(path = "/completedProducts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> completedProducts(@RequestBody String oderId) {

        JSONObject returnJson = new JSONObject();

        JSONObject requestJson = new JSONObject(oderId);
        long orderID = requestJson.getLong("id");

        Optional<Order> o_optional = orderRepository.findById(orderID);

        if (!o_optional.isPresent()) {
            returnJson.put("success", false);
            returnJson.put("status", "ERROR");
            returnJson.put("message", "Order o id " + orderID + " nie istnieje");
            return ResponseEntity.ok(returnJson.toString());
        }

        Order o = o_optional.get();
        Set<Long> ids = new HashSet<>();

        for (UsedProduct usedProduct : o.getUsedProductList()) {
            for (UsedProductLot usedProductLot : usedProduct.getUsedProductLots()) {
                ids.add(usedProductLot.getProductID());
            }
        }

        JSONArray jsonArray = new JSONArray();
        for (Long id : ids) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",id);

            Product p = productRepository.findById(id).get();

            jsonObject.put("staticProduct",new JSONObject().put("id",p.getStaticProduct().getId()));
            jsonArray.put(jsonObject);
        }

        return ResponseEntity.ok(jsonArray.toString());
    }

    private JSONObject addProductsToOrder(UsedProduct usedProduct) {
        System.out.println(usedProduct.getId() + " " + usedProduct.getQuanitity());
        StaticProduct product = staticProductRepository.getOne(usedProduct.getIdStaticProduct());
        JSONObject jsonToReturn = new JSONObject();

        JSONObject productJSON = new JSONObject();
        productJSON.put("id", product.getId());
        productJSON.put("name", product.getName());
        productJSON.put("producer", product.getProducer());
        productJSON.put("barCode", product.getBarCode());
        productJSON.put("category", product.getCategory());
        productJSON.put("quantityOnThePalette", product.getQuantityOnThePalette());
        productJSON.put("amountInAPack", product.getAmountInAPack());

        JSONObject location = new JSONObject();
        location.put("id", product.getStaticLocation().getId());
        location.put("barCodeLocation", product.getStaticLocation().getBarCodeLocation());

        productJSON.put("staticLocation", location);
        productJSON.put("quantityInPackage", product.getAmountInAPack());

        jsonToReturn.put("product", productJSON);
        jsonToReturn.put("orderedQuantity", usedProduct.getQuanitity());
        jsonToReturn.put("quantity", usedProduct.getQuanitity()-usedProduct.getPickedQuanitity());
        jsonToReturn.put("pickedQuantity",usedProduct.getPickedQuanitity());
        jsonToReturn.put("isPicked",usedProduct.isPicked());
        return jsonToReturn;
    }

    private JSONObject orderToJSON(Order order) {

        JSONObject orderObject = new JSONObject();
        JSONObject principal = new JSONObject();
        principal.put("id", order.getPrincipal().getId());
        principal.put("nip", order.getPrincipal().getNip());
        principal.put("address", order.getPrincipal().getAddress());
        principal.put("companyName", order.getPrincipal().getCompanyName());
        principal.put("phoneNo", order.getPrincipal().getPhoneNo());


        orderObject.put("id", order.getId());
        orderObject.put("date", order.getDate());
        orderObject.put("endDate", order.getEndDate());
        orderObject.put("principal", principal);
        orderObject.put("price", order.getPrice());
        orderObject.put("departureDate", order.getDepartureDate());
        orderObject.put("amountOfArticles", countArticles(order.getUsedProductList()));
        orderObject.put("palletes", countPalletes(order.getUsedProductList()));
        orderObject.put("productsCount", order.getUsedProductList().size());
        return orderObject;

    }

    private long countArticles(List<UsedProduct> usedProductList) {
        long sum = usedProductList.stream().filter(o -> o.getQuanitity() > 0).mapToInt(o -> o.getQuanitity()).sum();

        return sum;
    }

    private double countPalletes(List<UsedProduct> usedProductList) {
        double sum = usedProductList.stream().filter(o -> o.getQuanitity() > 0).mapToDouble(o -> ((double) o.getQuanitity() / (double) staticProductRepository.getOne(o.getIdStaticProduct()).getQuantityOnThePalette())).sum();

        return sum;
    }



    @RequestMapping(path = "/make", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> makeOrder(@RequestBody String request) {

        JSONObject json = new JSONObject(request);
        //HashMap<Long,Integer> productsMap = (HashMap<Long, Integer>) json.get("products");

        //System.out.println("Tutaj: " + json.getJSONArray("products"));
        //System.out.println("tutaj" + json.get("principalID"));
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
            usedProduct.setBarCodeProduct(staticProduct.getBarCode());
            usedProduct.setUsedProductLots(new ArrayList<>());

            usedProductArrayList.add(usedProduct);

            price += staticProduct.getPrice() * quantity;

            if (staticProduct.getLogicState() - quantity > 0) {
                staticProduct.setLogicState(staticProduct.getLogicState() - quantity);
                staticProductArrayList.add(staticProduct);
            } else {
                JSONObject jo = new JSONObject();

                jo.put("success", false);
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

            o.setDate(new Date());
            DateTime dateTime = new DateTime().withHourOfDay(8);
            dateTime = dateTime.plusDays(2);

            o.setDepartureDate(dateTime.toDate());
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
            return ResponseEntity.ok("ERROR");
        }
    }

//    @RequestMapping(path = "/complete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<?> completingOrder(@RequestBody String request) {
//
//        JSONObject json = new JSONObject(request);
//        JSONObject returnJson = new JSONObject();
//
//        long orderID = json.getLong("orderID");
//
//        Optional<Order> o_optional = orderRepository.findById(orderID);
//
//        if (!o_optional.isPresent()) {
//            returnJson.put("success", false);
//            returnJson.put("status", "ERROR");
//            returnJson.put("message", "Order o id " + orderID + " nie istnieje");
//            return ResponseEntity.ok(returnJson.toString());
//        }
//
//        Order o = o_optional.get();
//
//        JSONArray jsonArray = json.getJSONArray("products");
//
//        List<UsedProduct> usedProductList = new ArrayList<>();
//        List<Product> productList = new ArrayList<>();
//        List<ProductIdWithQuantity> productIdWithQuantityList = new ArrayList<>();
//
//        Map<Long, ProductIdWithQuantity> productIdWithQuantityMap = new HashMap<>();
//
//        for (UsedProduct usedProduct : o.getUsedProductList()) {
//            ProductIdWithQuantity productIdWithQuantity = new ProductIdWithQuantity();
//            productIdWithQuantity.setId(usedProduct.getIdStaticProduct());
//            productIdWithQuantity.setQuantity(usedProduct.getQuanitity());
//            productIdWithQuantityList.add(productIdWithQuantity);
//
//            productIdWithQuantityMap.put(usedProduct.getIdStaticProduct(), new ProductIdWithQuantity(usedProduct.getIdStaticProduct(), usedProduct.getQuanitity()));
//
//            usedProduct.setPicked(true);
//            usedProductList.add(usedProduct);
//        }
//
//        for (int i = 0; i < jsonArray.length(); ++i) {
//
//            JSONObject jo = jsonArray.getJSONObject(i);
//            Location l = locationRepository.findByBarCodeLocation(jo.getString("locationBarCode"));
//            Product p = productRepository.findById(jo.getLong("productID")).get();
//            int quantiy = jo.getInt("quantity");
//
//            //System.out.println("XDD " + jsonArray.toString());
//            //System.out.println("XDDDDDDDDDDDDDDD " + jsonArray.length());
//
//            Long staticProductID = p.getStaticProduct().getId();
//            //System.out.println("XDD " + staticProductID);
//            productIdWithQuantityMap.get(staticProductID).setQuantity(productIdWithQuantityMap.get(staticProductID).getQuantity() - quantiy);
//
//            if (productIdWithQuantityMap.get(staticProductID).getQuantity() == 0) {
//                productIdWithQuantityMap.remove(staticProductID);
//            } else if (productIdWithQuantityMap.get(staticProductID).getQuantity() < 0) {
//                System.out.println("ERROR");
//            }
//
//            p.setState(p.getState() - quantiy);
//            productList.add(p);
//        }
//
//        if (productIdWithQuantityMap.isEmpty()) {
//
//            usedProductRepository.saveAll(usedProductList);
//            productRepository.saveAll(productList);
//            //System.out.println("XDDDDDDDDDDDDD");
//
//            returnJson.put("success", true);
//
//            return ResponseEntity.ok(returnJson.toString());
//
//        } else {
//            returnJson.put("success", false);
//            returnJson.put("message", "PRODUKTY_SIE_NIE_ZGADZAJA");
//            return ResponseEntity.ok(returnJson.toString());
//        }
//    }

    @RequestMapping(path = "/completing", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> completingOrder2(@RequestBody String request) {

        JSONArray jsonArray = new JSONArray(request);
        JSONObject returnJson2 = new JSONObject();
        returnJson2.put("messages",new JSONArray());

        List<UsedProductLot> usedProductLotToSave = new ArrayList<>();
        List<UsedProduct> usedProductToSave = new ArrayList<>();
        List<Product> productsToSave = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            boolean success = false;

            JSONObject returnJson = new JSONObject();
            JSONObject json = jsonArray.getJSONObject(i);

            long orderID = json.getLong("orderID");

            Optional<Order> o_optional = orderRepository.findById(orderID);

            if (!o_optional.isPresent()) {
                returnJson.put("success", false);
                returnJson.put("status", "ERROR");
                returnJson.put("message", "Pozycja: "+i+",Order o id " + orderID + " nie istnieje");
                return ResponseEntity.ok(returnJson.toString());

                //returnJson2.getJSONArray("messages").put("Pozycja: "+i+",Order o id " + orderID + " nie istnieje");
            }

            Order o = o_optional.get();

            long prodID = json.getJSONObject("product").getLong("productID");
            Optional<Product> p_optional = productRepository.findById(prodID);

            if (!p_optional.isPresent()) {
                returnJson.put("success", false);
                returnJson.put("status", "ERROR");
                returnJson.put("message", "Pozycja: "+i+",Produkt o id " + prodID + " nie istnieje");
                return ResponseEntity.ok(returnJson.toString());

                //returnJson2.getJSONArray("messages").put("Pozycja: "+i+",Produkt o id " + prodID + " nie istnieje");
            }

            Product p = p_optional.get();

            int quantity = json.getJSONObject("product").getInt("quantity");

            if (p.getState() < quantity) {
                returnJson.put("success", false);
                returnJson.put("status", "ERROR");
                returnJson.put("message", "Pozycja: "+i+", Wybranego produktu jest za malo");
                return ResponseEntity.ok(returnJson.toString());

                //returnJson2.getJSONArray("messages").put("Pozycja: "+i+", Wybranego produktu jest za malo");
            }

            for (UsedProduct x : o.getUsedProductList()) {

                if (x.getIdStaticProduct() == p.getStaticProduct().getId()) {

                    if (x.isPicked()) {
                        returnJson.put("success", false);
                        returnJson.put("status", "ERROR");
                        returnJson.put("message", "Pozycja: "+i+", Produkt byl juz skompletowany");
                        return ResponseEntity.ok(returnJson.toString());

                        //returnJson2.getJSONArray("messages").put("Pozycja: "+i+", Produkt byl juz skompletowany");
                    }

//                if (x.getPickedQuanitity()+quantity>x.getQuanitity()) {
//                    returnJson.put("success", false);
//                    returnJson.put("status", "ERROR");
//                    returnJson.put("message", "Za duzo produktu, zla ilosc");
//                    return ResponseEntity.ok(returnJson.toString());
//                }

                    if (x.getQuanitity() - x.getPickedQuanitity() >= quantity) {

                        x.setPickedQuanitity(x.getPickedQuanitity() + quantity);
                        if (x.getQuanitity() == x.getPickedQuanitity()) {
                            x.setPicked(true);
                        }
                        p.setState(p.getState() - quantity);

                        boolean upl_exist = false;

                        for (UsedProductLot xUsedProductLot : x.getUsedProductLots()) {

                            //System.out.println(xUsedProductLot.getProductID());

                            if (xUsedProductLot.getProductID() == p.getId()) {

                                //System.out.println("TUTAJ");

                                xUsedProductLot.setProductID(p.getId());

                                int newQuantity = xUsedProductLot.getQuanitity() + quantity;

                                xUsedProductLot.setQuanitity(newQuantity);

                                //usedProductLotRepository.save(xUsedProductLot);
                                usedProductLotToSave.add(xUsedProductLot);

                                upl_exist = true;
                                break;
                            }
                        }

                        if (!upl_exist) {
                            UsedProductLot upl = new UsedProductLot();
                            //upl.setOrderID(o.getId());
                            upl.setProductID(p.getId());
                            upl.setQuanitity(quantity);

                            //usedProductLotRepository.save(upl);
                            usedProductLotToSave.add(upl);

                            x.getUsedProductLots().add(upl);
                        }

                        //usedProductRepository.save(x);
                        //productRepository.save(p);

                        usedProductToSave.add(x);
                        productsToSave.add(p);

                        success=true;

                        returnJson2.getJSONArray("messages").put("Pozycja: "+i+", Skompletowano produkt, do skompletowania zostalo " + (x.getQuanitity() - x.getPickedQuanitity()) + " sztuk");

                        continue;

                        //returnJson.put("success", true);
                        //returnJson.put("status", "OK");
                        //returnJson.put("message", "Skompletowano produkt, do skompletowania zostalo " + (x.getQuanitity() - x.getPickedQuanitity()) + " sztuk");
                        //return ResponseEntity.ok(returnJson.toString());
                    } else {
                        returnJson.put("success", false);
                        returnJson.put("status", "ERROR");
                        returnJson.put("message", "Pozycja: "+i+", Zla ilosc produktu");
                        return ResponseEntity.ok(returnJson.toString());

                        //returnJson2.getJSONArray("messages").put("Pozycja: "+i+", Zla ilosc produktu");
                    }
                }
            }

            if(!success) {
                returnJson.put("success", false);
                returnJson.put("status", "ERROR");
                returnJson.put("message", "Pozycja: " + i + ", Zly produkt");
                return ResponseEntity.ok(returnJson.toString());

                //returnJson2.getJSONArray("messages").put("Pozycja: "+i+", Zly produkt");
            }
        }

        usedProductLotRepository.saveAll(usedProductLotToSave);
        usedProductRepository.saveAll(usedProductToSave);
        productRepository.saveAll(productsToSave);

        returnJson2.put("success", true);
        returnJson2.put("status", "OK");
        returnJson2.put("message", "Produkty skompletowane");
        return ResponseEntity.ok(returnJson2.toString());
    }

    @RequestMapping(path = "/returning", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> returningOrder2(@RequestBody String request) {

        JSONArray jsonArray = new JSONArray(request);
        JSONObject returnJson2 = new JSONObject();
        returnJson2.put("messages",new JSONArray());

        List<UsedProductLot> usedProductLotToSave = new ArrayList<>();
        List<UsedProduct> usedProductToSave = new ArrayList<>();
        List<Product> productsToSave = new ArrayList<>();

        int successCount = 0;

        for (int i = 0; i < jsonArray.length(); i++) {

            boolean success = false;
            JSONObject returnJson = new JSONObject();
            JSONObject json = jsonArray.getJSONObject(i);

            long orderID = json.getLong("orderID");

            Optional<Order> o_optional = orderRepository.findById(orderID);

            if (!o_optional.isPresent()) {
                returnJson.put("success", false);
                returnJson.put("status", "ERROR");
                returnJson.put("message", "Pozycja: "+i+", Order o id " + orderID + " nie istnieje");
                return ResponseEntity.ok(returnJson.toString());
            }

            Order o = o_optional.get();

            long prodID = json.getJSONObject("product").getLong("productID");
            Optional<Product> p_optional = productRepository.findById(prodID);

            if (!p_optional.isPresent()) {
                returnJson.put("success", false);
                returnJson.put("status", "ERROR");
                returnJson.put("message", "Pozycja: "+i+", Produkt o id " + prodID + " nie istnieje");
                return ResponseEntity.ok(returnJson.toString());
            }

            //int quantity = json.getJSONObject("product").getInt("quantity");

            Product p = p_optional.get();

            for (UsedProduct up : o.getUsedProductList()) {
                if (up.getIdStaticProduct() == p.getStaticProduct().getId()) {

                    Iterator<UsedProductLot> iter = up.getUsedProductLots().iterator();

                    while (iter.hasNext()) {
                        UsedProductLot xUsedProductLot = iter.next();

                        if (xUsedProductLot.getProductID() == p.getId()) {

                            int newQuantity = up.getPickedQuanitity() - xUsedProductLot.getQuanitity();

                            if (newQuantity < 0) {
                                returnJson.put("success", false);
                                returnJson.put("status", "ERROR");
                                returnJson.put("message", "Pozycja: "+i+", Zla ilosc");
                                return ResponseEntity.ok(returnJson.toString());
                            }

                            up.setPickedQuanitity(newQuantity);
                            up.setPicked(false);

                            p.setState(p.getState() + xUsedProductLot.getQuanitity());

                            //up.getUsedProductLots().remove(xUsedProductLot);

                            //usedProductLotRepository.delete(xUsedProductLot);

                            usedProductLotToSave.add(xUsedProductLot);

                            //usedProductRepository.save(up);
                            //productRepository.save(p);

                            usedProductToSave.add(up);
                            productsToSave.add(p);

                            returnJson2.getJSONArray("messages").put("Odlozono produkt");

                            successCount++;

                            continue;

                            //returnJson.put("success", true);
                            //returnJson.put("status", "OK");
                            //returnJson.put("message", "Odlozono produkt");
                            //return ResponseEntity.ok(returnJson.toString());
                        }
                    }

                    up.getUsedProductLots().removeAll(usedProductLotToSave);

//                    for (UsedProductLot xUsedProductLot : up.getUsedProductLots()) {
//                        if (xUsedProductLot.getProductID() == p.getId()) {
//
//                            int newQuantity = up.getPickedQuanitity() - xUsedProductLot.getQuanitity();
//
//                            if (newQuantity < 0) {
//                                returnJson.put("success", false);
//                                returnJson.put("status", "ERROR");
//                                returnJson.put("message", "Pozycja: "+i+", Zla ilosc");
//                                return ResponseEntity.ok(returnJson.toString());
//                            }
//
//                            up.setPickedQuanitity(newQuantity);
//                            up.setPicked(false);
//
//                            p.setState(p.getState() + xUsedProductLot.getQuanitity());
//
//                            up.getUsedProductLots().remove(xUsedProductLot);
//
//                            //usedProductLotRepository.delete(xUsedProductLot);
//
//                            usedProductLotToSave.add(xUsedProductLot);
//
//                            //usedProductRepository.save(up);
//                            //productRepository.save(p);
//
//                            usedProductToSave.add(up);
//                            productsToSave.add(p);
//
//                            success=true;
//
//                            returnJson2.getJSONArray("messages").put("Odlozono produkt");
//
//                            continue;
//
//                            //returnJson.put("success", true);
//                            //returnJson.put("status", "OK");
//                            //returnJson.put("message", "Odlozono produkt");
//                            //return ResponseEntity.ok(returnJson.toString());
//                        }
//                    }
                }
            }
        }
        System.out.println(successCount+" | "+jsonArray.length());
        if(successCount==jsonArray.length()) {
            usedProductLotRepository.deleteAll(usedProductLotToSave);
            usedProductRepository.saveAll(usedProductToSave);
            productRepository.saveAll(productsToSave);

            returnJson2.put("success", true);
            returnJson2.put("status", "OK");
            return ResponseEntity.ok(returnJson2.toString());
        }

        returnJson2.put("success", false);
        returnJson2.put("status", "ERROR");
        return ResponseEntity.ok(returnJson2.toString());
    }

    @RequestMapping(path = "/end", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> endOrder(@RequestBody String request) {

        JSONObject json = new JSONObject(request);
        JSONObject returnJson = new JSONObject();

        long orderID = json.getLong("orderID");

        Optional<Order> o_optional = orderRepository.findById(orderID);

        if (!o_optional.isPresent()) {
            returnJson.put("success", false);
            returnJson.put("status", "ERROR");
            returnJson.put("message", "Order o id " + orderID + " nie istnieje");
            return ResponseEntity.ok(returnJson.toString());
        }

        Order o = o_optional.get();

        Set<Long> productIdList = new HashSet<>();

        for (UsedProduct usedProduct : o.getUsedProductList()) {
            if (!usedProduct.isPicked()) {
                returnJson.put("success", false);
                returnJson.put("status", "ERROR");
                returnJson.put("message", "Order nie jest skompletowany");
                return ResponseEntity.ok(returnJson.toString());
            }

            for (UsedProductLot upl : usedProduct.getUsedProductLots()) {
                productIdList.add(upl.getProductID());
            }
        }

        for (Long pid : productIdList) {
            Product p = productRepository.findById(pid).get();

            if(p.getState()==0){
                StaticProduct sp =p.getStaticProduct();
                sp.getProducts().remove(p);
                productRepository.delete(p);
                staticProductRepository.save(sp);
            }
        }

        o.setEndDate(new Date());

        orderRepository.save(o);

        returnJson.put("success", true);
        returnJson.put("status", "OK");
        returnJson.put("message", "Zamowienie zaakceptowane");
        return ResponseEntity.ok(returnJson.toString());
    }
}