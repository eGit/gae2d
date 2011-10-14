package org.fonteditor.options.coords;


/**
 * Coords factory...
 */

public class CoordsFactory {
  public static Coords makeCoords(int siz, int aa_sf_x, int aa_sf_y) {
    return new Coords((siz >> 1) * aa_sf_x, siz * aa_sf_y, siz >> 1, siz);
  }
}
