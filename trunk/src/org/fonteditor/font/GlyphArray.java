package org.fonteditor.font;

import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.utilities.log.Log;

/**
 * An array of Glyphs...
 * Part of a font...
 */

public class GlyphArray {
  private FEGlyph[] glyph;

  public GlyphArray(int n) {
    glyph = new FEGlyph[n];
  }

  public void add(FEGlyph g, int n) {
    glyph[n] = g;
  }

  public int getMinX(int min, int max, DisplayOptions gdo) {
    int min_x = Integer.MAX_VALUE;

    for (int i = min; i < max; i++) {
      if (!isIgnoring(i)) {
        int min_this = glyph[i].getMinX(gdo);

        if (min_this < min_x) {
          min_x = min_this;
        }
      }
    }
    return min_x;
  }

  public int getMinY(int min, int max, DisplayOptions gdo) {
    int min_y = Integer.MAX_VALUE;

    for (int i = min; i < max; i++) {
      if (!isIgnoring(i)) {
        int min_this = glyph[i].getMinY(gdo);

        if (min_this < min_y) {
          min_y = min_this;
        }
      }
    }
    
    return min_y;
  }

  public int getMaxX(int min, int max, DisplayOptions gdo) {
    int max_x = Integer.MIN_VALUE;

    for (int i = min; i < max; i++) {
      if (!isIgnoring(i)) {
        int max_this = glyph[i].getMaxX(gdo);

        if (max_this > max_x) {
          max_x = max_this;
        }
      }
    }
    
    return max_x;
  }

  public int getMaxY(int min, int max, DisplayOptions gdo) {
    int max_y = Integer.MIN_VALUE;

    for (int i = min; i < max; i++) {
      if (!isIgnoring(i)) {
        int max_this = glyph[i].getMaxY(gdo);

        if (max_this > max_y) {
          max_y = max_this;
        }
      }
    }
    
    return max_y;
  }

  private boolean isIgnoring(int i) {
    if (i == 95) {
      return true;
    }
    
    return false;
  }

  public void showCharsWithX(int min, int max, int x, DisplayOptions gdo) {
    for (int i = min; i < max; i++) {
      if (glyph[i].hasX(x, gdo)) {
        Log.log("Extreme char x:" + i + " - " + (char) i);
      }
    }
  }

  public void showCharsWithY(int min, int max, int y, DisplayOptions gdo) {
    for (int i = min; i < max; i++) {
      if (glyph[i].hasY(y, gdo)) {
        Log.log("Extreme char y:" + i + " - " + (char) i);
      }
    }
  }

  public FEGlyph getGlyph(int i) {
    return glyph[i];
  }
}
