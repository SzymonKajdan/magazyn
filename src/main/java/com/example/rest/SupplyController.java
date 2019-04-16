package com.example.rest;

import com.example.model.*;
import com.example.repository.*;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.parsers.LocationParser.locationParser;
import static com.example.parsers.SupplyParser.supplyParser;

@CrossOrigin(origins = "*", allowCredentials = "true", maxAge = 3600)
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


    @RequestMapping(path = "/Supply/getInfoAboutSupply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getInfoAboutSupply(@RequestBody String supplyRequest) {
        JSONObject jsonObjectBarCode = new JSONObject(supplyRequest);
        String barCode = jsonObjectBarCode.getString("barCodeOfSupply");
        Supply supply = supplyRepository.findByBarCodeOfSupply(barCode);
        if (supply != null) {
            JSONObject supplyResponse = createSupplyResponse(supply);
            return ResponseEntity.ok(supplyResponse.toString());
        } else {
            JSONObject respone = new JSONObject();
            respone.put("Status", "Error");
            return ResponseEntity.ok(respone.toString());
        }

    }

    private JSONObject createSupplyResponse(Supply supply) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", supply.getId());
        jsonObject.put("barCodeOfSupply", supply.getBarCodeOfSupply());
        jsonObject.put("typeOfSupply", supply.getTypeOfSupply());
        jsonObject.put("arriveDate", supply.getArriveDate());
        int amountOfPalletes = supply.getPalettes().size();
        jsonObject.put("aomuntOfPalletes", amountOfPalletes);

        JSONArray jsonArray = new JSONArray();
        supply.getPalettes().forEach((x) -> {
            jsonArray.put(createListOfPalletesToJson(x));

        });
        jsonObject.put("palletes", jsonArray);
        return jsonObject;
    }

    private JSONObject createListOfPalletesToJson(Palette palette) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", palette.getId());
        int amountOfProducts = palette.getUsedProducts().stream().mapToInt(x -> x.getQuanitity()).sum();
        jsonObject.put("amountOfProducts", amountOfProducts);
        return jsonObject;
    }

    //zwraca wszystkie zaopatrzenia wraz z tymi które już były


    @RequestMapping(path = "/Supply/allSupply", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> allSupply() {


        return (ResponseEntity.ok(supplyRepository.findAll()));

    }

    //Zwraca Wszytskie aktwyne zaopatrzenia
    @RequestMapping(path = "/Supply/GetActiveSupplies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(path = "/Supply/addSupply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> addSupply(@RequestBody String supplyRequest) {

        Supply supply = supplyParser(supplyRequest);
        String barCodeOfSupply = supply.getBarCodeOfSupply();
        boolean statusIfSupplyNotExist = checkThatSupplyBarCodeIsUnique(barCodeOfSupply);
        if (statusIfSupplyNotExist) {
            JSONObject response = new JSONObject();
            response.put("Status", "SupplyExist");
            return ResponseEntity.ok(response.toString());
        } else {
            saveUsedProduct(supply.getPalettes());
            supply.setArriveDate(new DateTime().plusDays(4).toDate());

            boolean isPalettesBarCodesIsUnique = checkUniqueBarCodeOfPalettes(supply.getPalettes());
            if (isPalettesBarCodesIsUnique) {

                paletteRepository.saveAll(supply.getPalettes());
                supplyRepository.save(supply);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Status", "Succes");
                jsonObject.put("id", supply.getId());
                return ResponseEntity.ok(jsonObject.toString());
            } else {
                JSONObject response = new JSONObject();
                response.put("Status", "oneOfBarCodeOfPalettesIsNotUnique");
                return ResponseEntity.ok(response.toString());
            }
        }
    }

    private boolean checkUniqueBarCodeOfPalettes(List<Palette> palettes) {
        for (Palette palette : palettes) {
            String barCodeOfPaletee = palette.getBarCode();
            Palette isExsit = paletteRepository.findByBarCode(barCodeOfPaletee);
            if (isExsit != null) {
                return false;
            }

        }
        return true;
    }

    private boolean checkThatSupplyBarCodeIsUnique(String supplyBarCode) {
        Supply isExist = supplyRepository.findByBarCodeOfSupply(supplyBarCode);
        if (isExist != null) {
            return true;
        } else {
            return false;
        }

    }

    //Rozkaldanie zaopatrzenia
    @RequestMapping(path = "/Supply/SpreadingGoods", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> SpreadingGoods(@RequestBody String supplyRequest) {

        JSONObject request = new JSONObject(supplyRequest);
        System.out.println(request.toString());
        String barCode = request.get("barCode").toString();

        Palette paletteInfo = paletteRepository.findByBarCode(barCode);

        JSONArray locationArray = request.getJSONArray("locations");
        List<Location> location = locationParser(locationArray.toString());


        boolean isChangingStateIsGood = changeInfo(paletteInfo, location);
        if (!isChangingStateIsGood) {

            JSONObject response = new JSONObject();
            response.put("Status", "error");
            return ResponseEntity.ok(response.toString());
        } else {
            //dodanie produktu do lokaizacji i zmiana satnu na palecie
            boolean isProductsareAddedtoStack = addProductToStack(location, paletteInfo);
            System.out.println("status " + isProductsareAddedtoStack);

            if (!isProductsareAddedtoStack) {
                JSONObject response = new JSONObject();
                response.put("Status", "error");

                return ResponseEntity.ok(response.toString());
            }


            return ResponseEntity.ok(new JSONObject().put("Status", "ok").toString());
        }

    }


    //zmaina stanu na palecie  bierzemy porudkt z palety  i porudlt z requestu sprawdziamy czy ten ktory zjest z requestu jest w tej lokalizacji
    private boolean changeInfo(Palette paletteInfo, List<Location> location) {

        for (UsedProduct productToSave : paletteInfo.getUsedProducts()) {
            for (Location oneLocation : location) {
                for (Product productFromRequest : oneLocation.getProducts()) {
                    System.out.println(productFromRequest.getStaticProduct().getBarCode());
                    if (productToSave.getBarCodeProduct().equals(productFromRequest.getStaticProduct().getBarCode())) {
                        System.out.println(productToSave.getBarCodeProduct());


                        if (productFromRequest.getState() > productToSave.getQuanitity()) {
                            System.out.println("tutaj 1 ");
                            return false;
                        } else {
                            productToSave.setQuanitity(productToSave.getQuanitity() - productFromRequest.getState());
                        }

                        if (productToSave.getQuanitity() == 0) {
                            productToSave.setPicked(true);
                            usedProductRepository.save(productToSave);

                        } else {
                            usedProductRepository.save(productToSave);

                        }
                    }
                }

            }
        }
        return true;
    }

    private boolean addProductToStack(List<Location> locationListWithInfo, Palette paletteInfo) {

        for (Location oneLocation : locationListWithInfo) {
            String barCodeLocation = oneLocation.getBarCodeLocation();
            Location locationInWareHosue = locationRepository.findByBarCodeLocation(barCodeLocation);

            if (locationInWareHosue == null) {
                System.out.println("tutaj 3 ");
                return false;
            }

            for (Product productToAddToStack : oneLocation.getProducts()) {
                String barCode = productToAddToStack.getStaticProduct().getBarCode();
                StaticProduct staticProduct = staticProductRepository.findByBarCode(barCode);


                if (staticProduct == null) {
                    System.out.println("tutaj 2 ");
                    return false;
                }
                //  boolean isPorductPicked = checkIfProductWasPicekd(productToAddToStack, paletteInfo);
                // if (isPorductPicked) return false;


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
        return true;
    }

    private boolean checkIfProductWasPicekd(Product productToAddToStack, Palette paletteInfo) {
        UsedProduct usedProduct = paletteInfo.getUsedProducts().stream().filter
                (x -> x.getBarCodeProduct().equals(productToAddToStack.getStaticProduct().getBarCode()))
                .findFirst().orElse(null);

        System.out.println(usedProduct.getQuanitity());

        if (usedProduct.isPicked()) {

            return true;

        } else {

            return false;
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

