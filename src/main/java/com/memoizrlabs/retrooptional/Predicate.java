package com.memoizrlabs.retrooptional;

public interface Predicate<T> {

    boolean verify(T subject);
}
