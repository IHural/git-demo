package com.epam.rd.autotasks;

import java.util.Iterator;


public class CarouselRun {
    private final Iterator<Integer> iterator;
    private int current;

    public CarouselRun(Iterator<Integer> iterator) {
        this.iterator = iterator;
        this.current = -1;
    }

    public int next() {
        while (this.iterator.hasNext()) {
            int next = this.iterator.next();
            if (next > 0) {
                this.current = next;
                this.current--;
                return this.current;
            }
        }
        return (this.current = -1);
    }

    public boolean isFinished() {
        return !this.iterator.hasNext();
    }
}







//    public int next() {
//       throw new UnsupportedOperationException();
//    }
//
//    public boolean isFinished() {
//        throw new UnsupportedOperationException();
//    }


