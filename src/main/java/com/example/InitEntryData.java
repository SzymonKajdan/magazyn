package com.example;

import com.example.model.Location;
import com.example.model.Order;
import com.example.model.Product;
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

            Location location1 = new Location();
            location1.setBarCodeLocation("xdddd");

            Product product1 = new Product();
            product1.setBarCode("xdddd");
            product1.setLocations(new ArrayList<Location>(Arrays.asList(location1)));
            product1.setName("produkt1");
            product1.setPrice(100.0);
            product1.setExprDate(new Date());
            product1.setPrice(100.0);
            product1.setExprDate(new Date(1003000));
            product1.setQuantityOnThePalette(150);
            product1.setName("maka");
            product1.setState(10000);

            Date date1 = new Date(1000000);

            Order order1 = new Order();
            order1.setDate(date1);
            order1.setEndDate(new Date());
            order1.setPrice(100.0);
            order1.setUser(user);


            locationRepository.save(location1);
            productRepository.save(product1);
            orderRepository.save(order1);


            Location location2 = new Location();
            location2.setBarCodeLocation("xddd2");

            Location location4 = new Location();
            location4.setBarCodeLocation("xddd4");

            Product product2 = new Product();
            product2.setBarCode("xdddd2");
            product2.setLocations(new ArrayList<Location>(Arrays.asList(location2, location4)));
            product2.setPrice(200.0);
            product2.setExprDate(new Date(1003000));
            product2.setQuantityOnThePalette(200);
            product2.setState(10000);

            Date date2 = new Date(1020000);

            Order order2 = new Order();
            order2.setDate(date2);
            order2.setEndDate(new Date());
            order2.setPrice(200.0);
            order2.setUser(user);

            locationRepository.save(location2);
            locationRepository.save(location4);
            productRepository.save(product2);
            orderRepository.save(order2);

            Location location3 = new Location();
            location3.setBarCodeLocation("xddd3");

            Product product3 = new Product();
            product3.setBarCode("xdddd3");
            product3.setLocations(new ArrayList<Location>(Arrays.asList(location3)));
            product3.setPrice(200.0);
            product3.setExprDate(new Date());

            product3.setQuantityOnThePalette(90);
            product3.setExprDate(new Date());
            product3.setName("maka");
            product3.setState(10000);

            Date date3 = new Date(1003000);

            Order order3 = new Order();
            order3.setDate(date3);
            order3.setEndDate(new Date());
            order3.setPrice(200.0);
            order3.setUser(user);

            locationRepository.save(location3);
            productRepository.save(product3);
            orderRepository.save(order3);

        }
    }
}
