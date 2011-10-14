package org.fonteditor.options.pen;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.graphics.WideLine;
import org.fonteditor.options.coords.Coords;

import com.jgraph.gaeawt.java.awt.Graphics;

public class PenUneven extends Pen {
  public PenUneven(int width) {
    setWidth(width);
  }

  public void drawStroke(Graphics g, FEPoint from, FEPoint to, Coords c) {
    WideLine.renderUneven(g, from, to, getWidth(), c);
  }

  public void quickDrawStroke(Graphics g, FEPoint from, FEPoint to, Coords c) {
    WideLine.renderUneven(g, from, to, getWidth(), c);
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof PenUneven)) {
      return false;
    }

    PenUneven pen = (PenUneven) o;

    return (pen.getWidth() == getWidth());
  }
}
