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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/addUserAsWorker", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",false);
        jsonObject.put("status","ERROR");
        jsonObject.put("message","USERNAME ALREADY EXISTS");
        return ResponseEntity.ok(jsonObject);
    }

    @RequestMapping(path = "/deleteWorker", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deleteWorker(@RequestBody User u) {

        userService.delete(u);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success",true);
        jsonObject.put("status","SUCCESS");
        return ResponseEntity.ok(jsonObject);
    }

    @RequestMapping(path = "/workersList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> workersList() {

        Authority worker_a = authorityRepository.findByName(AuthorityName.ROLE_WORKER);

        return ResponseEntity.ok(worker_a.getUsers());
    }

    @RequestMapping(path = "/managersList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> managersList() {

        Authority manager_a = authorityRepository.findByName(AuthorityName.ROLE_MANAGER);

        return ResponseEntity.ok(manager_a.getUsers());
    }
}
