package org.fonteditor.kerning;

import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.graphics.GreyByteArray;

/**
 * Kerning data for a *particular* cached glyph...
 */

public class Kerning {
  private static final boolean KERNING_BLUR = true;

  /** Reference back to the CachedGlyph this kerning data is associated with */
  private CachedGlyph cached_glyph;

  private int[] kerning_offsets_lhs = null;
  private int[] kerning_offsets_rhs = null;

  public Kerning(CachedGlyph cached_glyph) {
    this.cached_glyph = cached_glyph;
  }

  public int[] getKerningOffsetsLHS() {
    if (kerning_offsets_lhs == null) {
      GreyByteArray gba = cached_glyph.getGreyByteArray();
      int height = cached_glyph.getHeight();
      kerning_offsets_lhs = new int[height];
      for (int y = height; --y >= 0;) {
        kerning_offsets_lhs[y] = getLHSOffset(gba, y);
      }

      kerning_offsets_lhs = KerningSpreader.spreadKerningDataLHS(cached_glyph, kerning_offsets_lhs);
    }

    return kerning_offsets_lhs;
  }

  public int[] getKerningOffsetsRHS() {
    if (kerning_offsets_rhs == null) {
      GreyByteArray gba = cached_glyph.getGreyByteArray();
      int height = cached_glyph.getHeight();
      kerning_offsets_rhs = new int[height];
      for (int y = height; --y >= 0;) {
        kerning_offsets_rhs[y] = getRHSOffset(gba, y);
      }

      kerning_offsets_rhs = KerningSpreader.spreadKerningDataRHS(cached_glyph, kerning_offsets_rhs);
    }

    return kerning_offsets_rhs;
  }

  private int getOffset(GreyByteArray gba, int y, int x0, int dx) {
    byte[] ba = gba.getArray();

    int width = cached_glyph.getWidth();
    int offset = y * width + x0;

    do {
      if ((ba[offset] & 0xFF) < 0x80) {
        return x0;
      }
      x0 += dx;
      offset += dx;
    } while ((x0 >= 0) && (x0 < width));

    return x0 - dx;
  }

  private int getRHSOffset(GreyByteArray gba, int y) {
    return getOffset(gba, y, cached_glyph.getWidth() - 1, -1);
  }

  private int getLHSOffset(GreyByteArray gba, int y) {
    return getOffset(gba, y, 0, 1);
  }

  public static int getKerningGap(int character_size) {
    return character_size / 6;
  }
}
