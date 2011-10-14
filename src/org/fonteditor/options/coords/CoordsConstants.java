package org.fonteditor.options.coords;

public interface CoordsConstants {
  int LOG_FACTOR_X = 15;
  int LOG_FACTOR_Y = 16;
  
  int MASK_X = -1 << LOG_FACTOR_X;
  int MASK_Y = -1 << LOG_FACTOR_Y;
  
  int FACTOR_X = 1 << LOG_FACTOR_X;
  int FACTOR_Y = 1 << LOG_FACTOR_Y;
}
