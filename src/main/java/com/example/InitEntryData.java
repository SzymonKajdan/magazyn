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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Component
public class InitEntryData implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired private UserRepository userRepository;
    @Autowired private AuthorityRepository authorityRepository;
    @Autowired private LocationRepository locationRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PasswordEncoder encoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if(authorityRepository.findByName(AuthorityName.ROLE_ADMIN)==null) {
            Authority adminRole = new Authority();
            adminRole.setName(AuthorityName.ROLE_ADMIN);

            Authority userRole = new Authority();
            userRole.setName(AuthorityName.ROLE_USER);

            authorityRepository.save(adminRole);
            authorityRepository.save(userRole);

            adminRole = authorityRepository.findByName(AuthorityName.ROLE_ADMIN);//.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
            userRole = authorityRepository.findByName(AuthorityName.ROLE_USER);//.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));

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
            location1.setAmountOfProduct(100.0);
            location1.setBarCodeLocation("xdddd");

            Product product1 = new Product();
            product1.setBarCode("xdddd");
            product1.setLocation(location1);
            product1.setPrice(100.0);

            Date date1 = new Date(1000000);

            Order order1 = new Order();
            order1.setDate(date1);
            order1.setEndDate(new Date());
            order1.setPrice(100.0);
            order1.setUser(user);
            order1.setProduct(new ArrayList<Product>(Arrays.asList(product1)));

            locationRepository.save(location1);
            productRepository.save(product1);
            orderRepository.save(order1);

            Location location2 = new Location();
            location2.setAmountOfProduct(120.0);
            location2.setBarCodeLocation("xddd2");

            Product product2 = new Product();
            product2.setBarCode("xdddd2");
            product2.setLocation(location2);
            product2.setPrice(200.0);

            Date date2 = new Date(1020000);

            Order order2 = new Order();
            order2.setDate(date2);
            order2.setEndDate(new Date());
            order2.setPrice(200.0);
            order2.setUser(user);
            order2.setProduct(new ArrayList<Product>(Arrays.asList(product2)));

            locationRepository.save(location2);
            productRepository.save(product2);
            orderRepository.save(order2);

            Location location3 = new Location();
            location3.setAmountOfProduct(120.0);
            location3.setBarCodeLocation("xddd3");

            Product product3 = new Product();
            product3.setBarCode("xdddd3");
            product3.setLocation(location3);
            product3.setPrice(200.0);

            Date date3 = new Date(1003000);

            Order order3 = new Order();
            order3.setDate(date3);
            order3.setEndDate(new Date());
            order3.setPrice(200.0);
            order3.setUser(user);
            order3.setProduct(new ArrayList<Product>(Arrays.asList(product3)));

            locationRepository.save(location3);
            productRepository.save(product3);
            orderRepository.save(order3);
        }
    }
}
