package com.kncept.oauth2.config;

import java.lang.reflect.InvocationTargetException;

public class EnvPropertyConfiguration {

    /**
     * Use a value like <pre>com.kncept.oauth2.config.InMemoryConfiguration</pre> for testing<br>
     */
    public static final String OIDC_STORAGE_CONFIGURATION_PROPERTY = "OIDC_Storage_Config";

    public static final String OIDC_HOSTNAME = "OIDC_Hostname";


    // TODO
    public static final String OIDC_KEY_CONFIGURATION_PROPERTY = "OIDC_Key_Config";

    // also 'renderer' overrides

    public static String env(String name) {
        String value = System.getenv(name);
        if (value == null) return null;
        value = value.trim();
        if (value.equals("")) return null;
        return value;
    }

    public static String hostname() {
        String hostname =  env(OIDC_HOSTNAME);
        return hostname == null ? "" : hostname;
    }

    public static Oauth2StorageConfiguration loadStorageConfigFromEnvProperty() {
        String configClassName = env(OIDC_STORAGE_CONFIGURATION_PROPERTY);
        if (configClassName == null)
            throw new RuntimeException("Property is not defined or has no value: " + OIDC_STORAGE_CONFIGURATION_PROPERTY);
        return loadFromClassname(configClassName);
    }

    private static <T> T loadFromClassname(String classname) {
        if (classname == null) throw new NullPointerException();
        try {
            Class<?> configClass = Class.forName(classname);
            return (T) configClass.getDeclaredConstructor().newInstance();
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

