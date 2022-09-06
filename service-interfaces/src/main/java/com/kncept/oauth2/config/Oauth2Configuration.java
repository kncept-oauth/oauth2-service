package com.kncept.oauth2.config;

import com.kncept.oauth2.config.authcode.AuthcodeRepository;
import com.kncept.oauth2.config.authrequest.AuthRequestRepository;
import com.kncept.oauth2.config.client.ClientRepository;
import com.kncept.oauth2.config.session.OauthSessionRepository;
import com.kncept.oauth2.config.user.UserRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public interface Oauth2Configuration {
        boolean requirePkce();
        ClientRepository clientRepository();
        AuthRequestRepository authRequestRepository();
        AuthcodeRepository authcodeRepository();
        UserRepository userRepository();
        OauthSessionRepository oauthSessionRepository();

        static Oauth2Configuration loadConfigurationFromEnvProperty(String property, Supplier<? extends Oauth2Configuration> defaultValue) {
                try {
                        String configClassName = System.getenv(property);
                        if (configClassName == null) return defaultValue.get();
                        Class configClass = Class.forName(configClassName);
                        return (Oauth2Configuration)configClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                }
        }
}
