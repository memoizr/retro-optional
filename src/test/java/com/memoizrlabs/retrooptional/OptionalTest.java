package com.memoizrlabs.retrooptional;

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import nl.jqno.equalsverifier.EqualsVerifier;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OptionalTest {

    private static final String CONTENT_TO_STRING = "Content description";

    @Test
    public void of_withNullValue_returnsEmpty() throws Exception {
        assertFalse(Optional.of(null).isPresent());
    }

    @Test
    public void of_withValue_returnsWrappedValue() throws Exception {
        assertTrue(Optional.of(new A())
            .isPresent());
    }

    @Test
    public void get_withValue_returnsValue() throws Exception {
        final A value = new A();

        assertEquals(value, Optional.of(value)
            .get());
    }

    @Test(expected = NoSuchElementException.class)
    public void get_withoutValue_throwsException() throws Exception {
        Optional.empty().get();
    }

    @Test
    public void map_withValue_continuesTheChain() throws Exception {
        final A value = new A();
        final B other = new B();

        Function1<A, B> function = new Function1<A, B>() {
            @Override
            public B apply(A a) {
                return other;
            }
        };

        Optional<B> optionalB = Optional.of(value).map(function);

        assertEquals(other, optionalB.get());
    }

    @Test
    public void map_withoutValue_doesNotContinueTheChain() throws Exception {
        Optional.empty().map(new Function1<Object, Object>() {
            @Override
            public Object apply(Object a) {
                fail();
                return null;
            }
        });
    }

    @Test
    public void flatMap_withValue_continuesTheChain() throws Exception {
        final A value = new A();
        final B other = new B();

        Function1<A, Optional<B>> monadicFunction = new Function1<A, Optional<B>>() {
            @Override
            public Optional<B> apply(A a) {
                return Optional.of(other);
            }
        };

        Optional<B> optionalB = Optional.of(value).flatMap(monadicFunction);

        assertEquals(other, optionalB.get());
    }

    @Test
    public void flatMap_withNoValue_doesNotContinue() throws Exception {
        Optional.empty().flatMap(new Function1<Object, Optional<Object>>() {
            @Override
            public Optional<Object> apply(Object a) {
                fail();
                return null;
            }
        });
    }

    @Test
    public void filterFilters_withValue_ifMatches_returnsWrappedValue() throws Exception {
        A value = new A();

        Optional<A> filteredOption = Optional.of(value).filter(new Predicate<A>() {
            @Override
            public boolean verify(A a) {
                return true;
            }
        });

        assertEquals(value, filteredOption.get());
    }


    @Test
    public void filterFilters_withValue_ifNoMatch_returnsEmpty() throws Exception {
        Optional<A> filteredOption = Optional.of(new A()).filter(new Predicate<A>() {
            @Override
            public boolean verify(A a) {
                return false;
            }
        });

        assertFalse(filteredOption.isPresent());
    }

    @Test
    public void filterFilters_withoutValue_returnsEmpty() throws Exception {

        Optional<A> filteredOption = Optional.<A>empty().filter(new Predicate<A>() {
            @Override
            public boolean verify(A a) {
                return false;
            }
        });

        assertFalse(filteredOption.isPresent());
    }

    @Test
    public void isPresent_withValue_returnsTrue() throws Exception {
        assertTrue(Optional.of(new A())
            .isPresent());
    }

    @Test
    public void isPresent_withoutValue_returnsFalse() throws Exception {
        assertFalse(Optional.empty()
            .isPresent());
    }

    @Test
    public void doIfPresent_withValue_executesFunction() throws Exception {
        A value = new A();

        final AtomicInteger integer = new AtomicInteger();

        Optional.of(value)
            .doIfPresent(new Action1<A>() {
                @Override
                public void accept(A x) {
                    integer.incrementAndGet();
                }
            });

        assertEquals(1, integer.get());
    }

    @Test
    public void doIfPresentDoesnt_withoutValue_doesNotExecuteFunction() throws Exception {
        Optional.empty()
            .doIfPresent(new Action1<Object>() {
                @Override
                public void accept(Object x) {
                    fail();
                }
            });
    }

    @Test
    public void orElse_withValue_returnsValue() throws Exception {
        A value = new A();
        A alternative = new A();

        assertEquals(value, Optional.of(value)
            .orElse(alternative));
    }

    @Test
    public void orElse_withoutValue_returnsAlternative() throws Exception {
        A alternative = new A();

        assertEquals(alternative, Optional.empty()
            .orElse(alternative));
    }

    @Test
    public void orElseGet_withValue_returnsValue() throws Exception {
        final A value = new A();
        final A alternative = new A();

        assertEquals(value, Optional.of(value)
            .orElseGet(new Function0<A>() {
                @Override
                public A apply() {
                    return alternative;
                }
            }));
    }

    @Test
    public void orElseGet_withoutValue_returnsAlternative() throws Exception {
        final A alternative = new A();

        assertEquals(alternative, Optional.empty()
            .orElseGet(new Function0<Object>() {
                @Override
                public Object apply() {
                    return alternative;
                }
            }));
    }

    @Test
    public void orElseThrow_withValue_returnsValue() throws Exception, AnException {
        A value = new A();

        assertEquals(value, Optional.of(value)
            .orElseThrow(new Function0<AnException>() {
                @Override
                public AnException apply() {
                    return new AnException();
                }
            }));
    }

    @Test(expected = AnException.class)
    public void orElseThrow_withoutValue_throwsException() throws Exception, AnException {
        Optional.empty()
            .orElseThrow(new Function0<AnException>() {
                @Override
                public AnException apply() {
                    return new AnException();
                }
            });
    }

    @Test
    public void toString_withValue_printsTheValue() throws Exception {
        String s = Optional.of(new A())
            .toString();

        assertTrue(s.contains(CONTENT_TO_STRING));
    }

    @Test
    public void toString_withoutValue_printsEmptyDescription() throws Exception {
        String s = Optional.empty()
            .toString();

        assertTrue(s.toLowerCase()
            .contains("empty"));
    }

    @Test
    public void equals_withValue_returnsTrueIfValueIsEqual() throws Exception {
        A value = new A();
        assertTrue(Optional.of(value)
            .equals(Optional.of(value)));
    }

    @Test
    public void hashcode_withValue_returnsValuesHashcode() throws Exception {
        A value = new A();
        assertEquals(value.hashCode(), Optional.of(value).hashCode());
    }

    @Test
    public void equalsAndHashcode_withValue_fulfilsStandardContract() throws Exception {
        EqualsVerifier.forClass(Optional.of(new A()).getClass()).verify();
    }

    @Test
    public void equals_withAnotherEmpty_isAlwaysTrue() throws Exception {
        assertEquals(Optional.of(null), Optional.empty());
    }

    private static final class A {
        @Override
        public String toString() {
            return CONTENT_TO_STRING;
        }
    }

    private static final class B {
    }

    private static final class AnException extends Throwable {
    }
}

