package org.fonteditor.kerning;

import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.utilities.general.Utils;

/**
 * Kerning data for a *particular* cached glyph...
 */

public class KerningSpreader {
  private static final boolean KERNING_BLUR = true;

  /** Reference back to the CachedGlyph this kerning data is associated with */
  //private CachedGlyph cached_glyph;

  static int[] spreadKerningDataRHS(CachedGlyph cached_glyph, int[] offsets) {
    if (!KERNING_BLUR) {
      return offsets;
    }

    int length = offsets.length;
    int height = cached_glyph.getHeight();
    int[] new_data = new int[length];

    for (int y = height - 1; --y >= 1;) {
      new_data[y] = Utils.max(offsets[y - 1], offsets[y], offsets[y + 1]);
    }

    if (length >= 2) {
      new_data[length - 1] = Math.max(offsets[length - 1], offsets[length - 2]);
      new_data[0] = Math.max(offsets[0], offsets[1]);
    }

    return new_data;
  }

  static int[] spreadKerningDataLHS(CachedGlyph cached_glyph, int[] offsets) {
    if (!KERNING_BLUR) {
      return offsets;
    }

    int length = offsets.length;
    int height = cached_glyph.getHeight();
    int[] new_data = new int[length];

    for (int y = height - 1; --y >= 1;) {
      new_data[y] = Utils.min(offsets[y - 1], offsets[y], offsets[y + 1]);
    }

    if (length >= 2) {
      new_data[length - 1] = Math.min(offsets[length - 1], offsets[length - 2]);
      new_data[0] = Math.min(offsets[0], offsets[1]);
    }

    return new_data;
  }
}
