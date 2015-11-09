package com.memoizrlabs.retrooptional;

import java.util.NoSuchElementException;

final class Empty<T> extends Optional<T> {

    Empty() {
    }

    @Override
    public T get() {
        throw new NoSuchElementException();
    }

    @Override
    public <S> Optional<S> map(Function1<? super T, S> function) {
        return empty();
    }

    @Override
    public <S> Optional<S> flatMap(Function1<? super T, Optional<S>> function) {
        return empty();
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Optional<T> filter(Predicate<? super T> predicate) {
        return empty();
    }

    @Override
    public void doIfPresent(Action1<T> action1) {
    }

    @Override
    public T orElse(T alternative) {
        return alternative;
    }

    @Override
    public T orElseGet(Function0<T> function) {
        return function.apply();
    }

    @Override
    public <X extends Throwable> T orElseThrow(Function0<X> function) throws X {
        throw function.apply();
    }

    @Override
    public String toString() {
        return "Empty option";
    }
}
