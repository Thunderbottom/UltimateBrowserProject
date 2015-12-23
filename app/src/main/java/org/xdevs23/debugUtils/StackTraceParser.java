package org.xdevs23.debugUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceParser {
	
	public static String parse(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter  pw = new  PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}

    public static void logStackTrace(Throwable throwable) { Logging.logt(parse(throwable)); }

}
