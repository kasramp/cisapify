package com.madadipouya.cisapify.user.service.impl;

import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.repository.UserRepository;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmailAddress(String emailAddress) {
        return userRepository.getByEmailAddress(emailAddress);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
