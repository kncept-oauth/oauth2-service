package com.kncept.oauth2.user.repository;

import com.kncept.oauth2.user.User;

import java.util.Optional;

public interface UserRepository {

    public boolean isAcceptingSignup();

    public Optional<User> attemptUserLogin(String username, String password);

//    public Optional<User> lookupUserBySub(String sub);

    public Optional<User> createUser(String username, String password);}
