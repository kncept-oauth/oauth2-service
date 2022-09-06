package com.kncept.oauth2.config.user;

public interface SaltedUser extends User {
	
	public String salt();
	public String passwordHash();
	public String hashAlgorithm();
}
