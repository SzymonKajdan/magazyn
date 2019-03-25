package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import com.example.security.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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



}
