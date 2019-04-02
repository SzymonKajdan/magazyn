package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.Counters.CounterPhysicalState.countPhyscialState;
import static com.example.parsers.OrderParser.orderParser;
import static com.example.parsers.PrincipalParser.*;
import static com.example.parsers.UsedProductParser.usedProductParser;
@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
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






}
