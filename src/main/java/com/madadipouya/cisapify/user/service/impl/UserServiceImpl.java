package com.madadipouya.cisapify.user.service.impl;

import com.madadipouya.cisapify.user.exception.UserNotFoundException;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.repository.UserRepository;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getLoggedInUser() {
        return getUserByEmailAddress(getContext().getAuthentication().getName());
    }

    public User getCurrentUser() throws UserNotFoundException {
        return getLoggedInUser().orElseThrow(() -> new UserNotFoundException("Cannot find the user"));
    }

    public Optional<User> getUserByEmailAddress(String emailAddress) {
        return userRepository.getByEmailAddress(emailAddress);
    }

    public User save(User user) {
        return save(user, true);
    }

    public User save(User user, boolean isPasswordUpdated) {
        if(isPasswordUpdated) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
}
