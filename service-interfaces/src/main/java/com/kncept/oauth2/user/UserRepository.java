package com.kncept.oauth2.user;

import java.util.Optional;

public interface UserRepository {

    public boolean isAcceptingSignup();

    public Optional<User> attemptUserLogin(String username, String password);

    public Optional<User> createUser(String username, String password);}
