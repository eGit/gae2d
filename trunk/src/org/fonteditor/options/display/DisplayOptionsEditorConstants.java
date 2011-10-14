package org.fonteditor.options.display;

import org.fonteditor.options.coords.CoordsEditor;

/**
 * DisplayOptions in the Editor - default values...
 */

public interface DisplayOptionsEditorConstants {
  boolean DEFAULT_SHOW_SLIDERS = true;
  boolean DEFAULT_SHOW_GRID = true;
  boolean DEFAULT_BORDER = true;
  boolean DEFAULT_SHOW_POINTS = true;
  boolean DEFAULT_SHOW_SPRINGS = false;
  CoordsEditor DEFAULT_COORDS_EDITOR = new CoordsEditor(40, 40, 12, 20, 0, 0);
}
