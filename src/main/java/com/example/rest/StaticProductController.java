package com.example.rest;

import com.example.model.Location;
import com.example.model.Product;
import com.example.model.StaticLocation;
import com.example.model.StaticProduct;
import com.example.repository.LocationRepository;
import com.example.repository.ProductRepository;
import com.example.repository.StaticLocationRepository;
import com.example.repository.StaticProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.parsers.LocationParser.locationParserOneLocation;
import static com.example.parsers.StaticPorductPraser.staticProductParser;

@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/Product")
public class StaticProductController {

    @Autowired
    StaticProductRepository staticProductRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    StaticLocationRepository staticLocationRepository;


    @RequestMapping(path = "/findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllProducts() {
        List<StaticProduct> allProducts = staticProductRepository.findAll();
        JSONArray jsonArray = new JSONArray();

        for (StaticProduct oneProduct : allProducts) {
            JSONObject jsonObjecOfStaticProduct = createJsonToResponse(oneProduct);

            JSONArray products = new JSONArray();
            for (Product one : oneProduct.getProducts()) {
                JSONObject oneProdcutJson = productJson(one);
                JSONObject location = new JSONObject();
                for (Location l : one.getLocations()) {

                    location.put("id", l.getId());
                    location.put("barCodeLocation", l.getBarCodeLocation());

                }
                oneProdcutJson.put("locations", location);
                products.put(oneProdcutJson);
            }
            jsonObjecOfStaticProduct.put("products", products);
            jsonArray.put(jsonObjecOfStaticProduct);
        }

        return ResponseEntity.ok(jsonArray.toString());
    }


    @RequestMapping(path = "/getInfoAboutProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getInfoAboutProduct(@RequestBody String barcode) throws JsonProcessingException {
        System.out.println("json" + barcode);
        JSONObject js = new JSONObject(barcode);


        Long id = js.getLong("id");
        System.out.println(id);
        if (id != 0) {
            StaticProduct staticProduct = staticProductRepository.getOne(id);

            JSONObject jsonObject = createJsonToResponse(staticProduct);
            JSONArray jsonArray = new JSONArray();

            for (Product product : staticProduct.getProducts()) {
                JSONObject jsonObject1 = productJson(product);

                jsonArray.put(jsonObject1);
            }


            //System.out.println(jsonObject);
            jsonObject.put("products", jsonArray);
            return ResponseEntity.ok(jsonObject.toString());
        } else {
            return ResponseEntity.ok(new JSONObject().put("Status", "error"));
        }


    }

    @RequestMapping(path = "/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> test(@RequestBody String test) {
        JSONObject js = new JSONObject(test);
        Long id = js.getLong("id");
        if (id != 0) {
            StaticProduct staticProduct = staticProductRepository.getOne(id);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", staticProduct.getId());
            jsonObject.put("price", staticProduct.getPrice());
            jsonObject.put("quantityOnThePalette", staticProduct.getQuantityOnThePalette());
            jsonObject.put("quantityInPackage", staticProduct.getAmountInAPack());
            jsonObject.put("producer", staticProduct.getProducer());
            jsonObject.put("barCode", staticProduct.getBarCode());
            jsonObject.put("name", staticProduct.getName());
            jsonObject.put("logicState", staticProduct.getLogicState());
            jsonObject.put("category", staticProduct.getCategory());

            JSONObject staticLocation = new JSONObject();
            staticLocation.put("id", staticProduct.getStaticLocation().getId());
            staticLocation.put("barCodeLocation", staticProduct.getStaticLocation().getBarCodeLocation());

            jsonObject.put("staticLocation", staticLocation);
            return ResponseEntity.ok(jsonObject.toString());
        } else {
            return ResponseEntity.ok(new JSONObject().put("Status", "error"));
        }

    }

    @RequestMapping(path = "/changeLocationOfTheProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> changeLocationOfTheProduct(@RequestBody String newLocation) {
        JSONObject js = new JSONObject(newLocation);

        Location location = locationParserOneLocation(js.get("location").toString());
        Location locationToUpdate = locationRepository.findByBarCodeLocation(location.getBarCodeLocation());


        String oldLocationBarCode = locationParserOneLocation(js.get("oldLocation").toString()).getBarCodeLocation();
        Location oldLocation = locationRepository.findByBarCodeLocation(oldLocationBarCode);
        addToStack(locationToUpdate, location, oldLocation);


        locationRepository.save(locationToUpdate);

        return ResponseEntity.ok(new JSONObject().put("status","changed").toString());
    }


    private void removeStateFromOldLocation(Product product, int state) {
        product.setState(product.getState() - state);
        if (product.getState() == 0) {

            StaticProduct st = staticProductRepository.findByBarCode(product.getStaticProduct().getBarCode());
            st.getProducts().remove(product);
            staticProductRepository.save(st);
            productRepository.delete(product);

        } else {
            productRepository.save(product);
        }

    }

    private Product createProductWithInfo(Product product, int state) {
        Product p = new Product();
        p.setStaticProduct(product.getStaticProduct());
        p.setExprDate(product.getExprDate());
        p.setState(state);
        return p;

    }

    private void addToStack(Location locationToUpdate, Location location, Location oldLocation) {

        try {
            for (Product product : location.getProducts()) {

                Product productInNewLocation = locationToUpdate.getProducts().stream().filter(
                        x -> x.getStaticProduct().getBarCode().equals(product.getStaticProduct().getBarCode())).filter(
                        x -> x.getExprDate().equals(productRepository.getProductById(product.getId()).getExprDate()))
                        .findFirst().orElse(null);


                Product productInDB = oldLocation.getProducts().stream()
                        .filter(x -> x.getId().equals(product.getId())).findFirst().get();
                if (productInDB != null) {

                    int state = product.getState();

                    if (productInNewLocation != null) {
                        productInNewLocation.setState(productInNewLocation.getState() + state);
                        removeStateFromOldLocation(productRepository.getOne(product.getId()), state);
                        productRepository.save(productInNewLocation);
                    } else {
                        Product toSave = createProductWithInfo(productInDB, state);
                        productRepository.save(toSave);
                        removeStateFromOldLocation(productRepository.getOne(product.getId()), state);


                        locationToUpdate.getProducts().add(toSave);
                        locationRepository.save(locationToUpdate);


                        toSave.setLocations(new ArrayList<>());
                        toSave.getLocations().add(locationToUpdate);
                        productRepository.save(toSave);


                        StaticProduct staticProduct = staticProductRepository.findByBarCode(toSave.getStaticProduct().getBarCode());
                        staticProduct.getProducts().add(toSave);
                        staticProductRepository.save(staticProduct);
                    }
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("Exception", e);
        }
    }

    @RequestMapping(path = "/addNewProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> addNewProduct(@RequestBody String newProduct) {
        StaticProduct staticProduct = staticProductParser(newProduct);
        String barCode = staticProduct.getBarCode();
        if (staticProductRepository.findByBarCode(barCode) == null) {
            System.out.println(staticProduct.getStaticLocation().getBarCodeLocation());
            StaticLocation staticLocation = staticLocationRepository.findByBarCodeLocation(staticProduct.getStaticLocation().getBarCodeLocation());
            if (staticLocation == null) {
                staticLocationRepository.save(staticProduct.getStaticLocation());
            } else {
                staticProduct.setStaticLocation(staticLocation);
            }

            staticProduct.setProducts(new ArrayList<>());


            staticProductRepository.save(staticProduct);
            return ResponseEntity.ok(new JSONObject().put("Status", "OK").put("Id", staticProduct.getId()).toString());
        } else {
            return ResponseEntity.ok(new JSONObject().put("Status", "ProductAlredyExist").toString());
        }

    }

    @RequestMapping(path = "/findAllProducts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> findAllProducts() {
        List<StaticProduct> allProducts = staticProductRepository.findAll();
        JSONArray jsonArray = new JSONArray();

        for (StaticProduct oneProduct : allProducts) {
            JSONObject jsonObjecOfStaticProduct = new JSONObject();
            jsonObjecOfStaticProduct.put("id", oneProduct.getId());
            jsonObjecOfStaticProduct.put("producer", oneProduct.getProducer());
            jsonObjecOfStaticProduct.put("barCode", oneProduct.getBarCode());
            jsonObjecOfStaticProduct.put("name", oneProduct.getName());
            jsonObjecOfStaticProduct.put("category", oneProduct.getCategory());
            jsonArray.put(jsonObjecOfStaticProduct);
        }
        return ResponseEntity.ok(jsonArray.toString());

    }


    private JSONObject createJsonToResponse(StaticProduct staticProduct) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", staticProduct.getId());
        jsonObject.put("price", staticProduct.getPrice());
        jsonObject.put("quantityOnThePalette", staticProduct.getQuantityOnThePalette());
        jsonObject.put("quantityInPackage", staticProduct.getAmountInAPack());
        jsonObject.put("producer", staticProduct.getProducer());
        jsonObject.put("barCode", staticProduct.getBarCode());
        jsonObject.put("name", staticProduct.getName());
        jsonObject.put("logicState", staticProduct.getLogicState());
        jsonObject.put("category", staticProduct.getCategory());

        JSONObject staticLocation = new JSONObject();
        staticLocation.put("id", staticProduct.getStaticLocation().getId());
        staticLocation.put("barCodeLocation", staticProduct.getStaticLocation().getBarCodeLocation());

        jsonObject.put("staticLocation", staticLocation);
        return jsonObject;

    }

    private JSONObject productJson(Product product) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", product.getId());
        jsonObject.put("exprDate", product.getExprDate());
        jsonObject.put("state", product.getState());
        jsonObject.put("locations", product.getLocations());

        return jsonObject;
    }


}
