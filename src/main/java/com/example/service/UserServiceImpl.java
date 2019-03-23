package com.example.service;

import com.example.repository.AuthorityRepository;
import com.example.repository.UserRepository;
import com.example.security.model.Authority;
import com.example.security.model.AuthorityName;
import com.example.security.model.User;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private AuthorityRepository authorityRepository;

    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private JdbcTemplate jdbc;

    @Override
    public void saveWorker(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        Authority worker_a = authorityRepository.findByName(AuthorityName.ROLE_WORKER);
        user.setAuthorities(new ArrayList<Authority>(Arrays.asList(worker_a)));
        userRepository.save(user);
    }

    @Override
    public void saveManager(User user) {

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        Authority manager_a = authorityRepository.findByName(AuthorityName.ROLE_MANAGER);
        user.setAuthorities(new ArrayList<Authority>(Arrays.asList(manager_a)));
        userRepository.save(user);
    }

    @Override
    public void delete(User user) {

//        jdbc.execute("DELETE FROM USER_ROLE WHERE USER_ID ="+user.getId());
//        jdbc.execute("DELETE FROM USER_MA_PASSES WHERE USER_ID ="+user.getId());
//        jdbc.execute("DELETE FROM USER_GYM_PASSES WHERE USER_ID ="+user.getId());
//        jdbc.execute("DELETE FROM MAOFFER_CLIENTS WHERE CLIENTS_ID ="+user.getId());

        if(user.getId()!=null) {
            long id = user.getId();
            if(userRepository.existsById(id)) {
                userRepository.deleteById(id);
            }
        }
        if(user.getUsername()!=null){
            if(userRepository.existsByUsername(user.getUsername())) {
                userRepository.deleteById(userRepository.findByUsername(user.getUsername()).getId());
            }
        }

        userRepository.delete(user);
    }

    @Override
    public String generatePassword() {
        //String newPass = RandomStringUtils.randomAscii(8);
        //return newPass;

        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('!', 'z').build();
        String randomLetters = generator.generate(8);
        return randomLetters;
    }

    public String ecryptPassword(String str){
        return bCryptPasswordEncoder.encode(str);
    }
}
