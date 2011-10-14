package org.fonteditor.graphics;

/**
 * A GreyByteArray that has been translated.
 * Translation is currently Y offset only.
 * 
 * Translation is a performance optimisation.
 * It saves drawing lots of transparent pixels at the top of each glyph.
 */

public class GreyByteArrayTranslated {
  private GreyByteArray grey_byte_array;
  private int offset_y;

  public GreyByteArrayTranslated(GreyByteArray grey_byte_array, int offset_y) {
    this.grey_byte_array = grey_byte_array;
    this.offset_y = offset_y;
  }

  public GreyByteArray getGreyByteArray() {
    return grey_byte_array;
  }

  public int getOffsetY() {
    return offset_y;
  }
}
