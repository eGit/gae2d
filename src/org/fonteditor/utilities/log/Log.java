package org.fonteditor.utilities.log;

import java.io.PrintStream;

/**
 * Log - Tim Tyler 2001.
 * 
 * A simple logging class.<P>
 *
 * This code has been placed in the public domain.<P>
 * You can do what you like with it.<P>
 * Note that this code comes with no warranty.<P>
 *
 */

public class Log {
  private static final boolean DEBUG = true;
  private static final boolean ERROR = DEBUG & true;
  private static final boolean WARN = DEBUG & true;
  private static final boolean INFO = DEBUG & true;
  private static final boolean LOG = DEBUG & true;
  private static PrintStream print_stream = System.out;

  private static PrintStream getPrintStream() {
    return print_stream;
  }

  /**
   * Log an error
   */
  private static void error(String message) {
    if (ERROR) {
      print(print_stream, "Error: " + message);
    }
  }

  /**
   * Log a warning
   */
  private static void warn(String message) {
    if (WARN) {
      print(print_stream, "Warning: " + message);
    }
  }

  /**
   * Log an informational message
   */
  private static void info(String message) {
    if (INFO) {
      print(print_stream, "Information: " + message);
    }
  }

  /**
   * Log a plain message
   */
  public static void log(String message) {
    if (LOG) {
      print(print_stream, message);
    }
  }

  /**
   * Put out a partial line
   */
  private static void put(String message) {
    if (LOG) {
      put(print_stream, message);
    }
  }

  private static void print(PrintStream out, String message) {
    out.println(message);
  }

  private static void put(PrintStream out, String message) {
    out.print(message);
  }
}
