package com.example.pixel.server.chat.service;

import com.example.pixel.server.chat.entity.Customer;
import com.example.pixel.server.chat.repository.UserRepository;
import com.example.pixel.server.util.controller.advice.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@AllArgsConstructor
@Component
public class UserService {

    private UserRepository repository;

    public Collection<Customer> getAllUsers() {
        return repository.findAll();
    }

    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    public Customer getOneUser(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

}