package org.fonteditor.editor;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.utilities.callback.CallBack;

/**
  * Fix - "fixes" the points of a glyph in place.
  * The idea is to make the job of hinting by making glyphs more regularly spaced.
  * This is done by hinting them at a small point size they look good at - and then
  * storing the hinted points.
  *  
  * This is a temporary way of improving the display - until the hinting is capable
  * of good quality without this assistance...
  */

public class FEFixer {
  private FEGlyph glyph;

  public FEFixer(FEGlyph glyph) {
    this.glyph = glyph;
  }

  public void fix(DisplayOptions gdo) {
    FEPointList fepl = glyph.getFEPointList(gdo);
    fepl.executeOnEachPoint(getCallBackFix());
  }

  public CallBack getCallBackFix() {
    return new CallBack() {
      public void callback(Object o) {
        FEPoint point = (FEPoint) o;
        InstructionStream is = glyph.getInstructionStream();
        is.setPoint(point, point.getX(), point.getY());
      }
    };
  }

}
