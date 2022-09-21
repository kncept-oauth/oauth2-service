package com.kncept.oauth2.config.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> login(String username, String password);

    Optional<User> create(String username, String password);}
