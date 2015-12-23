package org.xdevs23.threads;


public class Sleeper {

    public static void sleep(int millis) {
        try { Thread.sleep(millis); }
        catch(InterruptedException e) { /* */ }
    }

}
