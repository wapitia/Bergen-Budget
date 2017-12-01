
package com.wapitia.dates;

import java.time.LocalDate;

public interface Sandbox1 {

    static abstract class Foo<T> {

    };

    static Foo<? extends Object> ALL = new Foo<Object>() {

    };

    class LDFoo extends Foo<LocalDate> {

    }


    static void f() {
        Foo<LocalDate> ldfA = new LDFoo();
        ldfA = (Foo<LocalDate>) ALL;

//        LDFoo ldf = new LDFoo();
//        ldf = (LDFoo) ALL;
    }

    public static void main(String[] args) {
        f();
    }
}
