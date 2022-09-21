package com.kncept.oauth2.config.crypto;

public class SimpleExpiringKeypair implements ExpiringKeypair {

    String id;
    String privateKey;
    String publicKey;
    long validFrom;
    long validTo;
    long deletionTime;

    public SimpleExpiringKeypair(
            String id,
            String privateKey,
            String publicKey,
            long validFrom,
            long validTo,
            long deletionTime
    ) {
        this.id = id;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.deletionTime = deletionTime;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String privateKey() {
        return privateKey;
    }

    @Override
    public String publicKey() {
        return publicKey;
    }

    @Override
    public long validFrom() {
        return validFrom;
    }

    @Override
    public long validTo() {
        return validTo;
    }

    @Override
    public long deletionTime() {
        return deletionTime;
    }
}
