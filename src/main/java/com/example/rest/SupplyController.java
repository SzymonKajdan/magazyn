package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.parsers.LocationParser.locationParser;
import static com.example.parsers.SupplyParser.supplyParser;

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
    @Autowired
    StaticProductRepository staticProductRepository;
    @Autowired
    StaticLocationRepository staticLocationsRepository;
    @Autowired
    UsedProductRepository usedProductRepository;


    //zwraca wszystkie zaopatrzenia wraz z tymi które już były
    @RequestMapping(path = "/Supply/allSupply", method = RequestMethod.GET)
    public ResponseEntity<?> allSupply() {
        return (ResponseEntity.ok(supplyRepository.findAll()));

    }

    //Zwraca Wszytskie aktwyne zaopatrzenia
    @RequestMapping(path = "/Supply/GetActiveSupplies", method = RequestMethod.GET)
    public ResponseEntity<?> getActiveSupplies() {
        return ResponseEntity.ok(supplyRepository.findByStatus(false));
    }

    //Zakoncznie rozkaldania zaopatrzenia
    @RequestMapping(path = "/Supply/FinishSpreadingGoods", method = RequestMethod.POST)
    public ResponseEntity<?> finishSpreadingGoods(@RequestBody String supplyRequest) {

        //tworze obiekt na podstwaie otrzymanego jsona -> sqlect po kodzie kreskowym
        Supply supply = supplyParser(supplyRequest);
        Supply suppplyToSave = supplyRepository.findByBarCodeOfSupply(supply.getBarCodeOfSupply());

        //Metoda sprawdza czy  paleta zostala oprózniona
        if (chceckGoods(suppplyToSave.getPalettes())) {
             suppplyToSave.setStatus(true);
            supplyRepository.save(suppplyToSave);

            return ResponseEntity.ok(new JSONObject().put("Status", "OK").toString());
        } else {
            return ResponseEntity.ok(new JSONObject().put("Status", "ERROR").toString());
        }
    }

    private boolean chceckGoods(List<Palette> palettes) {
        for (Palette p : palettes)
            for (UsedProduct product : p.getUsedProducts()) {
                if (product.getQuanitity() != 0) {
                    return false;
                }
            }
        return true;
    }

    //dodowanie nowego zaopatrzenia z storny internetowej
    @RequestMapping(path = "/Supply/addSupply", method = RequestMethod.POST)
    public ResponseEntity<?> addSupply(@RequestBody String supplyRequest) {

        Supply supply = supplyParser(supplyRequest);
        saveUsedProduct(supply.getPalettes());
        supply.setArriveDate(new Date());

        paletteRepository.saveAll(supply.getPalettes());
        supplyRepository.save(supply);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Status", "Succes");
        jsonObject.put("id", supply.getId());
        return ResponseEntity.ok(jsonObject.toString());

    }

    //Rozkaldanie zaopatrzenia
    @RequestMapping(path = "/Supply/SpreadingGoods", method = RequestMethod.POST)
    public ResponseEntity<?> SpreadingGoods(@RequestBody String supplyRequest) {

        JSONObject request = new JSONObject(supplyRequest);
        String barCode=request.get("barCode").toString();

        Palette paletteInfo = paletteRepository.findByBarCode(barCode);

        JSONArray locationArray=request.getJSONArray("locations");
        List<Location> location = locationParser(locationArray.toString());

        //dodanie produktu do lokaizacji i zmiana satnu na palecie
        addProductToStack(location);
        changeInfo(paletteInfo, location);

        return ResponseEntity.ok(new JSONObject().put("Status", "ok").toString());

    }

    //zmaina stanu na palecie  bierzemy porudkt z palety  i porudlt z requestu sprawdziamy czy ten ktory zjest z requestu jest w tej lokalizacji
    private void changeInfo(Palette paletteInfo, List<Location> location) {

        for (UsedProduct productToSave : paletteInfo.getUsedProducts()) {
            for (Location oneLocation : location) {
                for (Product productFromRequest : oneLocation.getProducts()) {
                    System.out.println(productFromRequest.getStaticProduct().getBarCode());
                    if (productToSave.getBarCodeProduct().equals(productFromRequest.getStaticProduct().getBarCode())) {
                        System.out.println(productToSave.getBarCodeProduct());
                        productToSave.setQuanitity(productToSave.getQuanitity() - productFromRequest.getState());
                        if (productToSave.getQuanitity() == 0) {
                            productToSave.setPicked(true);
                            usedProductRepository.save(productToSave);
                        }


                    }
                }

            }
        }
    }

    private void addProductToStack(List<Location> locationListWithInfo) {

        for (Location oneLocation : locationListWithInfo) {
            String barCodeLocation=oneLocation.getBarCodeLocation();
            Location locationInWareHosue = locationRepository.findByBarCodeLocation(barCodeLocation);

            for (Product productToAddToStack : oneLocation.getProducts()) {

                String barCode=productToAddToStack.getStaticProduct().getBarCode();
                StaticProduct staticProduct = staticProductRepository.findByBarCode(barCode);
                staticProduct.setLogicState(staticProduct.getLogicState() + productToAddToStack.getState());

                //System.out.println(locationInWareHosue.getBarCodeLocation());
                productToAddToStack.setStaticProduct(staticProduct);
                productToAddToStack.setLocations(new ArrayList<>());
                productToAddToStack.getLocations().add(locationInWareHosue);

                productRepository.save(productToAddToStack);

                staticProduct.getProducts().add(productToAddToStack);
                staticProductRepository.save(staticProduct);

            }
        }
    }

    private void saveUsedProduct(List<Palette> palettes) {
        for (Palette pallete : palettes) {

            for (UsedProduct product : pallete.getUsedProducts()) {
                usedProductRepository.save(product);


            }

        }

    }
}

