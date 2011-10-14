package org.fonteditor.utilities.general;

public class For {
  public static void get(Object o) {
    if (o == null) {
      o = null;
    }
  }

  public static void get(int i) {
    i++;
  }

  public static void get(boolean i) {
    i = !i;
  }
}
