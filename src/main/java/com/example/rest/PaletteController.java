package com.example.rest;

import com.example.model.Palette;
import com.example.model.StaticProduct;
import com.example.model.UsedProduct;
import com.example.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, path = "/Palette")
public class PaletteController {
    @Autowired
    SupplyRepository supplyRepository;
    @Autowired
    PaletteRepository paletteRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    StaticProductRepository staticProductRepository;
    @Autowired
    StaticLocationRepository staticLocationsRepository;
    @Autowired
    UsedProductRepository usedProductRepository;


    @RequestMapping(path = "/getInfoAboutPalette", method = RequestMethod.POST)
    private ResponseEntity<?> getInfoAboutPalette(@RequestBody String idString) {
        JSONObject jsonObject = new JSONObject(idString);
        long id = jsonObject.getLong("id");
        Palette palette = paletteRepository.getOne(id);
        if (palette == null) {
            return ResponseEntity.ok(new JSONObject().put("Status", "error").toString());
        } else {
            JSONObject response = createPalletesJSON(palette);

            return ResponseEntity.ok(response.toString());
        }


    }

    private JSONObject createPalletesJSON(Palette palette) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        palette.getUsedProducts().forEach((x) -> {
            jsonArray.put(createProductsInPaleeteJSON(x));
        });
        jsonObject.put("products", jsonArray);
        jsonObject.put("barCode",palette.getBarCode());
        return jsonObject;
    }

    private JSONObject createProductsInPaleeteJSON(UsedProduct usedProduct) {

        StaticProduct product = staticProductRepository.getOne(usedProduct.getIdStaticProduct());
        JSONObject jsonToReturn = new JSONObject();

        JSONObject productJSON = new JSONObject();
        productJSON.put("id", product.getId());
        productJSON.put("name", product.getName());
        productJSON.put("producer", product.getProducer());
        productJSON.put("barCode", product.getBarCode());
        productJSON.put("category", product.getCategory());
        productJSON.put("quantityOnThePalette", product.getQuantityOnThePalette());
        productJSON.put("amountInAPack", product.getAmountInAPack());

        JSONObject location = new JSONObject();
        location.put("id", product.getStaticLocation().getId());
        location.put("barCodeLocation", product.getStaticLocation().getBarCodeLocation());

        productJSON.put("staticLocation", location);
        productJSON.put("quantityInPackage", product.getAmountInAPack());

        jsonToReturn.put("product", productJSON);
        jsonToReturn.put("quantity", usedProduct.getQuanitity());
        return jsonToReturn;
    }

}
