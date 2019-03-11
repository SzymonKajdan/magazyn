package com.example.rest;

import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MainRestService {

    @Autowired UserRepository userRepository;
    @Autowired OrderRepository orderRepository;

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
