package com.example.rest;

import com.example.model.Principal;
import com.example.model.Product;
import com.example.repository.PrincipalRepository;
import com.example.repository.ProductRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProduktController {

    @Autowired
    ProductRepository productRepository;

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

//    @RequestMapping(path = "/findAllByOrderByName", method = RequestMethod.GET)
//    public ResponseEntity<?> getAllProductsOrderByName() {
//        return ResponseEntity.ok(productRepository.findAllByOrderByName());
//    }

    @RequestMapping(path = "/add", method = RequestMethod.PUT)
    public ResponseEntity<?> addProduct(@RequestBody Product p) {

        if(!productRepository.existsByBarCode(p.getBarCode())) {

            productRepository.save(p);
            return ResponseEntity.ok("Success");
        }
        else{
            return new ResponseEntity<>("PRODUCT_ALREADY_EXISTS", HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProduct(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")) {
            long id = json.getLong("id");
            if(productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return ResponseEntity.ok("Success");
            }
        }
        if(!json.isNull("barCode")){
            String barCode = json.getString("barCode");
            if(productRepository.existsByBarCode(barCode)) {
                productRepository.deleteById(productRepository.findByBarCode(barCode).getId());
                //productRepository.deleteByBarCode(barCode);
                return ResponseEntity.ok("Success");
            }
        }
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    public ResponseEntity<?> findProduct(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")){
            return ResponseEntity.ok(productRepository.findById(json.getLong("id")));
        }
        if(!json.isNull("barCode")){
            return ResponseEntity.ok(productRepository.findByBarCode(json.getString("barCode")));
        }
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }

}
