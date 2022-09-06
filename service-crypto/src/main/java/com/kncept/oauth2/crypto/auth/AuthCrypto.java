package com.kncept.oauth2.crypto.auth;

public class AuthCrypto {

    public static final String DEFAULT_SALT_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()`~,.<>/?;:'\"|\\[{]}";
    public static final int defaultSaltLength = 16;

//    public static final String defaultMessageDigestAlgorithm ="SHA256"; // MD5 SHA512
    // other options...
    // RIPEMD-160 and Whirlpool ?
    // PBKDF2 / Rfc2898 / Rfc2898DeriveBytes

    public static String defaultPasswordHasherAlgorithm = "b64(sha256)";

    public String salt() {
        return salt(defaultSaltLength, DEFAULT_SALT_CHARSET);
    }

    public String salt(int len, String charset) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++ ) {
            int index = (int)(charset.length() * Math.random());
            if (index >= charset.length()) i--; // error condition
            sb.append(charset.charAt(index));
        }
        return sb.toString();
    }

    public PasswordHasher hasher() {
        return hasher(defaultPasswordHasherAlgorithm);
    }

    public PasswordHasher hasher(String algorithm) {
        if (algorithm == null || algorithm.trim().equals("")) return hasher(defaultPasswordHasherAlgorithm);
        return new PasswordHasher.MessageDigestPasswordHasher(algorithm);
    }

    public PasswordHasher hasherFromMessageDigestAlgorithm(String messageDigestAlgorithm) {
        if (messageDigestAlgorithm == null || messageDigestAlgorithm.trim().equals("")) return hasher(defaultPasswordHasherAlgorithm);
        return new PasswordHasher.MessageDigestPasswordHasher("b64(" + messageDigestAlgorithm + ")");
    }



}
