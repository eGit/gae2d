package org.fonteditor.options.coords;

import org.fonteditor.utilities.log.Log;

/**
 * Specialised version of the Coords class for use in the editor...
 */

public class CoordsEditor extends Coords {
  private int log_aa_scale_factor_x;
  private int log_aa_scale_factor_y;
  private int aa_scale_factor_x;
  private int aa_scale_factor_y;

  public CoordsEditor(int width, int height, int aa_width, int aa_height, int log_aa_scale_factor_x, int log_aa_scale_factor_y) {
    super(width, height, aa_width, aa_height);

    setLogAntiAliasingScaleFactorX(log_aa_scale_factor_x);
    setLogAntiAliasingScaleFactorY(log_aa_scale_factor_y);
  }

  public void setWidth(int width) {
    super.setWidth(width);
    setXScale(width * aa_scale_factor_x);
  }

  public void setHeight(int height) {
    super.setHeight(height);
    setYScale(height * aa_scale_factor_y);
  }

  public CoordsEditor setAASizeEditor(int aa_x, int aa_y) {
    return new CoordsEditor(getWidth(), getHeight(), aa_x, aa_y, log_aa_scale_factor_x, log_aa_scale_factor_y);
  }

 public void setLogAntiAliasingScaleFactorX(int log_aa_scale_factor_x) {
    this.log_aa_scale_factor_x = log_aa_scale_factor_x;
    this.aa_scale_factor_x = 1 << log_aa_scale_factor_x;
    setXScale(getWidth() * aa_scale_factor_x);
  }

  public void setLogAntiAliasingScaleFactorY(int log_aa_scale_factor_y) {
    this.log_aa_scale_factor_y = log_aa_scale_factor_y;
    this.aa_scale_factor_y = 1 << log_aa_scale_factor_y;
    setYScale(getHeight() * aa_scale_factor_y);
  }

  public int getAAScaleFactorX() {
    return aa_scale_factor_x;
  }

  public int getAAScaleFactorY() {
    return aa_scale_factor_y;
  }

  public int getLogAAScaleFactorX() {
    return log_aa_scale_factor_x;
  }

  public int getLogAAScaleFactorY() {
    return log_aa_scale_factor_y;
  }

  public Object clone() throws CloneNotSupportedException {
    return new CoordsEditor(getWidth(), getHeight(), getAAWidth(), getAAHeight(), log_aa_scale_factor_x, log_aa_scale_factor_y);
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof CoordsEditor)) {
      return false;
    }

    CoordsEditor c = (CoordsEditor) o;

    if (c.getWidth() != getWidth()) {
      return false;
    }

    if (c.getHeight() != getHeight()) {
      return false;
    }

    if (c.getAAWidth() != getAAWidth()) {
      return false;
    }

    if (c.getAAHeight() != getAAHeight()) {
      return false;
    }

    if (c.getXScale() != getXScale()) {
      return false;
    }

    if (c.getYScale() != getYScale()) {
      return false;
    }

    if (c.aa_scale_factor_x != aa_scale_factor_x) {
      return false;
    }

    if (c.aa_scale_factor_y != aa_scale_factor_y) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return getWidth() ^ (getHeight() << 8) ^ (getAAWidth() << 16) ^ (getAAHeight() << 24) ^ (getXScale() << 14) ^ (getYScale() << 22) ^ (aa_scale_factor_x << 12) ^ (aa_scale_factor_y << 20);
  }

  void dump() {
    super.dump();
    Log.log("log_aa_scale_factor_x:" + log_aa_scale_factor_x);
    Log.log("log_aa_scale_factor_y:" + log_aa_scale_factor_y);
    Log.log("aa_scale_factor_x:" + aa_scale_factor_x);
    Log.log("aa_scale_factor_y:" + aa_scale_factor_y);
  }
}
