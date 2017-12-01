package com.wapitia.common.io.csv;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVColumns {

    public static final Iterable<String> ALIAS_COLUMNS = () -> new AliasColumnIterator();

    public static final Stream<String> aliasColumnStream() {

        final Stream<String> result = StreamSupport
            .stream(ALIAS_COLUMNS.spliterator(), false);
        return result;
    }

    public static final int LETTERS_IN_ALPHABET = 26;

    /**
     * Iterate over alias CSV columns: "A".."Z","AA".."ZZ","AAA"...
     */
    static final class AliasColumnIterator implements Iterator<String> {

        private int letterIndex; // 0..25
        private int repeatCount; // 1..

        public AliasColumnIterator() {
            this.letterIndex = 0;
            this.repeatCount = 1;
        }

        @Override
        public boolean hasNext() {

            return true; // never stops
        }

        @Override
        public String next() {

            String label = currentLabel();
            advance();
            return label;
        }

        private void advance() {

            if (++this.letterIndex >= LETTERS_IN_ALPHABET) {
                this.letterIndex = 0;
                ++this.repeatCount;
            }
            ;

        }

        private String currentLabel() {

            StringBuilder bldr = new StringBuilder();
            char ch = (char) ('A' + letterIndex);
            for (int i = 0; i < repeatCount; i++) {
                bldr.append(ch);
            }
            return bldr.toString();
        }

    }

}
