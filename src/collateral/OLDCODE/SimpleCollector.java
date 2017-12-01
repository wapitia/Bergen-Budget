package com.wapitia.common.collection;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * A collector pattern which accumulates into a single value of type R.
 * It is assumed that the collector's supplier supplies this
 * here SimpleCollector, and that the instance is constructed with
 * the proper initial value of R.
 *
 * @param <T>
 * @param <SC>
 * @param <R>
 */
public abstract class SimpleCollector<T, SC extends SimpleCollector<T, SC, R>, R>
    implements Collector<T, SC, R>
{

    private R value;


    protected abstract void accumulate(T t);

    protected abstract void combine(SC r);

    public R value() {

        return value;
    }

    public void setValue(R value) {

        this.value = value;
    }

    protected SimpleCollector(R initValue) {
        this.value = initValue;
    }

    @Override
    public Supplier<SC> supplier() {

        @SuppressWarnings("unchecked")
        final Supplier<SC> f = () -> (SC) this;
        return f;
    }

    @Override
    public BiConsumer<SC, T> accumulator() {

        return (ss, s) -> ss.accumulate(s);
    }

    @Override
    public BinaryOperator<SC> combiner() {

        return (ss1, ss2) -> {
            ss1.combine(ss2);
            return ss1;
        };
    }

    @Override
    public Function<SC, R> finisher() {

        return SC::value;
    }

    @Override
    public Set<Characteristics> characteristics() {

        return EnumSet.of(Characteristics.CONCURRENT,
            Characteristics.UNORDERED);
    }

}
