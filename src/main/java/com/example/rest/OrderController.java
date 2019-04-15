package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import org.aspectj.weaver.ast.Or;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    @RequestMapping(path = "/amountOfNotEndedOrders",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?>amountOfNotEndedOrders(){
        List<Order>ordersNotEnded=orderRepository.findAllByUserIsNullAndEndDateIsNull();
        int size=ordersNotEnded.size();
        JSONObject response=new JSONObject();
        response.put("amount",size);
        return ResponseEntity.ok(size);


    }

    @RequestMapping(path = "/findAllOrderByDate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllOrdersOrderByDateAsc() {
        List<Order> orders = orderRepository.findAllByOrderByDate();

        JSONArray jsonArrayOrders=new JSONArray();
        for(Order order:orders){
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
    private ResponseEntity<?> getInfoAboutOrder(@RequestBody String oderId) {


        JSONObject jsonId = new JSONObject(oderId);

        if(jsonId.isNull("id")){
            JSONObject returnJson = new JSONObject();
            returnJson.put("status","ERROR");
            returnJson.put("message","Nie podano id");
            returnJson.put("success",false);

            return ResponseEntity.ok(returnJson.toString());
        }

        Long id = jsonId.getLong("id");

        if(orderRepository.existsById(id)) {

            Order order = orderRepository.getOne(id);


            JSONArray products = new JSONArray();

            JSONObject orderObject = orderToJSON(order);
            for (UsedProduct usedProduct : order.getUsedProductList()) {

                products.put(addProductsToOrder(usedProduct));

            }
            orderObject.put("products", products);
            return ResponseEntity.ok(orderObject.toString());
        }

        JSONObject returnJson = new JSONObject();
        returnJson.put("status","ERROR");
        returnJson.put("message","Brak takiego zamowienia");
        returnJson.put("success",false);

        return ResponseEntity.ok(returnJson.toString());


    }

    private JSONObject addProductsToOrder(UsedProduct usedProduct) {
        System.out.println(usedProduct.getId() + " " + usedProduct.getQuanitity());
        StaticProduct product = staticProductRepository.getOne(usedProduct.getIdStaticProduct());
        JSONObject jsonToReturn= new JSONObject();

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

        jsonToReturn.put("product",productJSON);
        jsonToReturn.put("quantity",usedProduct.getQuanitity());
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
        orderObject.put("date",order.getDate());
        orderObject.put("endDate",order.getEndDate());
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

        System.out.println("Tutaj: " + json.getJSONArray("products"));
        System.out.println("tutaj"+json.get("principalID"));
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
//        catch (ParseException e) {
//            e.printStackTrace();
//        }

        //return ResponseEntity.ok("ERROR");
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

        Map<Long, ProductIdWithQuantity> productIdWithQuantityMap = new HashMap<>();

        for (UsedProduct usedProduct : o.getUsedProductList()) {
            ProductIdWithQuantity productIdWithQuantity = new ProductIdWithQuantity();
            productIdWithQuantity.setId(usedProduct.getIdStaticProduct());
            productIdWithQuantity.setQuantity(usedProduct.getQuanitity());
            productIdWithQuantityList.add(productIdWithQuantity);

            productIdWithQuantityMap.put(usedProduct.getIdStaticProduct(), new ProductIdWithQuantity(usedProduct.getIdStaticProduct(), usedProduct.getQuanitity()));

            usedProduct.setPicked(true);
            usedProductList.add(usedProduct);
        }

        for (int i = 0; i < jsonArray.length(); ++i) {

            JSONObject jo = jsonArray.getJSONObject(i);
            Location l = locationRepository.findByBarCodeLocation(jo.getString("locationBarCode"));
            Product p = productRepository.findById(jo.getLong("productID")).get();
            int quantiy = jo.getInt("quantity");

            //System.out.println("XDD " + jsonArray.toString());
            //System.out.println("XDDDDDDDDDDDDDDD " + jsonArray.length());

            Long staticProductID = p.getStaticProduct().getId();
            //System.out.println("XDD " + staticProductID);
            productIdWithQuantityMap.get(staticProductID).setQuantity(productIdWithQuantityMap.get(staticProductID).getQuantity() - quantiy);

            if (productIdWithQuantityMap.get(staticProductID).getQuantity() == 0) {
                productIdWithQuantityMap.remove(staticProductID);
            } else if (productIdWithQuantityMap.get(staticProductID).getQuantity() < 0) {
                System.out.println("ERROR");
            }

            p.setState(p.getState() - quantiy);
            productList.add(p);
        }

        if (productIdWithQuantityMap.isEmpty()) {

            usedProductRepository.saveAll(usedProductList);
            productRepository.saveAll(productList);
            //System.out.println("XDDDDDDDDDDDDD");

            returnJson.put("success", true);

            return ResponseEntity.ok(returnJson.toString());

        } else {
            returnJson.put("success", false);
            returnJson.put("message", "PRODUKTY_SIE_NIE_ZGADZAJA");
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
        return ResponseEntity.ok(returnJson.toString());
    }
}