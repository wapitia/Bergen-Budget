
package com.wapitia.test.common.collection;

import com.wapitia.common.collection.Blenderator;
import com.wapitia.common.collection.CollectionFunctions;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TestFeederSpliterator {

    @Test
    public void test1() {
        final Stream<String> str1 = Stream.of("A","B","L");
        final Stream<String> str2 = Stream.of("G","S","Y");
        final Stream<String> str3 = Stream.of("ER","GR","PR","TR");
        final Stream<String> str4 = Stream.of();

//        final List<String> t = Stream.of("ER","GR","PR","TR").collect(Collectors.toList());

        Blenderator<String> spliter = new Blenderator<String>(
           String::compareTo, str1.spliterator(),
           str2.spliterator(), str3.spliterator(), str4.spliterator());

        long estSize = spliter.estimateSize();
        Assert.assertEquals(10, estSize);
        final Stream<String> resultStream = StreamSupport.stream(spliter, false);
        final String actResult = resultStream.collect(Collectors.joining(";"));
        System.out.println(actResult);
        Assert.assertEquals("A;B;ER;G;GR;L;PR;S;TR;Y", actResult);
    }

    @Test
    public void test2() {
        Blenderator<String> spliter
         = new Blenderator.Builder<>(String::compareTo)
                 .add(Stream.of("A","B","L"))
                 .add(CollectionFunctions.listOf("G","S","Y"))
                 .add(Stream.of("ER","GR","PR","TR").iterator())
                 .add(Stream.<String> of().spliterator())
                 .toBlenderator();


        final Stream<String> resultStream = StreamSupport.stream(spliter, false);
        final String actResult = resultStream.collect(Collectors.joining(";"));
        System.out.println(actResult);
        Assert.assertEquals("A;B;ER;G;GR;L;PR;S;TR;Y", actResult);
    }
}
