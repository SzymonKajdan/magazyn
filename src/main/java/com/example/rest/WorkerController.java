package com.example.rest;

import com.example.model.Principal;
import com.example.repository.PrincipalRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

    @RequestMapping(path = "/findAllActive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllActivePrincipals() {
        return ResponseEntity.ok(principalRepository.findAllByEnabled(true));
    }

    @RequestMapping(path = "/findAllDisactive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllDisactivePrincipals() {
        return ResponseEntity.ok(principalRepository.findAllByEnabled(false));
    }

    @RequestMapping(path = "/findAllByOrderByCompanyName", method = RequestMethod.GET)
    public ResponseEntity<?> getAllPrincipalsOrderByBrand() {
        return ResponseEntity.ok(principalRepository.findAllByOrderByCompanyName());
    }

    @RequestMapping(path = "/findAllActiveByOrderByCompanyName", method = RequestMethod.GET)
    public ResponseEntity<?> getAllActivePrincipalsOrderByBrand() {
        return ResponseEntity.ok(principalRepository.findAllByEnabledOrderByCompanyName(true));
    }

    @RequestMapping(path = "/findAllDisactiveByOrderByCompanyName", method = RequestMethod.GET)
    public ResponseEntity<?> getAllDisactivePrincipalsOrderByBrand() {
        return ResponseEntity.ok(principalRepository.findAllByEnabledOrderByCompanyName(false));
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @RequestMapping(path = "/edit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> editPrincipal(@RequestBody Principal p) {

        JSONObject jo = new JSONObject();
        if(p.getId()!=null) {
            if (principalRepository.existsById(p.getId())) {

                if (p.getNip().isEmpty()) {
                    jo.put("success", false);
                    jo.put("status", "ERROR");
                    jo.put("message", "BRAK NIPU");
                    return new ResponseEntity<>(jo.toString(), HttpStatus.CONFLICT);
                }

                principalRepository.save(p);

                jo.put("success", true);
                jo.put("status", "OK");
                return ResponseEntity.ok(jo.toString());
            } else {

                jo.put("success", false);
                jo.put("status", "ERROR");
                jo.put("message", "TAKI KLIENT NIE ISTNIEJE");
                return new ResponseEntity<>(jo.toString(), HttpStatus.CONFLICT);
            }
        }

        jo.put("success", false);
        jo.put("status", "ERROR");
        jo.put("message", "POLE ID JEST PUSTE");
        return new ResponseEntity<>(jo.toString(), HttpStatus.CONFLICT);
    }


    @RequestMapping(path = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deletePrincipal(@RequestBody String s) {

        JSONObject json = new JSONObject(s);
        if(!json.isNull("id")) {
            long id = json.getLong("id");
            if(principalRepository.existsById(id)) {
                //principalRepository.deleteById(id);

                Principal p = principalRepository.findById(id).get();
                p.setEnabled(false);
                principalRepository.save(p);

                JSONObject jo = new JSONObject();
                jo.put("success", true);
                jo.put("status", "OK");

                return ResponseEntity.ok(jo.toString());
            }
        }
        if(!json.isNull("nip")){
            String nip = json.getString("nip");
            if(principalRepository.existsByNip(nip)) {
                //principalRepository.deleteById(principalRepository.findByNip(nip).getId());
                //principalRepository.deleteByNip(nip);

                Principal p = principalRepository.findByNip(nip);
                p.setEnabled(false);
                principalRepository.save(p);

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

    @RequestMapping(path ="getTime",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?>geTime(){
        DateTime dateTime=new DateTime();
        String date = new String();
        //set time zonegit
        date = dateTime.toString(DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss").withZone(DateTimeZone.forID("Europe/Warsaw")));

        JSONObject response=new JSONObject();
        response.put("time",date);
        return ResponseEntity.ok(response.toString());

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
