package com.aaron.basemvvmlibrary2.binding.command;

/**
 * A one-argument action.
 */
public interface BindingConsumer<T> {
    void call(T t);
}
