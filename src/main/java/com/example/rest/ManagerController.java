package com.example.rest;

import com.example.model.Product;
import com.example.repository.AuthorityRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.security.model.Authority;
import com.example.security.model.AuthorityName;
import com.example.security.model.User;
import com.example.service.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/addUserAsWorker", method = RequestMethod.POST)
    public ResponseEntity<?> addUserAsWorker(@RequestBody User u) {

        if(!userRepository.existsByUsername(u.getUsername())) {

            String password = userService.generatePassword();
            u.setPassword(password);

            Authority userRole = authorityRepository.findByName(AuthorityName.ROLE_WORKER);

            u.setAuthorities(Arrays.asList(userRole));

            u.setLastPasswordResetDate(new Date());

            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("username",u.getUsername());
            json.put("password", password);
            json.put("email",u.getEmail());
            json.put("firstname",u.getFirstname());
            json.put("lastname",u.getLastname());

            userService.saveWorker(u);

            return ResponseEntity.ok(json.toString());
        }

        return ResponseEntity.ok("USERNAME ALREADY EXISTS");
    }

    @RequestMapping(path = "/deleteWorker", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteWorker(@RequestBody User u) {

        userService.delete(u);

        return ResponseEntity.ok("SUCCESS");
    }

    @RequestMapping(path = "/workersList", method = RequestMethod.GET)
    public ResponseEntity<?> workersList() {

        Authority worker_a = authorityRepository.findByName(AuthorityName.ROLE_WORKER);

        return ResponseEntity.ok(worker_a.getUsers());
    }

    @RequestMapping(path = "/managersList", method = RequestMethod.GET)
    public ResponseEntity<?> managersList() {

        Authority manager_a = authorityRepository.findByName(AuthorityName.ROLE_MANAGER);

        return ResponseEntity.ok(manager_a.getUsers());
    }
}
