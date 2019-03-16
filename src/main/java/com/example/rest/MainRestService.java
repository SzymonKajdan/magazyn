package com.example.rest;

import com.example.model.Principal;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.example.parsers.PrincipalParser.parsePrincipal;

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

    @RequestMapping(path = "/createOrder", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody  String order) {
        System.out.println(order);
        JSONObject jsonOrder=new JSONObject(order);
        Principal principal= (Principal) parsePrincipal(jsonOrder.get("principal").toString());
        System.out.println(principal.getNip());


        return ResponseEntity.ok("");
    }
}
