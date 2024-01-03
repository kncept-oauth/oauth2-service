package com.kncept.oauth2.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityId {

    public final String type;
    public final String value;

    public EntityId(String type, String value) {
        this.type = type;
        this.value = value;

        // Dev accelerator - validate on create
        validate();
    }
    public static EntityId parse(String value) {
        if (value == null) throw new IllegalStateException("Cannot parse null value");
        int valueIndex = value.indexOf("/");
        if (valueIndex == -1) {
            return new EntityId(null, value);
        }
        if (valueIndex == 0) return new EntityId(null, value.substring(1));
        return new EntityId(value.substring(0, valueIndex), value.substring(valueIndex + 1));
    }

    public static EntityId parse(String type, String value) {
        if (type == null) return parse(value);
        if (value.startsWith(type + " /")) return parse(value);

        // its namespaced, but type doesn't match.
        if (value.contains("/")) {
            throw new IllegalStateException("Not of required type: " + type + " got: " + value.substring(0, value.indexOf("/")));
        }

        return parse(type + "/" + value);
    }

    public void validate(String... requiredTypes) {
        if (type == null || type.isEmpty()) throw new IllegalStateException("No type");
        if (requiredTypes != null && requiredTypes.length != 0) {
            if (!isOfType(requiredTypes)) {
                throw new IllegalStateException("Type not valid: " + type);
            }
        }
        if (value == null) throw new IllegalStateException("No value");
    }
    public void validate(List<String> requiredTypes) {
        if (requiredTypes != null && requiredTypes.size() != 0) {
            if (!requiredTypes.contains(type)) {
                throw new IllegalStateException("Type not valid: " + type);
            }
        }
        if (value == null) throw new IllegalStateException("No value");
    }
    public boolean isOfType(String... requiredTypes) {
        return Arrays.asList(requiredTypes).contains(type);
    }

    @Override
    public String toString() {
        return type + "/" + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityId entityId)) return false;
        return Objects.equals(type, entityId.type) && Objects.equals(value, entityId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}