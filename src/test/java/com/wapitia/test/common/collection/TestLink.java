package com.wapitia.test.common.collection;

import com.wapitia.common.collection.CollectionFunctions;
import com.wapitia.common.collection.ILink;
import com.wapitia.common.collection.LinkBase;

import org.apache.commons.lang3.mutable.MutableLong;
import org.junit.Assert;
import org.junit.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestLink {


	static class StringChain extends LinkBase<String,StringChain> {

		public static StringChain of(String item) {
			return new StringChain(item, Optional.<StringChain> empty());
		}

		public static StringChain of(String item, StringChain nextLink) {
			return new StringChain(item, Optional.<StringChain> of(nextLink));
		}

		protected StringChain(String item, Optional<StringChain> nextOpt) {
			super(item, nextOpt);
		}

	};

	@Test
	public void testOne() {

		Optional<StringChain> myChain = Optional.of(StringChain.of("X", StringChain.of("Y", StringChain.of("Z"))));

		List<String> list = ILink.collect(myChain, Collectors.toList());
		String actString = list.toString();
		System.out.println(actString);
		Assert.assertEquals("[X, Y, Z]", actString);

		Assert.assertEquals(3, ILink.size(myChain));
        Assert.assertEquals(3, (int)myChain.map(ILink::size).orElse(0));


	}

	static class SpliterChain<T> extends LinkBase<Spliterator<T>,SpliterChain<T>> {

		public static <TS> SpliterChain<TS> of(Spliterator<TS> item) {
			return new SpliterChain<TS>(item, Optional.<SpliterChain<TS>> empty());
		}

		public static <TS> SpliterChain<TS> of(Spliterator<TS> item, SpliterChain<TS> nextLink) {
			return new SpliterChain<TS>(item, Optional.<SpliterChain<TS>> of(nextLink));
		}

		protected SpliterChain(Spliterator<T> item, Optional<SpliterChain<T>> nextOpt) {
			super(item, nextOpt);
		}

	};


	static class SizeCollector implements Collector<Spliterator<?>, SizeCollector, Long> {

	    long value;

		public SizeCollector() {
			this.value = 0L;
		}

		public long value() {
		    return value;
		}

        @Override
        public Supplier<SizeCollector> supplier() {

            return SizeCollector::new;
        }

        @Override
        public BiConsumer<SizeCollector, Spliterator<?>> accumulator() {

            return (ss, s) -> ss.value = CollectionFunctions.addEstimateSizes(ss.value, s.estimateSize());
        }

        @Override
        public BinaryOperator<SizeCollector> combiner() {

            return (ss1, ss2) -> { ss1.value = CollectionFunctions.addEstimateSizes(ss1.value, ss2.value); return ss1; };
        }

        @Override
        public Function<SizeCollector, Long> finisher() {

            return SizeCollector::value;
        }

        @Override
        public Set<Characteristics> characteristics() {

            return EnumSet.noneOf(Characteristics.class);
        }


	}

    static class SpliteratorSize {

        private long value = 0L;

        public long value() {
            return value;
        }

        public void setValue(long newVal) {
            this.value = newVal;
        }


        public static Collector<Spliterator<?>, SpliteratorSize, Long> collector() {
            final Collector<Spliterator<?>, SpliteratorSize, Long> collector = Collector.of(
                    SpliteratorSize::new,
                    (ss, s) -> ss.setValue(CollectionFunctions.addEstimateSizes(ss.value(), s.estimateSize())),
                    (ss1, ss2) -> { ss1.setValue(CollectionFunctions.addEstimateSizes(ss1.value(), ss2.value())); return ss1; },
                    SpliteratorSize::value);
            return collector;
        }

    }

    public static Collector<Spliterator<?>, MutableLong, Long> spliterSizeCollector() {
        final Collector<Spliterator<?>, MutableLong, Long> collector =
            Collector.<Spliterator<?>, MutableLong, Long> of(
            MutableLong::new,
                (ss, s) -> ss.setValue(CollectionFunctions.addEstimateSizes(ss.getValue(), s.estimateSize())),
                (ss1, ss2) -> { ss1.setValue(CollectionFunctions.addEstimateSizes(ss1.getValue(), ss2.getValue())); return ss1; },
                MutableLong::getValue);
        return collector;
    }

	@Test
	public void testTwo() {

		Spliterator<String> splitA = Stream.of("A","B","C").spliterator();
		Spliterator<String> splitB = Stream.of("Q","X","Y","Z").spliterator();
		Optional<SpliterChain<String>> myChain = Optional.of(SpliterChain.of(splitA, SpliterChain.of(splitB)));

		Long size = ILink.collect(myChain, SpliteratorSize.collector());
		Assert.assertEquals(size, (Long) 7L);
	}

	@Test
	public void testThree() {

		Spliterator<String> splitA = Stream.of("A","B","C").spliterator();
		Spliterator<String> splitB = Stream.of("Q","X","Y","Z").spliterator();
		Optional<SpliterChain<String>> myChain = Optional.of(SpliterChain.of(splitA, SpliterChain.of(splitB)));

		Long size = ILink.collect(myChain, new SizeCollector());
		Assert.assertEquals(size, (Long) 7L);
	}
}
