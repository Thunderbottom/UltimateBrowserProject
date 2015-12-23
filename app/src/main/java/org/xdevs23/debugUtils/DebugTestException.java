package org.xdevs23.debugUtils;


public class DebugTestException extends Exception {

    public DebugTestException() { super(); }

    public static void printActualStack() {
        try { throw new DebugTestException(); }
        catch(DebugTestException e) { StackTraceParser.logStackTrace(e); }
    }

}
