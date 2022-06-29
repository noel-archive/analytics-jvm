package org.noelware.analytics.server;

@FunctionalInterface
public interface GenericModifyOperation<T> {
    void modify(T data);
}
