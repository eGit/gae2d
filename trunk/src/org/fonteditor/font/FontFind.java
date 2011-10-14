package org.fonteditor.font;

import org.fonteditor.gui.FontList;

import com.jgraph.gaeawt.java.awt.Graphics;

public class FontFind {
  public static FEFont find(String s, Graphics g, int char_min, int char_max, boolean bold, boolean italic) {
    FEFont font = null;

    if (FontList.isInternalFont(s)) {
      //Log.log("Loading internal font: " + s);
      font = FontLoad.load(s + ".cff");
    }
    
    if (FontList.isSystemFont(s)) {
      //Log.log("Ripping font: " + s);
      font = FontRip.rip(s, g, char_min, char_max, bold, italic);
    }
    
    if (font == null) {
      throw(new RuntimeException("Problems finding Font"));
    }
    
    return font;
  }
}