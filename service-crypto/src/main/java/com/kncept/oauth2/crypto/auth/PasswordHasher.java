package com.kncept.oauth2.crypto.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/*
    TODO: Turn this into a mini parable hash specifier with nestable chains
    ideas:
        h(sha256)  ->   Plain sha 256
        r(500,h(sha256)  ->  500 loops of a sha256 (output -> input passwword)
        h(sha256, 500)  ->  Sha256 looped 500 times
        r(25,h(sha256,25)  -> 25 loops of sha25 looped 25 times
    approaches:
        functional --> b64(sha256(arg,arg2))
        chain --> sha256|b64
        hybrid r(25,h(sha256,25)|b64
*/
public interface PasswordHasher {
    String algorithm();
    String hash(String password, String salt);


    public static enum EncodingType {
        hex, b64
    }
    public static class MessageDigestPasswordHasher implements PasswordHasher {
        private final EncodingType encodingType;
        private final String messageDigestAlgorithm;

        public MessageDigestPasswordHasher(String messageDigestAlgorithm) {
            encodingType = EncodingType.valueOf(messageDigestAlgorithm.substring(0, 3));
            this.messageDigestAlgorithm = messageDigestAlgorithm.substring(4, messageDigestAlgorithm.length() - 1);
        }

        @Override
        public String algorithm() {
            return encodingType.name() + "(" + messageDigestAlgorithm + ")";
        }

        // we use post-salt (not pre-salt) by default
        @Override
        public String hash(String password, String salt) {
            try {
                MessageDigest md = MessageDigest.getInstance(messageDigestAlgorithm);
                md.update(password.getBytes());
                md.update(salt.getBytes());

                byte[] hash = md.digest();
                return toString(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private String toString(byte[] hash) {
            switch (encodingType) {
                case hex : return hexCode(hash);
                case b64: return Base64.getEncoder().encodeToString(hash);
            }
            throw new RuntimeException("Unable to encode as " + encodingType.name());
        }

        private String hexCode(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length; i++)
                sb.append(hexCode(bytes[i]));
            return sb.toString().toLowerCase();
        }
        private String hexCode(byte b) {
            return String.format("%02X", b);
        }
    }



}