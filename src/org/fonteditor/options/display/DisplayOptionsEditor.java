package org.fonteditor.options.display;

import org.fonteditor.options.coords.Coords;
import org.fonteditor.options.coords.CoordsEditor;
import org.fonteditor.options.pen.Pen;
import org.fonteditor.options.pen.PenRound;

/**
 * DisplayOptions for each glyph...
 * You'll need to use this class if using the renderer...
 */

//needs to be immutable...

public class DisplayOptionsEditor extends DisplayOptions implements DisplayOptionsEditorConstants {
  private boolean show_sliders = true;
  private boolean show_grid = true;
  private boolean border = false;
  private boolean show_points = false;
  private boolean show_springs = true;

  public DisplayOptionsEditor(boolean show_sliders, boolean show_grid, boolean hint, boolean fill, boolean border, boolean show_points, boolean show_springs, Pen pen, CoordsEditor coords) {
    set(show_sliders, show_grid, hint, fill, border, show_points, show_springs, pen, coords);
  }

  public DisplayOptionsEditor() {
    set(DEFAULT_SHOW_SLIDERS, DEFAULT_SHOW_GRID, DEFAULT_HINT, DEFAULT_FILL, DEFAULT_BORDER, DEFAULT_SHOW_POINTS, DEFAULT_SHOW_SPRINGS, DEFAULT_PEN, DEFAULT_COORDS_EDITOR);
  }

  private void set(boolean show_sliders, boolean show_grid, boolean hint, boolean fill, boolean border, boolean show_points, boolean show_springs, Pen pen, CoordsEditor coords_editor) {
    this.setHint(hint);
    this.setFill(fill);
    this.setPen(pen);
    this.setCoords((Coords) coords_editor);

    this.show_sliders = show_sliders;
    this.show_grid = show_grid;
    this.border = border;
    this.show_points = show_points;
    this.show_springs = show_springs;
  }

  public static DisplayOptions getGDOForScaling() {
    return new DisplayOptionsEditor(DEFAULT_SHOW_SLIDERS, DEFAULT_SHOW_GRID, false, DEFAULT_FILL, false, false, false, DEFAULT_PEN, DEFAULT_COORDS_EDITOR);
  }

  private static DisplayOptions getGDOdefaults() {
    return new DisplayOptionsEditor(DEFAULT_SHOW_SLIDERS, DEFAULT_SHOW_GRID, DEFAULT_HINT, DEFAULT_FILL, false, false, false, DEFAULT_PEN, DEFAULT_COORDS_EDITOR);
  }

  public static DisplayOptions getGDOrender(int siz, int aa_sf_x, int aa_sf_y) {
    CoordsEditor c = new CoordsEditor((siz >> 1) * aa_sf_x, siz * aa_sf_y, siz >> 1, siz, 2, 2);

    return new DisplayOptionsEditor(false, false, true, false, false, false, false, new PenRound(0), c);
  }

  /*
    public int getLineWidthOffsetEast() {
      return 0 - (getCoordsEditor().getOnePixelWidth() >> 1) + (isOutline() ? getPen().getRight() : 0); // OK
    }
  
    public int getLineWidthOffsetWest() {
      return 0 - (getCoordsEditor().getOnePixelWidth() >> 2) + (isOutline() ? -getPen().getLeft() : 0); // OK
    }
  
    public int getLineWidthOffsetNorth() {
      return 0 - (getCoordsEditor().getOnePixelHeight() >> 2) + (isOutline() ? -getPen().getTop() : 0);
    }
  
    public int getLineWidthOffsetSouth() {
      return 0 - (getCoordsEditor().getOnePixelHeight() >> 1) + (isOutline() ? getPen().getBottom() : 0);
    }
    */

  public boolean isShowGrid() {
    return show_grid;
  }

  public boolean isShowPoints() {
    return show_points;
  }

  public boolean isShowSliders() {
    return show_sliders;
  }

  public boolean isShowSprings() {
    return show_springs;
  }

  public boolean isBorder() {
    return border;
  }

  public Object clone() throws CloneNotSupportedException {
    // super.clone();
    return new DisplayOptionsEditor(show_sliders, show_grid, isHint(), isFill(), border, show_points, show_springs, getPen(), (CoordsEditor) getCoordsEditor().clone());
  }

  public CoordsEditor getCoordsEditor() {
    return (CoordsEditor) super.getCoords();
  }

//???
//  public void setCoordsEditor(CoordsEditor coords_editor) {
//    setCoords(coords_editor);
//  }

  //  public Coords getCoords() {
  // throw new RuntimeException("getCoords called!");
  //  }

  public void antiAliasing(boolean b) {
    getCoordsEditor().setLogAntiAliasingScaleFactorX(b ? 1 : 0);
    getCoordsEditor().setLogAntiAliasingScaleFactorY(b ? 1 : 0);
  }

  public void refresh() {
    getCoordsEditor().refresh();
    //getCoords().refresh();
  }

  public boolean isTruncate() {
    return false;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof DisplayOptionsEditor)) {
      return false;
    }

    DisplayOptionsEditor gdo = (DisplayOptionsEditor) o;

    if (gdo.getSlant() != getSlant()) {
      return false;
    }

    if (gdo.getExpand() != getExpand()) {
      return false;
    }

    if (gdo.isHint() != isHint()) {
      return false;
    }

    if (gdo.isFill() != isFill()) {
      return false;
    }

    if (!gdo.getPen().equals(getPen())) {
      return false;
    }

    if (gdo.show_sliders != show_sliders) {
      return false;
    }

    if (gdo.show_grid != show_grid) {
      return false;
    }

    if (gdo.border != border) {
      return false;
    }

    if (gdo.show_points != show_points) {
      return false;
    }

    if (gdo.show_springs != show_springs) {
      return false;
    }

    return gdo.getCoordsEditor().equals(getCoordsEditor());
  }

  public int hashCode() {
    int hash_code = 0;
    hash_code ^= getSlant() << 17;
    hash_code ^= getExpand() << 11;
    hash_code ^= isHint() ? 0x19754368 : 0x3974B3E4;
    hash_code ^= isFill() ? 0xC76D1425 : 0xE1637568;
    hash_code ^= getPen().hashCode();
    hash_code ^= getCoordsEditor().hashCode();

    hash_code ^= show_sliders ? 0x1C76DF25 : 0x2163789;
    hash_code ^= show_grid ? 0x23684530 : 0x47843698;
    hash_code ^= border ? 0x1365415 : 0xf3649db1;
    hash_code ^= show_points ? 0x136fd102 : 0x9436D41F;
    hash_code ^= show_springs ? 0x0874E3AA : 0x535AB014;

    return hash_code;
  }

  public void setShowSprings(boolean b) {
    this.show_springs = b;
  }

  public void setShowPoints(boolean b) {
    this.show_points = b;
  }

  public void setShowSliders(boolean b) {
    this.show_sliders = b;
  }

  public void setShowGrid(boolean b) {
    this.show_grid = b;
  }

  public void setBorder(boolean b) {
    this.border = b;
  }

  //  /**
  //   * Method dump.
  //   */
  //  private void dump() {
  //    Log.log("show_sliders:" + show_sliders);
  //    Log.log("show_grid:" + show_grid);
  //    Log.log("hint:" + hint);
  //    Log.log("outline:" + outline);
  //    Log.log("fill:" + fill);
  //    Log.log("border:" + border);
  //    Log.log("show_points:" + show_points);
  //    Log.log("show_springs:" + show_springs);
  //    Log.log("width:" + width);
  //    //Log.log("coords:" + coords);
  //  }

}
