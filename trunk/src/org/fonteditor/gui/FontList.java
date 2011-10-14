package org.fonteditor.gui;

import java.awt.GraphicsEnvironment;

import org.fonteditor.FEConstants;

public class FontList implements FEConstants {
  private static String[] system_fonts = { /**/ }; 
  private static String[] internal_fonts = { "lucky", "nofrills", "typeright", "hollywood", "robocop" };

  public static String[] getSystemFonts() {
    if (JAVA_2) {
      system_fonts = new String[]{""}; // FIXME GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    }
    
    return system_fonts;
  }

  public static String[] getInternalFonts() {
    return internal_fonts;
  }

  public static boolean isSystemFont(String s) {
    return isInList(s, system_fonts);
  }

  public static boolean isInternalFont(String s) {
    return isInList(s, internal_fonts);
  }

  private static boolean isInList(String s, String[] sa) {
    for (int i = 0; i < sa.length; i++) {
      if (s.equals(sa[i])) {
        return true;
      }
    }

    return false;
  }
}
