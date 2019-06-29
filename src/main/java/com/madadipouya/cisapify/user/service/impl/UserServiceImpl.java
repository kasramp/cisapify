package com.madadipouya.cisapify.user.service.impl;

import com.madadipouya.cisapify.i18n.service.I18nService;
import com.madadipouya.cisapify.user.exception.UserNotFoundException;
import com.madadipouya.cisapify.user.model.User;
import com.madadipouya.cisapify.user.repository.UserRepository;
import com.madadipouya.cisapify.user.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final I18nService i18nService;


    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, I18nService i18nService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.i18nService = i18nService;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getLoggedInUser() {
        return getUserByEmailAddress(getContext().getAuthentication().getName());
    }

    @Override
    public User getCurrentUser() throws UserNotFoundException {
        return getLoggedInUser().orElseThrow(() -> new UserNotFoundException(i18nService.getMessage("user.service.userNotFound")));
    }

    @Override
    public Optional<User> getUserByEmailAddress(String emailAddress) {
        return userRepository.getByEmailAddress(emailAddress);
    }

    @Override
    public User save(User user) {
        return save(user, true);
    }

    @Override
    public User save(User user, boolean isPasswordUpdated) {
        if (isPasswordUpdated) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
