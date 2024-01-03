package com.kncept.oauth2.subhandler;


import com.kncept.oauth2.config.Oauth2StorageConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InitHandler {

    private final Oauth2StorageConfiguration config;

    public InitHandler(Oauth2StorageConfiguration config) {
        this.config= config;
    }

    public void init(boolean await) {
        List<Runnable> tasks = new ArrayList<>();
        tasks.add(config::clientRepository);
        tasks.add(config::authcodeRepository);
        tasks.add(config::oauthSessionRepository);
        tasks.add(config::authRequestRepository);
        tasks.add(config::userRepository);
        tasks.add(config::userLoginRepository);
        tasks.add(config::parameterRepository);
        ExecutorService service = Executors.newFixedThreadPool(tasks.size());
        tasks.forEach(service::execute);
        service.shutdown();
        if (await) try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
    }

}
