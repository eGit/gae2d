package org.fonteditor.cache;

import org.fonteditor.graphics.GraphicsConverter;
import org.fonteditor.graphics.GreyByteArray;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.kerning.Kerning;

/**
 * Cache of a *particular* glyph...
 */

public class CachedGlyph {
  /** Monochrome master image (with transparency) */
  private ImageWrapper image_wrapper;
  /** Grey master byte array */
  private GreyByteArray grey_byte_array;
  /** Y offset of bitmap start from "origin" - in top left */
  private int offset_y;
  /** Width of bitmap data */
  private int width = -1;
  /** Height of bitmap data (offset not included) */
  private int height = -1;

  private Kerning kerning;

  public CachedGlyph(ImageWrapper image_wrapper, int offset_y) {
    this.image_wrapper = image_wrapper;
    this.offset_y = offset_y;
  }

  public CachedGlyph(GreyByteArray grey_byte_array, int offset_y) {
    this.grey_byte_array = grey_byte_array;
    this.offset_y = offset_y;
  }

  public int getWidth() {
    if (width < 0) {
      width = getGreyByteArray().getWidth(); // lazy...
    }

    return width;
  }

  public int getHeight() {
    if (height < 0) {
      height = getGreyByteArray().getHeight(); // lazy...
    }

    return height;
  }

  public ImageWrapper getImageWrapper() {
    if (image_wrapper == null) {
      image_wrapper = GraphicsConverter.convert(grey_byte_array); // lazy...
    }

    return image_wrapper;
  }

  public GreyByteArray getGreyByteArray() {
    if (grey_byte_array == null) {
      grey_byte_array = GraphicsConverter.convert(image_wrapper); // lazy...
    }

    return grey_byte_array;
  }

  public int getOffsetY() {
    return offset_y;
  }

  public Kerning getKerning() {
    if (kerning == null) {
      kerning = new Kerning(this); // lazy...
    }

    return kerning;
  }
}
