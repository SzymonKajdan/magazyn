package com.example;

import com.example.model.*;
import com.example.repository.*;
import com.example.security.model.Authority;
import com.example.security.model.AuthorityName;
import com.example.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
            sp1.setStaticLocations(new ArrayList<StaticLocation>(Arrays.asList(sl1)));

            StaticProduct sp2 = new StaticProduct();
            sp2.setName("Chleb");
            sp2.setCategory("Żywność");
            sp2.setProducer("Piekarnia");
            sp2.setPrice(210.0);
            sp2.setQuantityOnThePalette(200);
            sp2.setStaticLocations(new ArrayList<StaticLocation>(Arrays.asList(sl2)));

            StaticProduct sp3 = new StaticProduct();
            sp3.setName("Makaron");
            sp3.setCategory("Żywność");
            sp3.setProducer("Lubella");
            sp3.setPrice(90.0);
            sp3.setQuantityOnThePalette(20);
            sp3.setStaticLocations(new ArrayList<StaticLocation>(Arrays.asList(sl1,sl2,sl3)));

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
            p1.setBarCode("000001");
            //p1.setExprDate(new Date("2019-11-02"));
            p1.setExprDate(new Date());
            p1.setState(1000);
            p1.setLocations(new ArrayList<Location>(Arrays.asList(l1)));

            Product p11 = new Product();
            p11.setStaticProduct(sp1);
            p11.setBarCode("000001");
            //p1.setExprDate(new Date("2020-02-22"));
            p11.setExprDate(new Date());
            p11.setState(100);
            p11.setLocations(new ArrayList<Location>(Arrays.asList(l1)));

            Product p2 = new Product();
            p2.setStaticProduct(sp2);
            p2.setBarCode("000002");
            //p2.setExprDate(new Date("2020-02-23"));
            p2.setExprDate(new Date());
            p2.setState(2000);
            p2.setLocations(new ArrayList<Location>(Arrays.asList(l2)));

            Product p3 = new Product();
            p3.setStaticProduct(sp3);
            p3.setBarCode("000003");
            //p3.setExprDate(new Date("2021-02-22"));
            p3.setExprDate(new Date());
            p3.setState(3000);
            p3.setLocations(new ArrayList<Location>(Arrays.asList(l1,l2,l3)));

            productRepository.save(p1);
            productRepository.save(p11);
            productRepository.save(p2);
            productRepository.save(p3);

            //---------------------------
            // PRINCIPAL
            //---------------------------

            Principal pr1 = new Principal();
            pr1.setAddress("Ulica 1/10");
            pr1.setCompanyName("Firma1");
            pr1.setNip("nip");
            pr1.setPhoneNo("111111111");
            pr1.setZipCode("00-000");

            //---------------------------
            // ORDER
            //---------------------------

//            UsedProduct up1 = new UsedProduct();
//            up1.setIdproduct(sp1);
//
//            Order o1 = new Order();
//            o1.setPrincipal(pr1);
//            o1.setDate(new Date());
//            o1.setDepartureDate(new Date());
//            o1.setEndDate(new Date());


        }

    }
}
