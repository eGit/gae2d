package org.fonteditor.font;

import org.fonteditor.FEFontRenderer;
import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.kerning.Kerning;
import org.fonteditor.kerning.KerningType;
import org.fonteditor.options.display.DisplayOptions;

import com.jgraph.gaeawt.java.awt.FontMetrics;

public class FEFontMetrics extends FontMetrics {
  private FEFont font;
  private FEFontRenderer renderer;
  private DisplayOptions display_options;
  private KerningType kerning_type;

  private int max_width = -1;
  private int max_descent = -1;

  private static final char CHARACTER_BASELINE = 'o';
  private static final char CHARACTER_ASCENT = 'H';
  private static final char CHARACTER_DESCENT = 'g';
  private static final char CHARACTER_BAR = '|';

  public FEFontMetrics(FEFont font, FEFontRenderer renderer, DisplayOptions display_options, KerningType kerning_type) {
    super(null);
    this.font = font;
    this.renderer = renderer;
    this.display_options = display_options;
    this.kerning_type = kerning_type;
  }

  public int getAscent() {
    return getCachedGlyph(CHARACTER_ASCENT).getHeight();
  }

  public int getDescent() {
    CachedGlyph cached_glyph_g = getCachedGlyph(CHARACTER_DESCENT);
    CachedGlyph cached_glyph_h = getCachedGlyph(CHARACTER_ASCENT);
    int bottom_of_g = cached_glyph_g.getHeight() + cached_glyph_g.getOffsetY();
    int bottom_of_h = cached_glyph_h.getHeight() + cached_glyph_h.getOffsetY();

    return bottom_of_g - bottom_of_h;
  }

  public int getLeading() {
    return getAscent() / 4 + 1;
  }

  //cached for speed...
  public int getMaxDescent() {
    if (max_descent == -1) {
      int baseline = getAscentPlusDescent(CHARACTER_BASELINE);
      for (int i = 32; i < 127; i++) {
        int descent = getAscentPlusDescent((char) i) - baseline;
        max_descent = Math.max(max_descent, descent);
      }
    }

    return max_descent;
  }

  // cached for speed...
  public int getMaxAdvance() {
    if (max_width == -1) {
      for (int i = 0; i < 255; i++) {
        max_width = Math.max(max_width, getCachedGlyph((char) i).getWidth());
      }
    }

    return max_width;
  }

  /** Returns the distance from the bottom of the character to the maximum ascent point. */
  private int getAscentPlusDescent(char ch) {
    CachedGlyph glyph = getCachedGlyph(ch);
    return glyph.getOffsetY() + glyph.getHeight();
  }

  public int charWidth(char ch) {
    CachedGlyph glyph = getCachedGlyph(ch);
    return glyph.getWidth();
  }

  public int charsWidth(char[] chars, int start, int length) {
    if (length == 0) {
      return 0;
    }
    char previous = CHARACTER_BAR;
    int width = -renderer.getKerningDistance(previous, font, display_options, chars[0], font, display_options, kerning_type);
    width -= Kerning.getKerningGap(display_options.getCoords().getAAHeight());

    for (int i = start; i < start + length; i++) {
      char ch = chars[i];
      int kd = renderer.getKerningDistance(previous, font, display_options, ch, font, display_options, kerning_type);

      width += kd + Kerning.getKerningGap(display_options.getCoords().getAAHeight());
      previous = ch;
    }

    // A bit of a crude hack to add the width of the last character...
    width += renderer.getKerningDistance(previous, font, display_options, CHARACTER_BAR, font, display_options, kerning_type);

    return width;
  }

  private CachedGlyph getCachedGlyph(char ch) {
    return renderer.getCachedGlyph(ch, font, display_options);
  }
}
