package com.kncept.oauth2.crypto.key.strategy;

import com.kncept.oauth2.config.SimpleCrudRepository;
import com.kncept.oauth2.config.crypto.ExpiringKeypair;
import com.kncept.oauth2.crypto.key.KeyGenerator;
import com.kncept.oauth2.crypto.key.ManagedKeypair;
import com.kncept.oauth2.crypto.key.ManagedKeypairConverter;
import com.kncept.oauth2.date.DateRange;

import java.security.KeyPair;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StaticKeypairStrategy implements KeypairStrategy {

    private final SimpleCrudRepository<ExpiringKeypair> repository;

    private volatile ManagedKeypair cached;

    public StaticKeypairStrategy(SimpleCrudRepository<ExpiringKeypair> repository) {
        this.repository = repository;
    }

    @Override
    public synchronized ManagedKeypair current() {
        if (cached != null && cached.isValid()) return cached;

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        List<ExpiringKeypair> stored = repository.list(ExpiringKeypair.EntityType);
        stored.sort(Comparator.comparing(ExpiringKeypair::getExpiry));

        // trim all expired keys when searching
        while (!stored.isEmpty() && stored.get(stored.size() - 1).getExpiry().isBefore(now)) {
            ExpiringKeypair expired = stored.get(stored.size() - 1);
            repository.delete(expired);
            stored.remove(stored.size() - 1);
        }

        // order by valid from
        stored.sort(Comparator.comparing(ExpiringKeypair::getWhen));
        // trim anything not yet valid
        stored = stored.stream().filter(v -> v.getWhen().isAfter(now)).collect(Collectors.toList());


        // this one will be valid.
        if (!stored.isEmpty()) {
            cached = ManagedKeypairConverter.convert(stored.get(0));
            return cached;
        }

        KeyPair kp = new KeyGenerator().generateKeypair();
        cached = new ManagedKeypair(
                ExpiringKeypair.id("01-" + now.format(DateTimeFormatter.ofPattern("MM-yyyy"))),
                kp,
                DateRange.infinite,
                kp.getPrivate().getAlgorithm()
        );
        repository.create(ManagedKeypairConverter.convert(cached));
        return cached;
    }
}
