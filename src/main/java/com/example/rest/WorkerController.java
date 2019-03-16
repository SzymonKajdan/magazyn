package com.example.rest;

import com.example.model.Principal;
import com.example.repository.PrincipalRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkerController {

    @Autowired PrincipalRepository principalRepository;

    @RequestMapping(path = "/clients", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPrincipals() {
        return ResponseEntity.ok(principalRepository.findAll());
    }

    @RequestMapping(path = "/clientsOrderBy", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPrincipalsOrderByBrand() {
        return ResponseEntity.ok(principalRepository.findAll());
    }

    @RequestMapping(path = "/clients", method = RequestMethod.PUT)
    public ResponseEntity<?> addPrincipal(@RequestBody Principal p) {
        principalRepository.save(p);
        return ResponseEntity.ok("Success");
    }

    @RequestMapping(path = "/clients", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePrincipal(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")) {
            principalRepository.deleteById(json.getLong("id"));
            return ResponseEntity.ok("Success");
        }
        if(!json.isNull("nip")){
            principalRepository.deleteByNip(json.getString("nip"));
            return ResponseEntity.ok("Success");
        }
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<?> findPrincipal(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")){
            return ResponseEntity.ok(principalRepository.findById(json.getLong("id")));
        }
        if(!json.isNull("nip")){
            return ResponseEntity.ok(principalRepository.findByNip(json.getString("nip")));
        }
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
