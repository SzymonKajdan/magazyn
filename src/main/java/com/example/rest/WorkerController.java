package com.example.rest;

import com.example.model.Principal;
import com.example.repository.PrincipalRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/client")
public class WorkerController {

    @Autowired PrincipalRepository principalRepository;

    @RequestMapping(path = "/findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllPrincipals() {
        return ResponseEntity.ok(principalRepository.findAll());
    }

    @RequestMapping(path = "/findAllByOrderByCompanyName", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPrincipalsOrderByBrand() {
        return ResponseEntity.ok(principalRepository.findAllByOrderByCompanyName());
    }

    @RequestMapping(path = "/add", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> addPrincipal(@RequestBody Principal p) {

        if(!principalRepository.existsByNip(p.getNip())) {
            principalRepository.save(p);
            JSONObject jo = new JSONObject();

            jo.put("success", true);
            jo.put("status", "OK");
            jo.put("id",p.getId());
            return ResponseEntity.ok(jo.toString());
        }
        else{
            JSONObject jo = new JSONObject();
            jo.put("success", false);
            jo.put("status", "ERROR");
            jo.put("message", "NIP_ALREADY_EXISTS");
            return new ResponseEntity<>(jo.toString(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deletePrincipal(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")) {
            long id = json.getLong("id");
            if(principalRepository.existsById(id)) {
                principalRepository.deleteById(id);

                JSONObject jo = new JSONObject();
                jo.put("success", true);
                jo.put("status", "OK");

                return ResponseEntity.ok(jo.toString());
            }
        }
        if(!json.isNull("nip")){
            String nip = json.getString("nip");
            if(principalRepository.existsByNip(nip)) {
                principalRepository.deleteById(principalRepository.findByNip(nip).getId());
                //principalRepository.deleteByNip(nip);
                JSONObject jo = new JSONObject();
                jo.put("success", true);
                jo.put("status", "OK");

                return ResponseEntity.ok(jo.toString());
            }
        }
        JSONObject jo = new JSONObject();
        jo.put("success", false);
        jo.put("status", "ERROR");
        jo.put("message","NIE ZNALEZIONO");
        return new ResponseEntity<>(jo.toString(), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/find", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> findPrincipal(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")){
            return ResponseEntity.ok(principalRepository.findById(json.getLong("id")));
        }
        if(!json.isNull("nip")){
            return ResponseEntity.ok(principalRepository.findByNip(json.getString("nip")));
        }
        JSONObject jo = new JSONObject();
        jo.put("success", false);
        jo.put("status", "ERROR");
        jo.put("message","NIE ZNALEZIONO");
        return new ResponseEntity<>(jo.toString(), HttpStatus.NOT_FOUND);
    }
}
