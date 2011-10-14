package org.fonteditor.font;

import org.fonteditor.hinter.HintingCues;
import org.fonteditor.options.coords.CoordsConstants;
import org.fonteditor.options.display.DisplayOptions;

/**
 * Represents a font...
 */

//Either - needs to be immutable...
// ...or clonable...

public class FEFont implements CoordsConstants, Cloneable {
  private static final boolean SHOW_EXTREME_CHARACTERS = false;

  private GlyphArray fega = new GlyphArray(256); // hardwired...

  private int min; // character number...
  private int max; // character number...
  private String name;
  private HintingCues hinting_cues;

  public FEFont(String name, int min, int max) {
    // super("monospaced", 1, 1); // FIZ
    this.min = min;
    this.max = max;
    this.name = name;
    fega = new GlyphArray(max);
  }

  public void scaleRipped() {
    DisplayOptions gdo = DisplayOptions.getGDOForScaling();

    for (int i = min; i < max; i++) {
      FEGlyph glyph = fega.getGlyph(i);

      // Translate...
      int glyph_min_x = glyph.getMinX(gdo);
      glyph.getInstructionStream().translateAll(-glyph_min_x, 0);
      glyph.resetRemakeFlag();
      glyph.resetLastGDO();
    }

    GlyphArray ga = getGlyphArray();

    int min_x = 0; // ga.getMinX(min, max, gdo);
    int min_y = ga.getMinY(min, max, gdo);
    int max_x = ga.getMaxX(min, max, gdo);
    int max_y = ga.getMaxY(min, max, gdo);

    if (SHOW_EXTREME_CHARACTERS) {
      ga.showCharsWithY(min, max, min_y, gdo);
      ga.showCharsWithY(min, max, max_y, gdo);
      ga.showCharsWithX(min, max, max_x, gdo);
    }

    float x_factor = (float) FACTOR_X / (float) (max_x - min_x);
    float y_factor = (float) FACTOR_Y / (float) (max_y - min_y);
    float factor = Math.min(x_factor, y_factor);

    for (int i = min; i < max; i++) {
      FEGlyph glyph = fega.getGlyph(i);

      glyph.getInstructionStream().translateAll(0, -min_y); // move the IS...

      // Rescale...
      glyph.getInstructionStream().scaleAll(factor, factor); // rescale the IS...

      glyph.resetRemakeFlag();
      glyph.resetLastGDO();
    }
  }

  public int getMax() {
    return max;
  }

  int getMin() {
    return min;
  }

  public GlyphArray getGlyphArray() {
    return fega;
  }

  /** 
   * Needs much more work...
   */
  public Object clone() throws CloneNotSupportedException {
    return this; // new FEFont(name, min, max);
  }

/** 
 * Needs more work...
 */
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof FEFont)) {
      return false;
    }

    FEFont font = (FEFont) o;

    if (font.min != min) {
      return false;
    }

    if (font.max != max) {
      return false;
    }

    if (!font.name.equals(name)) {
      return false;
    }

    return true;
  }

  /** 
   * Needs more work...
   */
  public int hashCode() {
    int hash_code = 0;
    hash_code ^= min << 17;
    hash_code ^= max << 19;
    hash_code ^= name.hashCode();

    return hash_code;
  }

  private void setHintingCues(HintingCues hinting_cues) {
    this.hinting_cues = hinting_cues;
  }

  private void setupHintingCues() {
    DisplayOptions gdo = DisplayOptions.getGDOForScaling();
    FEGlyph glyph;
    
    glyph = fega.getGlyph('o');

    int min_lower_o = glyph.getMinY(gdo);
    int max_lower_o = glyph.getMaxY(gdo);

    glyph = fega.getGlyph('H');

    int max_upper_h = glyph.getMaxY(gdo);

    hinting_cues = new HintingCues(min_lower_o, max_lower_o, max_upper_h);
  }

  public HintingCues getHintingCues() {
    if (hinting_cues == null) {
      setupHintingCues();
    }

    return hinting_cues;
  }
}
