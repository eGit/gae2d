package org.fonteditor.font.transforms;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.options.display.DisplayOptions;

/**
 * Responsible for making outlines appear italicised.
 */

public class Slanter {
  public static void slant(FEGlyph glyph, DisplayOptions gdo) {
    int slant_factor = gdo.getSlant();
    if (slant_factor != 0) {
      FEPointList point_list = glyph.getFEPointList(gdo);
      FEPoint point;
      
      int len = point_list.getNumber();
      if (slant_factor > 0) {
        for (int index = len; --index >= 0;) {
          point = point_list.getPoint(index);
          point.setX(point.getX() + ((slant_factor * (0x10000 - point.getY())) >> 8));
        }
      } else if (slant_factor < 0) {
        for (int index = len; --index >= 0;) {
          point = point_list.getPoint(index);
          point.setX(point.getX() - ((slant_factor * point.getY()) >> 8));
        }
      }
    }
  }
}
