package com.example.rest;

import com.example.model.Location;
import com.example.model.Product;
import com.example.model.StaticProduct;
import com.example.repository.LocationRepository;
import com.example.repository.ProductRepository;
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
        System.out.println("json" +barcode);
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
        JSONObject js=new JSONObject(test);
        Long id=js.getLong("id");
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
        Location lcotionToUpdate = locationRepository.findByBarCodeLocation(location.getBarCodeLocation());

        String oldLocation = locationParserOneLocation(js.get("oldLocation").toString()).getBarCodeLocation();

        addProductToLocation(lcotionToUpdate, location.getProducts(), oldLocation);
        locationRepository.save(lcotionToUpdate);

        return ResponseEntity.ok("ok");
    }

    @RequestMapping(path = "/addNewProduct", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> addNewProduct(@RequestBody String newProduct) {
        StaticProduct staticProduct = staticProductParser(newProduct);
        String barCode = staticProduct.getBarCode();
        if (staticProductRepository.findByBarCode(barCode) == null) {
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

    private void addProductToLocation(Location lcotionToUpdate, List<Product> products, String oldLocation) {
        for (Product productToAdd : products) {
            boolean ischange = false;
            for (Product product : lcotionToUpdate.getProducts()) {
                if (product.getStaticProduct().getBarCode().equals(productToAdd.getStaticProduct().getBarCode()) && product.getExprDate().equals(productToAdd.getExprDate())) {
                    product.setState(product.getState() + productToAdd.getState());
                    changeOldLocationProducts(oldLocation, productToAdd, product);
                    ischange = true;


                }
            }
            if (ischange == false) {
                Product productToSave = new Product();
                productToSave.setState(productToAdd.getState());
                productToSave.setExprDate(productToAdd.getExprDate());
                productToSave.setStaticProduct(staticProductRepository.findByBarCode(productToAdd.getStaticProduct().getBarCode()));
                changeOldLocationProducts(oldLocation, productToAdd, productToSave);
                ischange = true;
            }
        }
    }

    private void changeOldLocationProducts(String oldLocation, Product productToAdd, Product productToSave) {
        productRepository.save(productToSave);
        Location oldLoc = locationRepository.findByBarCodeLocation(oldLocation);
        for (Product oldStaeProduct : oldLoc.getProducts()) {
            if (oldStaeProduct.getId().equals(productToAdd.getId())) {
                oldStaeProduct.setState(oldStaeProduct.getState() - productToAdd.getState());
                if (oldStaeProduct.getState() == 0) {
                    oldLoc.getProducts().remove(productRepository.getOne(productToAdd.getId()));
                    StaticProduct staticProduct = staticProductRepository.findByBarCode(productToAdd.getStaticProduct().getBarCode());
                    staticProduct.getProducts().remove(productRepository.getOne(productToAdd.getId()));
                    productRepository.delete(productRepository.getOne(productToAdd.getId()));
                    locationRepository.save(oldLoc);
                }
            }

        }
    }

}
