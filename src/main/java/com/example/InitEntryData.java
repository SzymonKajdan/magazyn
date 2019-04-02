package com.example;

import com.example.model.*;
import com.example.repository.*;
import com.example.security.model.Authority;
import com.example.security.model.AuthorityName;
import com.example.security.model.User;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

            //-----------------------------//

            //--------------------------
            // STATIC LOCATIONS
            //--------------------------

            StaticLocation sl1 = new StaticLocation();
            sl1.setBarCodeLocation("0001");

            StaticLocation sl2 = new StaticLocation();
            sl2.setBarCodeLocation("0002");

            StaticLocation sl3 = new StaticLocation();
            sl3.setBarCodeLocation("0003");

            staticLocationRepository.save(sl1);
            staticLocationRepository.save(sl2);
            staticLocationRepository.save(sl3);

            //--------------------------
            // STATIC PRODUCTS
            //--------------------------

            StaticProduct sp1 = new StaticProduct();
            sp1.setName("Mąka");
            sp1.setCategory("Żywność");
            sp1.setProducer("Złote Pola");
            sp1.setPrice(200.0);
            sp1.setQuantityOnThePalette(100);
            sp1.setBarCode("000001");
            sp1.setLogicState(1100);
            sp1.setStaticLocation(sl1);
            sp1.setProducts(new ArrayList<>());

            StaticProduct sp2 = new StaticProduct();
            sp2.setName("Chleb");
            sp2.setCategory("Żywność");
            sp2.setProducer("Piekarnia");
            sp2.setPrice(210.0);
            sp2.setQuantityOnThePalette(200);
            sp2.setLogicState(2000);
            sp2.setStaticLocation(sl2);
            sp2.setBarCode("000002");
            sp2.setProducts(new ArrayList<>());
            StaticProduct sp3 = new StaticProduct();
            sp3.setName("Makaron");
            sp3.setCategory("Żywność");
            sp3.setProducer("Lubella");
            sp3.setPrice(90.0);
            sp3.setQuantityOnThePalette(20);
            sp3.setLogicState(3000);
            sp3.setStaticLocation(sl3);
            sp3.setBarCode("000003");
            sp3.setProducts(new ArrayList<>());

            staticProductRepository.save(sp1);
            staticProductRepository.save(sp2);
            staticProductRepository.save(sp3);

            //---------------------------
            // LOCATIONS
            //---------------------------

            Location l1 = new Location();
            l1.setBarCodeLocation(sl1.getBarCodeLocation());

            Location l2 = new Location();
            l2.setBarCodeLocation(sl2.getBarCodeLocation());

            Location l3 = new Location();
            l3.setBarCodeLocation(sl3.getBarCodeLocation());

            locationRepository.save(l1);
            locationRepository.save(l2);
            locationRepository.save(l3);

            //---------------------------
            // PRODUCTS
            //---------------------------

            Product p1 = new Product();
            p1.setStaticProduct(sp1);
            String dateTime = "10/12/2020 08:00:00";
            // Format for input
            DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
            //p1.setExprDate(new Date("2019-11-02"));
            p1.setExprDate(new DateTime().toString(DateTimeFormat.shortDateTime()));
            p1.setState(1000);
            p1.setLocations(new ArrayList<Location>(Arrays.asList(l1)));

            Product p11 = new Product();
            p11.setStaticProduct(sp1);

            //p1.setExprDate(new Date("2020-02-22"));
            p11.setExprDate(new DateTime().toString(DateTimeFormat.shortDateTime()));
            p11.setState(100);
            p11.setLocations(new ArrayList<Location>(Arrays.asList(l1)));

            Product p2 = new Product();
            p2.setStaticProduct(sp2);

            //p2.setExprDate(new Date("2020-02-23"));
            p2.setExprDate(new DateTime().toString(DateTimeFormat.shortDateTime()));
            p2.setState(2000);
            p2.setLocations(new ArrayList<Location>(Arrays.asList(l2)));

            Product p3 = new Product();
            p3.setStaticProduct(sp3);

            //p3.setExprDate(new Date("2021-02-22"));
            p3.setExprDate(new DateTime().toString(DateTimeFormat.shortDateTime()));
            p3.setState(3000);
            p3.setLocations(new ArrayList<Location>(Arrays.asList(l1, l2, l3)));

            productRepository.save(p1);

            productRepository.save(p11);

            sp1.getProducts().add(p11);
            sp1.getProducts().add(p1);
            staticProductRepository.save(sp1);

            productRepository.save(p2);

            sp2.getProducts().add(p2);
            staticProductRepository.save(sp2);


            productRepository.save(p3);
            sp3.getProducts().add(p3);
            staticProductRepository.save(sp3);

            //---------------------------
            // PRINCIPAL
            //---------------------------

            Principal pr1 = new Principal();
            pr1.setAddress("Ulica 1/10");
            pr1.setCompanyName("Firma1");
            pr1.setNip("nip00000");
            pr1.setPhoneNo("111111111");
            pr1.setZipCode("00-000");

            principalRepository.save(pr1);

            //---------------------------
            // ORDER
            //---------------------------

            UsedProduct up1 = new UsedProduct();
            up1.setIdStaticProduct(sp1.getId());
            up1.setPicked(true);
            up1.setQuanitity(10);

            // ze statica usuwamy z logicstate zeby wiedziec ile zostało niezamowionych produktow
            sp1.setLogicState(sp1.getLogicState() - 10);

            Order o1 = new Order();
            o1.setPrincipal(pr1);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                o1.setDate(formatter.parse("2019-05-05"));
                o1.setDepartureDate(formatter.parse("2019-05-05"));
                o1.setEndDate(formatter.parse("2019-05-05"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            o1.setPrice(sp1.getPrice());
            o1.setUser(user);
            o1.setUsedProductList(new ArrayList<>(Arrays.asList(up1)));

            usedProductRepository.save(up1);
            orderRepository.save(o1);

            // kompletowanie zamowienia

            // pracownik pobiera liste produktow i wybiera
            sp1.getProducts();

            // po tym co wybierze usuwa sie ze stanu produktu
            p1.setState(p1.getState() - 10);
        }

    }
}
