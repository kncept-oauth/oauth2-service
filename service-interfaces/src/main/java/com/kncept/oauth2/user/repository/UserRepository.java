package com.kncept.oauth2.user.repository;

import com.kncept.oauth2.user.User;

public interface UserRepository {

    public boolean isAcceptingSignup();

    public User lookupUser(String username, String password);

    public boolean createUser(String username, String password);}
