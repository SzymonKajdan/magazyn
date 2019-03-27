package com.example.service;

import com.example.security.model.User;

public interface UserService {

    void saveWorker(User user);
    void saveManager(User user);
    void delete(User user);
    String generatePassword();
    String ecryptPassword(String str);
}
