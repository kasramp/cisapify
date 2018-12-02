package com.madadipouya.cisapify.user.service;

import com.madadipouya.cisapify.user.model.User;

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
}
