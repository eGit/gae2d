package org.fonteditor.options.pen;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.graphics.WideLine;
import org.fonteditor.options.coords.Coords;

import com.jgraph.gaeawt.java.awt.Graphics;

public class PenSquare extends Pen {
  public PenSquare(int width) {
    setWidth(width);
  }

  public void drawStroke(Graphics g, FEPoint from, FEPoint to, Coords c) {
    WideLine.renderSquare(g, from, to, getWidth(), c);
  }

  public void quickDrawStroke(Graphics g, FEPoint from, FEPoint to, Coords c) {
    WideLine.renderSquare(g, from, to, getWidth(), c);
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof PenSquare)) {
      return false;
    }

    PenSquare pen = (PenSquare) o;

    return (pen.getWidth() == getWidth());
  }
}
