package org.fonteditor.utilities.claim;

/**
  * Simple assertion agreement...
  */

public class Claim {
  public static void claim(boolean x, String s) {
    if (!x) {
      throw new RuntimeException(s);
    }
  }


  public static void claim(boolean x) {
    if (!x) {
      throw new RuntimeException("Claim failed");
    }
  }
}