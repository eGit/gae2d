package org.fonteditor.kerning;

/**
 * Character preprocessor for Kerning routines...
 */

public class KerningPreprocessor {
  // A bit of a hack, really.
  // May cause havoc if these characters aren't available...
  public static char process(char c) {
    switch (c) {

      case ' ' :
        return 'H';

      case '\'' :
      case '`' :
      case '.' :
      case ',' :
      case ';' :
      case ':' :
        return '!';

      case '-' :
        return 'o';
    }
    
    return c;
  }
}
