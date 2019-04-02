package com.example.rest;

import com.example.model.Principal;
import com.example.model.Product;
import com.example.repository.PrincipalRepository;
import com.example.repository.ProductRepository;
import com.example.repository.StaticProductRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StaticProductRepository staticProductRepository;

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(staticProductRepository.findAll());
    }

//    @RequestMapping(path = "/findAllByOrderByName", method = RequestMethod.GET)
//    public ResponseEntity<?> getAllProductsOrderByName() {
//        return ResponseEntity.ok(productRepository.findAllByOrderByName());
//    }

    @RequestMapping(path = "/add", method = RequestMethod.PUT)
    public ResponseEntity<?> addProduct(@RequestBody Product p) {

            productRepository.save(p);
            return ResponseEntity.ok("Success");

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
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
    public ResponseEntity<?> findProduct(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")){
            return ResponseEntity.ok(productRepository.findById(json.getLong("id")));
        }
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }

}
