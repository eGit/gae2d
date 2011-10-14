package org.fonteditor.font.transforms;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.options.display.DisplayOptions;

/**
 * Responsible for creating expanded/condensed outlines.
 */

public class Expander {
  public static void expand(FEGlyph glyph, DisplayOptions gdo) {
    int expand_factor = gdo.getExpand();
    if (expand_factor > 0) {
      FEPointList fepl_of_glyph = glyph.getFEPointList(gdo);
      for (int index = fepl_of_glyph.getNumber(); --index >= 0;) {
        FEPoint point = fepl_of_glyph.getPoint(index);
        point.setX((point.getX() * expand_factor) >> 10);
      }
    }
  }
}
