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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.example.parsers.LocationParser.locationParserOneLocation;
import static com.example.parsers.StaticPorductPraser.staticProductParser;

@RestController
@RequestMapping("/Product")
public class StaticProductController {

    @Autowired
    StaticProductRepository staticProductRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    ProductRepository productRepository;

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProducts() {
        for (StaticProduct st : staticProductRepository.findAll()) {
            for (Product p : st.getProducts()) {
                System.out.println(p.getId());
            }
        }
        return ResponseEntity.ok(staticProductRepository.findAll());
    }


    @RequestMapping(path = "/getInfoAboutProduct", method = RequestMethod.POST)
    public ResponseEntity<?> getInfoAboutProduct(@RequestBody String barcode) throws JsonProcessingException {
        JSONObject js = new JSONObject(barcode);
        System.out.println(js.toString());
        Long id=js.getLong("id");
        System.out.println(id);
        if(id!=0){
            StaticProduct staticProduct=staticProductRepository.getOne(id);

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id",staticProduct.getId());
            jsonObject.put("logicSate",staticProduct.getLogicState());
            jsonObject.put("name",staticProduct.getName());
            jsonObject.put("barCode",staticProduct.getBarCode());
            jsonObject.put("producer",staticProduct.getProducer());
            jsonObject.put("quantityOnThePalette",staticProduct.getQuantityOnThePalette());
            jsonObject.put("price",staticProduct.getPrice());
            jsonObject.put("category",staticProduct.getCategory());
            JSONArray jsonArray=new JSONArray();

            for(Product product:staticProduct.getProducts()){
                JSONObject jsonObject1=new JSONObject();
                jsonObject1.put("id",product.getId());
                jsonObject1.put("exprDate",product.getExprDate());
                jsonObject1.put("state",product.getState());
                jsonObject1.put("locations",product.getLocations());

                jsonArray.put(jsonObject1);
            }
            jsonObject.put("staticLcoations",staticProduct.getStaticLocations());
            //System.out.println(jsonObject);
            jsonObject.put("products",jsonArray);
            return  ResponseEntity.ok(jsonObject.toString());
        }else{
            return ResponseEntity.ok(new JSONObject().put("Status","error"));
        }


    }


    @RequestMapping(path = "/changeLocationOfTheProduct", method = RequestMethod.POST)
    public ResponseEntity<?> changeLocationOfTheProduct(@RequestBody String newLocation) {
        JSONObject js = new JSONObject(newLocation);

        Location location = locationParserOneLocation(js.get("location").toString());
        Location lcotionToUpdate = locationRepository.findByBarCodeLocation(location.getBarCodeLocation());

        String oldLocation = locationParserOneLocation(js.get("oldLocation").toString()).getBarCodeLocation();

        addProductToLocation(lcotionToUpdate, location.getProducts(), oldLocation);
        locationRepository.save(lcotionToUpdate);

        return ResponseEntity.ok("ok");
    }
    @RequestMapping(path = "/addNewProduct",method = RequestMethod.POST)
    public  ResponseEntity<?>addNewProduct(@RequestBody String newProduct){
        StaticProduct staticProduct=staticProductParser(newProduct);
        String barCode=staticProduct.getBarCode();
        if( staticProductRepository.findByBarCode(barCode)==null) {
            staticProduct.setProducts(new ArrayList<>());


            staticProductRepository.save(staticProduct);
             return ResponseEntity.ok(new JSONObject().put("Status","OK").put("Id",staticProduct.getId()).toString());
        }
        else{
            return ResponseEntity.ok(new JSONObject().put("Status","ProductAlredyExist").toString());
        }

    }
    private void addProductToLocation(Location lcotionToUpdate, List<Product> products, String oldLocation) {
        for (Product productToAdd : products) {
            boolean ischange=false;
            for (Product product : lcotionToUpdate.getProducts()) {
                if (product.getStaticProduct().getBarCode().equals(productToAdd.getStaticProduct().getBarCode()) && product.getExprDate().equals(productToAdd.getExprDate())) {
                    product.setState(product.getState() + productToAdd.getState());
                    changeOldLocationProducts(oldLocation,  productToAdd, product);
                    ischange=true;


                }
            }
            if(ischange==false){
                Product productToSave=new Product();
                productToSave.setState(productToAdd.getState());
                productToSave.setExprDate(productToAdd.getExprDate());
                productToSave.setStaticProduct(staticProductRepository.findByBarCode(productToAdd.getStaticProduct().getBarCode()));
                changeOldLocationProducts(oldLocation, productToAdd, productToSave);
                ischange=true;
            }
        }
    }

    private void changeOldLocationProducts(String oldLocation, Product productToAdd, Product productToSave) {
        productRepository.save(productToSave);
        Location oldLoc = locationRepository.findByBarCodeLocation(oldLocation);
        for (Product oldStaeProduct : oldLoc.getProducts()) {
            if(oldStaeProduct.getId().equals(productToAdd.getId())){
                oldStaeProduct.setState(oldStaeProduct.getState()-productToAdd.getState());
                if(oldStaeProduct.getState()==0){
                    oldLoc.getProducts().remove(productRepository.getOne(productToAdd.getId()));
                    StaticProduct staticProduct=staticProductRepository.findByBarCode(productToAdd.getStaticProduct().getBarCode());
                    staticProduct.getProducts().remove(productRepository.getOne(productToAdd.getId()));
                    productRepository.delete(productRepository.getOne(productToAdd.getId()));
                    locationRepository.save(oldLoc);
                }
            }

        }
    }

}
