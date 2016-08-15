package com.memoizrlabs.retrooptional;

final class Some<T> extends Optional<T> {

    private final T value;

    Some(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public <S> Optional<S> map(Function1<? super T, S> function) {
        return of(function.apply(value));
    }

    @Override
    public Optional<T> filter(Predicate<? super T> predicate) {
        if (predicate.verify(value)) {
            return this;
        } else {
            return empty();
        }
    }

    @Override
    public <S> Optional<S> flatMap(Function1<? super T, Optional<S>> function) {
        return function.apply(value);
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public void doIfPresent(Action1<T> action1) {
        action1.accept(value);
    }

    @Override
    public T orElse(T alternative) {
        return value;
    }

    @Override
    public T orElseGet(Function0<T> function) {
        return value;
    }

    @Override
    public <X extends Throwable> T orElseThrow(Function0<X> function) throws X {
        return value;
    }

    @Override
    public String toString() {
        return "Some {" +
            "value=" + value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Some<?> some = (Some<?>) o;

        return value != null ? value.equals(some.value) : some.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
