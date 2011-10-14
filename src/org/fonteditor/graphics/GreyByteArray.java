package org.fonteditor.graphics;

/**
 * Class representing a 256-grey font bitmap.
 * This is the most primitive and fundamental sort of font bitmap.
 * All other fypes of cached font bitmap are derived from this one.
 */

public class GreyByteArray implements Cloneable {
  private byte[] source;
  private int width;
  private int height;

  public Object clone() {
    byte[] source_new = new byte[source.length];

    System.arraycopy(source, 0, source_new, 0, source.length);
    return new GreyByteArray(source_new, getWidth(), getHeight());
  }

  public GreyByteArray(byte[] a, int w, int h) {
    createImageFromArray(a, w, h);
  }

  private void createImageFromArray(byte[] a, int w, int h) {
    source = a;
    width = w;
    height = h;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public byte[] getArray() {
    return source;
  }

  public int getPoint(int x, int y) {
    return getArray()[x + y * width];
  }
}

//  /**
//   *  Copy constructor :-(
//   * Should use clone();
//   */
//  
//  public GreyByteArray(GreyByteArray tti) {
//    byte[] in_pix = tti.getArray();
//    source = new byte[in_pix.length];
//    width = tti.getWidth();
//    height = tti.getHeight();
//    
//    System.arraycopy(in_pix, 0, source, 0, in_pix.length);
//  }
