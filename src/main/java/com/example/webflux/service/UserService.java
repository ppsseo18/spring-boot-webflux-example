package com.example.webflux.service;

import com.example.webflux.model.User;
import com.example.webflux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public User read(String requestUserId) {
         return userRepository.findById(requestUserId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<User> readAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User create(User requestUser) {
        return userRepository.findById(requestUser.getUserId()).isPresent() ? null : userRepository.save(requestUser);
    }

    @Transactional
    public User update(User requestUser) {
        return userRepository.findById(requestUser.getUserId()).isPresent() ? userRepository.save(requestUser) : null;
    }

    @Transactional
    public String delete(String requestUserId) {
        if(userRepository.findById(requestUserId).isPresent()) {
            userRepository.deleteById(requestUserId);
            return requestUserId;
        } else {
            return null; 
        }
    }
}
