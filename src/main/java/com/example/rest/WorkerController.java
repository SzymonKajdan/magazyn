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
@RequestMapping("/client")
public class WorkerController {

    @Autowired PrincipalRepository principalRepository;

    @RequestMapping(path = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPrincipals() {
        return ResponseEntity.ok(principalRepository.findAll());
    }

    @RequestMapping(path = "/findAllByOrderByCompanyName", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPrincipalsOrderByBrand() {
        return ResponseEntity.ok(principalRepository.findAllByOrderByCompanyName());
    }

    @RequestMapping(path = "/add", method = RequestMethod.PUT)
    public ResponseEntity<?> addPrincipal(@RequestBody Principal p) {

        if(!principalRepository.existsByNip(p.getNip())) {
            principalRepository.save(p);
            return ResponseEntity.ok("Success");
        }
        else{
            return new ResponseEntity<>("NIP_ALREADY_EXISTS", HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePrincipal(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")) {
            long id = json.getLong("id");
            if(principalRepository.existsById(id)) {
                principalRepository.deleteById(id);
                return ResponseEntity.ok("Success");
            }
        }
        if(!json.isNull("nip")){
            String nip = json.getString("nip");
            if(principalRepository.existsByNip(nip)) {
                principalRepository.deleteById(principalRepository.findByNip(nip).getId());
                //principalRepository.deleteByNip(nip);
                return ResponseEntity.ok("Success");
            }
        }
        return new ResponseEntity<>("NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET)
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
