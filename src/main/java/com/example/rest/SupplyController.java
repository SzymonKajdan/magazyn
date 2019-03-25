package com.example.rest;

import com.example.model.Location;
import com.example.model.Palette;
import com.example.model.Product;
import com.example.model.Supply;
import com.example.repository.LocationRepository;
import com.example.repository.PaletteRepository;
import com.example.repository.ProductRepository;
import com.example.repository.SupplyRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.parsers.LocationParser.locationParser;
import static com.example.parsers.ProductParser.productJSONParser;
import static com.example.parsers.ProductParser.productParser;
import static com.example.parsers.SupplyParser.supplyParser;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class SupplyController {

    @Autowired
    SupplyRepository supplyRepository;
    @Autowired
    PaletteRepository paletteRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    LocationRepository locationRepository;

    @RequestMapping(path = "/supply/getSupply", method = RequestMethod.POST)
    public ResponseEntity<?> getSupply(@RequestBody String orderRequest) {
        JSONObject supplyJSON = new JSONObject(orderRequest);
        Supply supply = supplyParser(supplyJSON.toString());
        supplyRepository.save(supply);
        return ResponseEntity.ok(new JSONObject().put("Status", OK));
    }

    @RequestMapping(path = "/supply/spreadingSupply", method = RequestMethod.POST)
    public ResponseEntity<?> spreadingSupply(@RequestBody String orderRequest) {
        JSONObject jsonSupply = new JSONObject(orderRequest);
        Supply supply = supplyRepository.findByBarCodeOfSupply(jsonSupply.get("barCodeOfSupply").toString());
        if (supply != null) {
            Palette paletteInSupply = findPallete(supply.getPaletteList(), jsonSupply.getString("barCodeOfPalette"));
            if (paletteInSupply.getId() != 0) {
                List<Location> locationList = locationParser(jsonSupply.get("location").toString());

                Product productInWarehosue = productRepository.findByBarCode(productJSONParser(jsonSupply.get("product").toString()).getBarCode());
                if (productInWarehosue != null) {

                    addProduct(locationList, productInWarehosue);

                }
            }

        }


        return ResponseEntity.ok("ok");
    }

    private void addProduct(List<Location> whereProductWasAdded, Product product) {
        for (Location location:product.getLocations()){

        }
    }

    private Palette findPallete(List<Palette> allPalleteInSupply, String barCodeFromReuqest) {
        Palette p = new Palette();
        for (Palette palette : allPalleteInSupply) {
            if (palette.getBarCode().equals(barCodeFromReuqest)) {
                p = paletteRepository.findByBarCode(barCodeFromReuqest);
            }
        }
        return p;
    }
}
