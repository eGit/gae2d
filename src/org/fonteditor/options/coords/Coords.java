package org.fonteditor.options.coords;

import org.fonteditor.utilities.log.Log;

/**
 * Main coordinate system class for glyphs...
 * You'll need to use this class if using the renderer...
 */

public class Coords implements CoordsConstants, Cloneable {
  private int width = -1;
  private int height = -1;

  private int aa_width; // result after anti-aliasing...
  private int aa_height; // result after anti-aliasing...

  private int one_pixel_width; // derived...
  private int one_pixel_height; // derived...

  private int x_scale = -1; // derived...
  private int y_scale = -1; // derived...

  private int aa_dx; // derived...
  private int aa_dy; // derived...

  //----------------------------------------------------------------------------

  //  public Coords() {
  //    setWidth(0);
  //    setHeight(0);
  //    setAAWidth(0);
  //    setAAHeight(0);
  //  }

  public Coords(int width, int height, int aa_width, int aa_height) {
    setWidth(width);
    setHeight(height);
    setAAWidth(aa_width);
    setAAHeight(aa_height);
  }

  public void setWidth(int width) {
    this.width = width;
    this.x_scale = width;
    this.one_pixel_width = FACTOR_X / width;
  }

  public void setHeight(int height) {
    this.height = height;
    this.y_scale = height;
    this.one_pixel_height = FACTOR_Y / height;
  }

  public void setAAWidth(int aa_width) {
    this.aa_width = aa_width;
    this.aa_dx = FACTOR_X / aa_width;
  }

  public void setAAHeight(int aa_height) {
    this.aa_height = aa_height;
    this.aa_dy = FACTOR_Y / aa_height;
  }

  public Coords setAASize(int x, int y) {
    return new Coords(width, height, x, y);
  }

  /** 0000-FFFF -> pixels */
  public int scaleX(int x) {
    return (x * x_scale) >> LOG_FACTOR_X;
  }

  /** 0000-FFFF -> pixels */
  public int scaleY(int y) {
    return (y * y_scale) >> LOG_FACTOR_Y;
  }

  /** pixels -> 0000-FFFF */
  public int rescaleX(int x) {
    return (x << LOG_FACTOR_X) / x_scale;
  }

  /** pixels -> 0000-FFFF */
  public int rescaleY(int y) {
    return (y << LOG_FACTOR_Y) / y_scale;
  }

  public int nearestX(int x, int v) {
    x += (aa_dx >> 1) + v;
    return (((x * aa_width) & MASK_X) / aa_width) - v;
  }

  public int nearestY(int y, int v) {
    y += (aa_dy >> 1) + v;
    return (((y * aa_height) & MASK_Y) / aa_height) - v;
  }

  public int getAAHeight() {
    return aa_height;
  }

  public int getAAWidth() {
    return aa_width;
  }

  public int getHeight() {
    return height;
  }

  public int getOnePixelHeight() {
    return one_pixel_height;
  }

  public int getOnePixelWidth() {
    return one_pixel_width;
  }

  public int getWidth() {
    return width;
  }

  public int getXScale() {
    return x_scale;
  }

  public int getYScale() {
    return y_scale;
  }

  public void refresh() {
    setWidth(-99);
    setHeight(-99);
  }

  public void setXScale(int x_scale) {
    this.x_scale = x_scale;
  }

  public void setYScale(int y_scale) {
    this.y_scale = y_scale;
  }

  void dump() {
    Log.log("width:" + width);
    Log.log("height:" + height);
    Log.log("aa_width:" + aa_width);
    Log.log("aa_height:" + aa_height);
  }

  public Object clone() throws CloneNotSupportedException {
    return new Coords(width, height, aa_width, aa_height);
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Coords)) {
      return false;
    }

    Coords c = (Coords) o;

    if (c.width != width) {
      return false;
    }

    if (c.height != height) {
      return false;
    }

    if (c.aa_width != aa_width) {
      return false;
    }

    if (c.aa_height != aa_height) {
      return false;
    }

    if (c.x_scale != x_scale) {
      return false;
    }

    if (c.y_scale != y_scale) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return width ^ (height << 8) ^ (aa_width << 16) ^ (aa_height << 24) ^ (x_scale << 14) ^ (y_scale << 22);
  }
}

//  Coords modifyAASize(int x, int y) {
//    return new Coords(width, height, x, y);
//  }

//  Coords modifySize(int x, int y) {
//    return new Coords(x, y, aa_width, aa_height);
//  }

//void setSizeInternalZ(int width, int height) {
//this.width = width;
//this.height = height;
//this.x_scale = width;
//this.y_scale = height;
//this.one_pixel_width = FACTOR_X / width;
//this.one_pixel_height = FACTOR_Y / height;
//}

//  void setAAWidth(int aa_width) {
//    this.aa_width = aa_width;
//  }
//
//  void setAAHeight(int aa_height) {
//    this.aa_height = aa_height;
//  }

//  private int nearestX(int x) {
//    x += (aa_dx >> 1);
//
//    return ((x * aa_width) & MASK_X) / aa_width;
//  }
//
//  private int nearestY(int y) {
//    y += (aa_dy >> 1);
//
//    return ((y * aa_height) & MASK_Y) / aa_height;
//  }

//protected
//  protected void antiAliasingOff() {
//    setAntiAliasing(0, 0);
//  }
//
//  protected void antiAliasingOn() {
//    setAntiAliasing(1, 1);
//  }
//
//  protected void setAntiAliasing(int aax, int aay) {
//    log_aa_scale_factor_x = aax;
//    log_aa_scale_factor_y = aay;
//    aa_scale_factor_x = 1 << aax;
//    aa_scale_factor_y = 1 << aay;
//  }
