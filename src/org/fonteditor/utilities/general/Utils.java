package org.fonteditor.utilities.general;

public class Utils {
  public static int min(int a, int b, int c) {
    return Math.min(Math.min(a, b), c);
  }

  public static int max(int a, int b, int c) {
    return Math.max(Math.max(a, b), c);
  }

  public static boolean equalsApprox(int a, int b, int tolerance) {
    return ((a - b) > tolerance) ? false : ((b - a) <= tolerance);
  }
}
