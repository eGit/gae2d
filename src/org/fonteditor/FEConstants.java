/* 
 * A list of the locations of other prominent constants
 * ===============================
 * SpringConstants.*
 * CoordsConstants.*
 * FEFont.SHOW_EXTREME_CHARACTERS
 * CachedGlyph.KERNING - NYI
 * CachedGlyph.KERNING_BLUR
 * ResourceLoader.RESOURCES_ZIPPED
 * 
 */

package org.fonteditor;

/** 
 * Main configuration constants...
 */
 
public interface FEConstants {
  /** Turns on access to the OS's True Type and Open Type fonts */
  boolean JAVA_2 = true;
  /** Turns on addittional gadgets and widgets - which are intended primarily for development purposes */
  boolean DEVELOPMENT_VERSION = true;
  /** Chooses whether the FontEditor is used */
  boolean EDITOR = true;
}
