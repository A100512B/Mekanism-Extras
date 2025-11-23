package com.jerry.mekanism_extras.common.util;

import java.util.Objects;

/**
 * A predicate that takes six arguments and returns a boolean.
 */
@FunctionalInterface
public interface Predicate6<T1, T2, T3, T4, T5, T6> {

    boolean test(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

    default Predicate6<T1, T2, T3, T4, T5, T6> and(Predicate6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> other) {
        Objects.requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6) -> test(t1, t2, t3, t4, t5, t6) && other.test(t1, t2, t3, t4, t5, t6);
    }

    default Predicate6<T1, T2, T3, T4, T5, T6> negate() {
        return (t1, t2, t3, t4, t5, t6) -> !test(t1, t2, t3, t4, t5, t6);
    }

    default Predicate6<T1, T2, T3, T4, T5, T6> or(Predicate6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> other) {
        Objects.requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6) -> test(t1, t2, t3, t4, t5, t6) || other.test(t1, t2, t3, t4, t5, t6);
    }
}
