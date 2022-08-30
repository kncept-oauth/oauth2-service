package com.kncept.oauth2.user.repository;

public interface UserRepository {

    public boolean isAcceptingSignup();

    public boolean verifyUser(String username, String password);

    public boolean createUser(String username, String password);}
