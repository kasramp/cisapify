package com.madadipouya.cisapify.user.service;

import com.madadipouya.cisapify.user.exception.UserNotFoundException;
import com.madadipouya.cisapify.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Retrieves a User by id
     *
     * @param id of a User
     * @return {@link Optional<User>} if finds a user, {@link Optional#EMPTY} otherwise
     */
    Optional<User> getUserById(long id);

    /**
     * Retrieves Logged in User
     *
     * @return {@link Optional<User>} if the user is logged in, {@link Optional#EMPTY} otherwise
     */
    Optional<User> getLoggedInUser();

    /**
     * Retrieves the current logged in User
     *
     * @return {@link User} if the user is logged in, throws exception otherwise
     * @throws UserNotFoundException if no user is logged in
     */
    User getCurrentUser() throws UserNotFoundException;

    /**
     * Retrieves a User by email address
     *
     * @param emailAddress of a User
     * @return {@link Optional<User>} if finds a user, {@link Optional#EMPTY} otherwise
     */
    Optional<User> getUserByEmailAddress(String emailAddress);

    /**
     * Persists a User to the database
     *
     * @param user to persist
     * @return {@link User} with non-null Id if the entity didn't exist, or updated {@link User} otherwise
     */
    User save(User user);

    /**
     * Persists a User to the database
     *
     * @param user              to persist
     * @param isPasswordUpdated flag to whether encrypt password or not
     * @return {@link User} with non-null Id if the entity didn't exist, or updated {@link User} otherwise
     */
    User save(User user, boolean isPasswordUpdated);


    List<User> getAll();
}
