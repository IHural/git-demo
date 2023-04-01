package com.epam.rd.autotasks;

import java.util.ArrayList;
import java.util.List;



public class DecrementingCarousel {
    private final List<Integer> elements;
    private final int capacity;
    private boolean isRunning;

    public DecrementingCarousel(int capacity) {
        this.elements = new ArrayList<>();
        this.capacity = capacity;
        this.isRunning = false;
    }

    public boolean addElement(int element) {
        if (this.isRunning || element <= 0 || this.elements.size() >= this.capacity) {
            return false;
        }
        this.elements.add(element);
        return true;
    }

    public CarouselRun run() {
        if (this.isRunning) {
            return null;
        }
        this.isRunning = true;
        return new CarouselRun(this.elements.iterator());
    }

    public static void main(String[] args) {
        DecrementingCarousel carousel = new DecrementingCarousel(3);

        System.out.println(carousel.addElement(-2)); //false
        System.out.println(carousel.addElement(0)); //false

        System.out.println(carousel.addElement(2)); //true
        System.out.println(carousel.addElement(3)); //true
        System.out.println(carousel.addElement(1)); //true

//carousel is full
        System.out.println(carousel.addElement(2)); //false

        CarouselRun run = carousel.run();

        System.out.println(run.next()); //2
        System.out.println(run.next()); //3
        System.out.println(run.next()); //1

        System.out.println(run.next()); //1
        System.out.println(run.next()); //2

        System.out.println(run.next()); //1

        System.out.println(run.isFinished()); //true
        System.out.println(run.next()); //-1

    }
}


//    public boolean addElement(int element){
//        throw new UnsupportedOperationException();
//    }
//
//    public CarouselRun run(){
//       throw new UnsupportedOperationException();
//    }
//}
