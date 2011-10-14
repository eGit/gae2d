package org.fonteditor.sliders;

import org.fonteditor.elements.points.FEPoint;

/**
 * Sliders represent vertical or horizontal markers...
 * ...which are aligned with vertical or horizontal edges of glyph outlines...
 */

public class Slider implements Cloneable, SliderConstants {
  private int type;
  private int orientation;
  private int length;
  private FEPoint point; // ?
  private boolean direction = false;
  private boolean selected = false;

  Slider(int orientation, int type, boolean direction, int length, FEPoint point) {
    this.type = type;
    this.length = length;
    this.selected = false;
    this.direction = direction;
    this.point = point;
    this.orientation = orientation;
  }
  
      public Object clone() throws CloneNotSupportedException {
    return new Slider(orientation, type, direction, length, point);
  }


  int compareTo(Object o) {
    if (o == null) {
      return 0;
    }
    Slider sb = (Slider) o;

    int sb_position = (sb.orientation == VERTICAL) ? sb.point.getX() : sb.point.getY();
    int position = (orientation == VERTICAL) ? point.getX() : point.getY();

    if (sb_position == position) {
      if (type == sb.type) {
        return sb.length - length;
      }
      return type - sb.type;
    }
    return (sb_position < position) ? 1 : -1;
  }

  int simpleCompareTo(Object o) {
    if (o == null) {
      return 0;
    }

    Slider sb = (Slider) o;

    int sb_position = (sb.orientation == VERTICAL) ? sb.point.getX() : sb.point.getY();
    int position = (orientation == VERTICAL) ? point.getX() : point.getY();

    if (sb_position == position) {
      return 0;
    }

    return (sb_position < position) ? 1 : -1;
  }

  public boolean canMove() {
    return type == PARALLEL;
  }

  public int getPosition() {
    return (orientation == VERTICAL) ? point.getX() : point.getY();
  }

  public boolean isHomewards() {
    return direction;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public boolean areAnySelected() {
    return selected;
  }

  void setType(int type) {
    this.type = type;
  }

  int getType() {
    return type;
  }
}
