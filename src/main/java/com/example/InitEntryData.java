package com.example;

import com.example.model.*;
import com.example.repository.*;
import com.example.security.model.Authority;
import com.example.security.model.AuthorityName;
import com.example.security.model.User;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class InitEntryData implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StaticProductRepository staticProductRepository;
    @Autowired
    private StaticLocationRepository staticLocationRepository;
    @Autowired
    private PrincipalRepository principalRepository;
    @Autowired
    private UsedProductRepository usedProductRepository;
    @Autowired
    private PasswordEncoder encoder;

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
    private Date convertStringToDate(String stringDate){
        String  s="09.08.2019";
        String[] spitletedDate =stringDate.split(Pattern.quote("."));
        int year=Integer.parseInt(spitletedDate[2]);
        int month=Integer.parseInt(spitletedDate[1]);
        int day=Integer.parseInt(spitletedDate[0]);

        return  new DateTime(year,month,day,8,0,0).toDate();

    }
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if (authorityRepository.findByName(AuthorityName.ROLE_MANAGER) == null) {
            Authority adminRole = new Authority();
            adminRole.setName(AuthorityName.ROLE_MANAGER);

            Authority userRole = new Authority();
            userRole.setName(AuthorityName.ROLE_WORKER);

            authorityRepository.save(adminRole);
            authorityRepository.save(userRole);

            adminRole = authorityRepository.findByName(AuthorityName.ROLE_MANAGER);//.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
            userRole = authorityRepository.findByName(AuthorityName.ROLE_WORKER);//.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));

            User admin = new User();
            admin.setEmail("admin@email.pl");
            admin.setUsername("admin");
            admin.setLastname("admin");
            admin.setFirstname("admin");
            admin.setLastPasswordResetDate(new Date());
            admin.setEnabled(true);
            admin.setPassword(encoder.encode("qwerty"));
            admin.setAuthorities(new ArrayList<Authority>(Arrays.asList(adminRole)));

            User user = new User();
            user.setEmail("user@email.pl");
            user.setUsername("user");
            user.setLastname("user");
            user.setFirstname("user");
            user.setLastPasswordResetDate(new Date());
            user.setEnabled(true);
            user.setPassword(encoder.encode("qwerty"));
            user.setAuthorities(new ArrayList<Authority>(Arrays.asList(userRole)));

            userRepository.save(admin);
            userRepository.save(user);


            List<List<String>> records = new ArrayList<>();
            try (Scanner scanner = new Scanner(new File("InitData"));) {
                while (scanner.hasNextLine()) {
                    records.add(getRecordFromLine(scanner.nextLine()));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            for(List<String> strings:records){
                StaticLocation staticLocation=new StaticLocation();
                staticLocation.setBarCodeLocation(strings.get(2));
                staticLocationRepository.save(staticLocation);

                Random r = new Random();
                double randomValue = 0.50 + (20.0 - 0.50) * r.nextDouble();

                StaticProduct staticProduct=new StaticProduct();
                staticProduct.setName(strings.get(1));
                staticProduct.setCategory(strings.get(7));
                staticProduct.setProducer(strings.get(0));
                staticProduct.setPrice(randomValue);
                staticProduct.setAmountInAPack(Integer.parseInt(strings.get(6)));
                staticProduct.setQuantityOnThePalette(Integer.parseInt(strings.get(5)));
                staticProduct.setBarCode(strings.get(3));
                staticProduct.setLogicState(Integer.parseInt(strings.get(5)));
                staticProduct.setStaticLocation(staticLocation);
                staticProduct.setProducts(new ArrayList<>());
                staticProductRepository.save(staticProduct);

                Location location = new Location();
                location.setBarCodeLocation(staticLocation.getBarCodeLocation());
                locationRepository.save(location);


                Product product = new Product();
                product.setStaticProduct(staticProduct);
                System.out.println(strings.get(4));
                if(strings.get(4).equals("null")) {

                }else{
                    product.setExprDate(convertStringToDate(strings.get(4)));
                }
                product.setState(staticProduct.getLogicState());
                product.setLocations(new ArrayList<Location>(Arrays.asList(location)));
                productRepository.save(product);

                staticProduct.getProducts().add(product);
                staticProductRepository.save(staticProduct);

            }
            Principal principal=new Principal();
            principal.setNip("124455667");
            principal.setPhoneNo("790540834");
            principal.setAddress("adres 1");
            principal.setCompanyName("Firma1");
            principal.setZipCode("32-340");
            principalRepository.save(principal);



            StaticProduct sp1=staticProductRepository.findByBarCode("1655409103");




            UsedProduct up1 = new UsedProduct();
            up1.setIdStaticProduct(sp1.getId());
            up1.setPicked(false);
            up1.setQuanitity(100);

            // ze statica usuwamy z logicstate zeby wiedziec ile zostało niezamowionych produktow
            sp1.setLogicState(sp1.getLogicState()-100);

            Order o1 = new Order();
            o1.setPrincipal(principal);



                DateTime dateTime = new DateTime().withHourOfDay(8);
                dateTime = dateTime.plusDays(2);

                o1.setDepartureDate(dateTime.toDate());



            o1.setPrice(sp1.getPrice()*100);
            o1.setUser(user);
            o1.setUsedProductList(new ArrayList<>(Arrays.asList(up1)));

            usedProductRepository.save(up1);
            orderRepository.save(o1);
            System.out.println(o1.getId());
            // kompletowanie zamowienia



        }

    }
}
