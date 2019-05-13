package com.example.rest;

import com.example.model.Location;
import com.example.model.Product;
import com.example.repository.LocationRepository;
import com.example.repository.StaticProductRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.example.parsers.LocationParser.locationParserOneLocation;
@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
@Controller
@RequestMapping(path = "/Location")
public class LocationController {
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    StaticProductRepository staticProductRepository;
    @Autowired
    ProductController productController;

    @RequestMapping(path = "/infoAboutLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> infoAboutLocation(@RequestBody  String location) {
        JSONObject jsonObjectLocation = new JSONObject(location);
        Location locationFromRequest = locationParserOneLocation(jsonObjectLocation.toString());

        String barCodeLocation = locationFromRequest.getBarCodeLocation();
        Location locationInDb = locationRepository.findByBarCodeLocation(barCodeLocation);
        if (locationInDb != null) {
            JSONObject jsonObjectToRespone = jsonToRespone(locationInDb);
            return ResponseEntity.ok(jsonObjectToRespone.toString());


        } else {
            return ResponseEntity.ok(null);
        }
    }
    @RequestMapping(path = "/locationCheck",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?>locationCheck(@RequestBody String location){
        JSONObject jsonObjectLocation = new JSONObject(location);
        Location locationFromRequest = locationParserOneLocation(jsonObjectLocation.toString());

        String barCodeLocation = locationFromRequest.getBarCodeLocation();
        Location locationInDb = locationRepository.findByBarCodeLocation(barCodeLocation);
        if (locationInDb != null) {
            JSONObject jsonObjectToRespone = new JSONObject();
            jsonObjectToRespone.put("status","Exist");

            return ResponseEntity.ok(jsonObjectToRespone.toString());


        } else {
            JSONObject jsonObjectToRespone = new JSONObject();

            jsonObjectToRespone.put("status","NotExist");
            return ResponseEntity.ok(jsonObjectToRespone.toString());

        }
    }

    private JSONObject jsonToRespone(Location location) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", location.getId());
        jsonObject.put("barCodeLocation", location.getBarCodeLocation());
        JSONArray jsonArrayWihtProductsInLocation = new JSONArray();
        for (Product product : location.getProducts()) {
            JSONObject object = new JSONObject();
            JSONObject staticIfno = new JSONObject();
            JSONObject staticLocation =new JSONObject();
            staticIfno.put("price", product.getStaticProduct().getPrice());
            staticIfno.put("id", product.getStaticProduct().getId());
            staticIfno.put("quantityOnThePalette", product.getStaticProduct().getQuantityOnThePalette());
            staticIfno.put("producer", product.getStaticProduct().getProducer());
            staticIfno.put("barCode", product.getStaticProduct().getBarCode());
            staticIfno.put("name", product.getStaticProduct().getName());
            staticIfno.put("logicState", product.getStaticProduct().getLogicState());
            staticIfno.put("quantityInPackage",product.getStaticProduct().getAmountInAPack());
            staticLocation.put("barCodeLocation",product.getStaticProduct().getStaticLocation().getBarCodeLocation());
            staticIfno.put("staticLocations", staticLocation);
            object.put("id", product.getId());
            object.put("exprDate", product.getExprDate());
            object.put("state", product.getState());
            object.put("staticProduct", staticIfno);
            jsonArrayWihtProductsInLocation.put(object);
        }
        jsonObject.put("products", jsonArrayWihtProductsInLocation);
        return jsonObject;
    }
}
