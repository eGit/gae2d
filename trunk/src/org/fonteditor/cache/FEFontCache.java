package org.fonteditor.cache;

import java.util.Hashtable;

import org.fonteditor.font.FEFont;
import org.fonteditor.options.display.DisplayOptions;

/**
  * Manages the Font Cache...
  */

public class FEFontCache {
  private Hashtable hashtable = new Hashtable();
  private FEDisplayOptionsCache cached_display_options_cache;
  private FEFont last_fefont;

  FEDisplayOptionsCache getFEDisplayOptionsCache(FEFont fefont) {
        //if (!fefont.equals(last_fefont)) {
//        last_fefont = fefont;
//          try {
//            last_fefont = (FEFont) fefont.clone();
//          } catch (CloneNotSupportedException e) {
//            //
//            Log.log("e.printStackTrace();");
//          }
          //cached_display_options_cache = (FEDisplayOptionsCache) hashtable.get((Object) fefont);
        //}
      //return cached_display_options_cache;
    return (FEDisplayOptionsCache) hashtable.get((Object) fefont);
  }

  public CachedGlyph getCachedGlyph(FEFont fefont, DisplayOptions gdo, char c) {
    FEDisplayOptionsCache display_cache = getFEDisplayOptionsCache(fefont);
    // FEDisplayOptionsCache display_cache = (FEDisplayOptionsCache) hashtable.get((Object) fefont);
    //(FEDisplayOptionsCache) hashtable.get((Object) fefont);

    if (display_cache == null) {
      display_cache = new FEDisplayOptionsCache(fefont);
      hashtable.put((Object) fefont, (Object) display_cache);
    }
    return display_cache.getCachedGlyph(gdo, c);
  }

  public void remove(FEFont font) {
    if (hashtable != null) {
      if (font != null) {
        hashtable.remove((Object) font);
        // System.gc();
      }
    }
  }

  public void remove(FEFont font, DisplayOptions gdo, char c) {
    if (hashtable != null) {
      FEDisplayOptionsCache display_cache = (FEDisplayOptionsCache) hashtable.get((Object) font);

      display_cache.remove(gdo, c);
    }
  }

  public void removeAll() {
    hashtable.clear();
    // System.gc();
  }
}