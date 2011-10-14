package org.fonteditor.options.pen;

public class PenFactory {
  public static Pen newPen(String pen, int size) {
    if ("Round".equals(pen)) {
      return new PenRound(size);
    }

    if ("Square".equals(pen)) {
      return new PenSquare(size);
    }

    if ("Uneven".equals(pen)) {
      return new PenUneven(size);
    }

    return null;
  }
}
