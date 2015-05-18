package helloworld;

import helloworld.ImGroovy;

public class ImJava {

    public ImJava() {
        System.out.println("I'm a Java class. " + true);
        new ImGroovy().bar();
        new ImGroovy().foo();
    }
}