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
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        int amountOfPalletes = supply.getPalettes().size();

        jsonObject.put("id", supply.getId());
        jsonObject.put("barCodeOfSupply", supply.getBarCodeOfSupply());
        jsonObject.put("typeOfSupply", supply.getTypeOfSupply());
        jsonObject.put("arriveDate", supply.getArriveDate());
        jsonObject.put("aomuntOfPalletes", amountOfPalletes);


        supply.getPalettes().forEach((x) -> {
            jsonArray.put(createListOfPalletesToJson(x));

        });

        jsonObject.put("palletes", jsonArray);
        return jsonObject;
    }

    private JSONObject createListOfPalletesToJson(Palette palette) {
        JSONObject jsonObject = new JSONObject();
        int amountOfProducts = palette.getUsedProducts().stream().mapToInt(x -> x.getQuanitity()).sum();

        jsonObject.put("id", palette.getId());
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
        boolean statusOfBarCodePalettes = checkThatBarCodeOfPalettesIsNotNull(supply.getPalettes());

        if (statusOfBarCodePalettes == false) {
            JSONObject response = new JSONObject();
            response.put("Status", "PaletteBarCodeNull");
            return ResponseEntity.ok(response.toString());
        }

        if (barCodeOfSupply == null) {
            JSONObject response = new JSONObject();
            response.put("Status", "BarCodeNull");
            return ResponseEntity.ok(response.toString());
        }


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

    private boolean checkThatBarCodeOfPalettesIsNotNull(List<Palette> paletteList) {
        for (Palette palette : paletteList) {
            String barCode = palette.getBarCode();
            if (barCode == null) {
                return false;
            }
        }
        return true;
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

        Palette paletteInDb = paletteRepository.findByBarCode(barCode);
        if (paletteInDb == null) {
            JSONObject response = new JSONObject();
            response.put("status", "PALLETE_" + barCode + "_NOT_EXIST");
            return ResponseEntity.ok(response.toString());
        }
        JSONArray locationArray = request.getJSONArray("locations");
        List<Location> requestLocations = locationParser(locationArray.toString());

        String locationStatus = cheeckLocations(requestLocations);

        if (locationStatus.equals("OK")) {
            String statusOfSpreadingGoods = getGoodsFromPaletteToLocation(paletteInDb, requestLocations);
            if (!statusOfSpreadingGoods.equals("OK")) {
                JSONObject response = new JSONObject();
                response.put("status", statusOfSpreadingGoods);
                return ResponseEntity.ok(response.toString());
            } else {
                JSONObject response = new JSONObject();
                response.put("status", "OK");
                return ResponseEntity.ok(response.toString());
            }

        } else {
            JSONObject response = new JSONObject();
            response.put("status", locationStatus);
            return ResponseEntity.ok(response.toString());

        }

    }

    private String getGoodsFromPaletteToLocation(Palette paletteInDb, List<Location> requestLocations) {
        for (Location location : requestLocations) {
            for (Product p : location.getProducts()) {
                String barCode = p.getStaticProduct().getBarCode();
                StaticProduct staticProduct = staticProductRepository.findByBarCode(barCode);
                if (staticProduct == null) {
                    return "Product_" + barCode + "_NOT_EXIST";
                } else {

                    UsedProduct productInThePalette = findInPalette(paletteInDb, barCode);
                    if (productInThePalette == null) {
                        return "Product_" + barCode + "_NOT_EXIST_IN_PALETTE" + paletteInDb.getBarCode();
                    } else {
                        boolean isSpredingSucces = spreadGood(location, productInThePalette, p, staticProduct);
                        if (!isSpredingSucces) {
                            return "SPREADING_ERROR";
                        }
                    }

                }
            }
        }
        return "OK";

    }

    private boolean spreadGood(Location location, UsedProduct productInThePalette, Product p, StaticProduct productInDb) {
        if (productInThePalette.getQuanitity() < p.getState()) {
            return false;
        } else {
            int quantityInPalette = productInThePalette.getQuanitity();
            productInThePalette.setQuanitity(quantityInPalette - p.getState());
            if (productInThePalette.getQuanitity() == 0) {
                productInThePalette.setPicked(true);
            }

            String barCodeLocationOfProdcutNewPart = location.getBarCodeLocation();
            Location locationInDb = locationRepository.findByBarCodeLocation(barCodeLocationOfProdcutNewPart);

            int loggicState = productInDb.getLogicState();
            productInDb.setLogicState(loggicState + p.getState());
            p.setExprDate(productInThePalette.getExprDate());
            p.setStaticProduct(productInDb);
            p.setLocations(new ArrayList<>());
            p.getLocations().add(locationInDb);
            productRepository.save(p);

            productInDb.getProducts().add(p);
            staticProductRepository.save(productInDb);

            return true;
        }
    }

    private UsedProduct findInPalette(Palette paletteInDb, String barCode) {
        UsedProduct usedProduct = paletteInDb.getUsedProducts().stream().filter(x -> x.getBarCodeProduct().equals(barCode)).findFirst().orElse(null);
        return usedProduct;
    }

    private String cheeckLocations(List<Location> requestLocations) {

        for (Location location : requestLocations) {
            Location inDb = locationRepository.findByBarCodeLocation(location.getBarCodeLocation());
            if (inDb == null) {
                return "NO_LOCATION_" + location.getBarCodeLocation();
            }

        }
        return "OK";
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
                if (product.getExprDate() == null) {
                    product.setExprDate(new DateTime().plusYears(1).toDate());
                }
                usedProductRepository.save(product);
            }
        }
    }
}

