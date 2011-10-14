package org.fonteditor.kerning;

import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.utilities.general.For;
import org.fonteditor.utilities.general.Range;

/**
 * Kerning data for a *particular* cached glyph...
 */

public interface KerningTypes {
  KerningType PROPORTIONAL = new KerningType() {
    public int getKerningDistance(CachedGlyph cached_glyph1, CachedGlyph cached_glyph2) {
      int[] rhs_kerning_1 = cached_glyph1.getKerning().getKerningOffsetsRHS();
      int[] lhs_kerning_2 = cached_glyph2.getKerning().getKerningOffsetsLHS();
      int min_y1 = cached_glyph1.getOffsetY();
      int min_y2 = cached_glyph2.getOffsetY();
      int max_y1 = min_y1 + cached_glyph1.getHeight();
      int max_y2 = min_y2 + cached_glyph2.getHeight();
      Range r1 = new Range(min_y1, max_y1);
      Range r2 = new Range(min_y2, max_y2);
      int largest_offset = 1;

      if (r1.doesOverlap(r2)) {
        Range r3 = r1.overlap(r2);
        int end = r3.getMin();

        for (int y = r3.getMax(); --y >= end;) {
          int current_offset = rhs_kerning_1[y - min_y1] - lhs_kerning_2[y - min_y2];

          if (current_offset > largest_offset) {
            largest_offset = current_offset;
          }
        }
      }

      return largest_offset;
    }
  };
  
  KerningType SEMI_PROPORTIONAL = new KerningType() {
    public int getKerningDistance(CachedGlyph cached_glyph1, CachedGlyph cached_glyph2) {
     For.get(cached_glyph2);
      return cached_glyph1.getWidth();
    }
  };
}
