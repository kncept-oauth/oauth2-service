package com.kncept.oauth2.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtils {

    enum PasswordHashType {

        disabled("x"),
        nopassword("_"),
        plainpassword(" "), // not hashed
        sha256("s"),
        pbkdf2("p"),
        fips_140("f"),
        ;
        private final String prefix;
        PasswordHashType(String prefix) {
            if (prefix == null) throw new NullPointerException();
            if (prefix.contains(":")) throw new IllegalArgumentException();
            this.prefix = prefix;
        }

        private SaltedPasswordHasher newHasher(String trimmedSalt) {
            switch (this) {
                case disabled -> {
                    return new SaltedPasswordHasher() {
                        @Override
                        public String hash(String password) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean matches(String hash, String password) {
                            return false;
                        }
                    };
                }
                case nopassword -> {
                    return new SaltedPasswordHasher() {
                        @Override
                        public String hash(String password) {
                            throw new UnsupportedOperationException();
                        }

                        @Override
                        public boolean matches(String hash, String password) {
                            return true;
                        }
                    };
                }
                case plainpassword -> {
                    return new SaltedPasswordHasher() {
                        @Override
                        public String hash(String password) {
                            return password;
                        }

                        @Override
                        public boolean matches(String hash, String password) {
                            return hash.equals(password);
                        }
                    };
                }
                case sha256 -> {
                    return new PresaltedMessageDigestPasswordHasher(
                            trimmedSalt,
                            "SHA-256",
                            10_000
                    );
                }
                case pbkdf2 -> {
                    return new PresaltedMessageDigestPasswordSecretKey(
                            trimmedSalt,
                            "PBKDF2WithHmacSHA1",
                            1_000
                    );
                }
                case fips_140 ->  {
                    return new PresaltedMessageDigestPasswordSecretKey(
                            trimmedSalt,
                            "PBKDF2WithHmacSHA256",
                            600_000
                    );
                }
            }
            throw new IllegalStateException();
        }

        public static SaltedPasswordHasher lookup(String salt) {
            int seperatorIndex = salt.indexOf(":");
            if (seperatorIndex == -1) throw new IllegalArgumentException("No type indicator in salt");
            String prefix = salt.substring(0, seperatorIndex);
            String trimmedSalt = salt.substring(seperatorIndex + 1);
            for(int i = 0; i < values().length; i++) {
                if (values()[i].prefix.equals(prefix)) return values()[i].newHasher(trimmedSalt);
            }
            throw new IllegalStateException("Unable to match password hasher to salt " + salt);
        }

    }
    public static final String base62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int saltLength = 12;

    public static String generateSalt() {
        return generateSalt(PasswordHashType.fips_140);
    }
    public static String generateSalt(PasswordHashType type) {
        return type.prefix + ":" + randomString(saltLength, base62);
    }
    public static String randomString(int length, String alphabet) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int index = (int)(Math.random() * alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }

    public static String hash(String salt, String password) {
        return PasswordHashType.lookup(salt).hash(password);
    }

    public static boolean matches(String salt, String hash, String password) {
        return PasswordHashType.lookup(salt).matches(hash, password);
    }

    interface SaltedPasswordHasher {
        String hash(String password);

        boolean matches(String hash, String password);
    }

    private static class PresaltedMessageDigestPasswordHasher implements SaltedPasswordHasher {
        private final String trimmedSalt;
        private final String hashAlgorighm;
        private final int iterations;
        public PresaltedMessageDigestPasswordHasher(
                String trimmedSalt,
                String hashAlgorighm,
                int iterations
        ) {
            this.trimmedSalt = trimmedSalt;
            this.hashAlgorighm = hashAlgorighm;
            this.iterations = iterations;
        }
        @Override
        public String hash(String password) {
            try {
                MessageDigest digest = MessageDigest.getInstance(hashAlgorighm);
                for(int i = 0; i < iterations; i++) {
                    digest.update(trimmedSalt.getBytes(StandardCharsets.UTF_8));
                    digest.update(password.getBytes(StandardCharsets.UTF_8));
                }
                byte[] bytes = digest.digest();
                return Base64.getEncoder().encodeToString(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean matches(String hash, String password) {
            return hash(password).equals(hash);
        }
    }

    private static class PresaltedMessageDigestPasswordSecretKey implements SaltedPasswordHasher {
        public PresaltedMessageDigestPasswordSecretKey(
                String trimmedSalt,
                String keyAlgorighm,
                int iterations
        ) {
            this.trimmedSalt = trimmedSalt;
            this.keyAlgorighm = keyAlgorighm;
            this.iterations = iterations;
        }
        private final String trimmedSalt;
        private final String keyAlgorighm;
        private final int iterations;

        @Override
        public String hash(String password) {
            try {
                PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), trimmedSalt.getBytes(StandardCharsets.UTF_8), iterations, 64 * 8);
                SecretKeyFactory skf = SecretKeyFactory.getInstance(keyAlgorighm);
                byte[] hash = skf.generateSecret(spec).getEncoded();
                return Base64.getEncoder().encodeToString(hash);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean matches(String hash, String password) {
            return hash(password).matches(hash);
        }
    }
}
