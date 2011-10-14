package org.fonteditor.hinter;

import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEFont;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.sliders.Slider;
import org.fonteditor.sliders.SliderManager;
import org.fonteditor.sliders.Sliders;

/**
 * Main hinting class.
 * Manages the alignment of vertical and horizontal Glyph path sections.
 * It's job is to make sure they fall on pixel boundaries.
 */

public class Hinter {
  private static boolean hint_x = true;
  private static boolean hint_y = true;

  public static void hint(FEGlyph glyph, DisplayOptions gdo) {
    if (gdo.isHint()) {
      final Sliders s = glyph.getSliders();

      // Prevent any shifting off the top or LHS caused by the width...

      final int v_offset = (gdo.getPen().getBottom() > 0) ? -gdo.getLineWidthOffsetNorth() : 0;

      final FEFont font = glyph.getFont();

      final HintingCues hinting_cues = font.getHintingCues();
      
      final int letter_o_bottom = hinting_cues.getBottomLetterO() + v_offset;
      final int letter_o_top = hinting_cues.getTopLetterO() + v_offset;
      final int letter_h_top = hinting_cues.getTopLetterH() + v_offset;

      final int new_letter_o_bottom = gdo.getCoords().nearestY(letter_o_bottom, gdo.getLineWidthOffsetNorth());
      final int new_letter_o_top = gdo.getCoords().nearestY(letter_o_bottom, gdo.getLineWidthOffsetSouth());
      final int new_letter_h_top = gdo.getCoords().nearestY(letter_h_top, gdo.getLineWidthOffsetSouth());

      HintingMapElement h_m_e;
      final HintingMap h_m = new HintingMap();

      h_m_e = new HintingMapElement(0, 0, true);
      h_m.add(h_m_e);
      h_m_e = new HintingMapElement(letter_h_top, new_letter_h_top, true);
      h_m.add(h_m_e);
      h_m_e = new HintingMapElement(letter_o_top, new_letter_o_top, true);
      h_m.add(h_m_e);
      h_m_e = new HintingMapElement(new_letter_o_top, new_letter_o_bottom, false);
      h_m.add(h_m_e);
      h_m_e = new HintingMapElement(0xFFFF, 0xFFFF, false);
      h_m.add(h_m_e);

      //values_old, values_new, is_top);

      glyph.translate(-gdo.getLineWidthOffsetWest(), new_letter_o_bottom - letter_o_bottom + v_offset, gdo);

      hintHorizontally(glyph, gdo, s);
      hintVertically(glyph, gdo, s, h_m);
    }
  }

  private static void hintVertically(FEGlyph glyph, DisplayOptions gdo, Sliders s, HintingMap hinting_map) {
    if (!hint_y) {
      return;
    }

    // Step 1 - hint according to baselines and top lines...

    final SliderManager s_m_h = (SliderManager) s.getSliderManagerHorizontal();
    final int new_letter_o_bottom = hinting_map.getElement(3).getValueNew();
    hintBasicBaseToBottom(glyph, gdo, s_m_h, new_letter_o_bottom);
    hintBasicBaseToTop(glyph, gdo, s_m_h, new_letter_o_bottom);

    // Step 2 - hint according to top and bottom of characters...

    hintWholeBaseToBottom(glyph, gdo, s_m_h);
    hintWholeBaseToTop(glyph, gdo, s_m_h);

    // Hint recursively inside existing regions...

    hintY(glyph, gdo, s_m_h);
  }

  private static void hintHorizontally(FEGlyph glyph, DisplayOptions gdo, Sliders s) {
    if (!hint_x) {
      return;
    }

    final SliderManager s_m_v = (SliderManager) s.getSliderManagerVertical();
    hintBasicRight(glyph, gdo, s_m_v);
    hintBasicLeft(glyph, gdo, s_m_v);

    hintX(glyph, gdo, s_m_v);
  }

  protected static void hintBasicRight(FEGlyph g, DisplayOptions gdo, SliderManager s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_min_pos = gdo.getCoords().nearestX(min_pos, gdo.getLineWidthOffsetWest());
    g.rescaleWithFixedRight(max_pos, min_pos, new_min_pos, gdo);

    //Log.log("min_pos:" + min_pos + " new_min_pos:" + new_min_pos);
  }

  protected static void hintBasicLeft(FEGlyph g, DisplayOptions gdo, SliderManager s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_max_pos = gdo.getCoords().nearestX(max_pos, gdo.getLineWidthOffsetEast());
    g.rescaleWithFixedLeft(min_pos, max_pos, new_max_pos, gdo);
  }

  protected static void hintBasicBaseToBottom(FEGlyph g, DisplayOptions gdo, SliderManager s_m, int base_line) {
    int min_pos = base_line;
    int max_pos = s_m.getMax();
    int new_max_pos = gdo.getCoords().nearestY(max_pos, gdo.getLineWidthOffsetSouth());
    g.rescaleWithFixedTop(min_pos, max_pos, new_max_pos, gdo);
  }

  protected static void hintBasicBaseToTop(FEGlyph g, DisplayOptions gdo, SliderManager s_m, int base_line) {
    int min_pos = s_m.getMin();
    int max_pos = base_line;
    int new_min_pos = gdo.getCoords().nearestY(min_pos, gdo.getLineWidthOffsetNorth());
    g.rescaleWithFixedBottom(max_pos, min_pos, new_min_pos, gdo);
  }

  protected static void hintWholeBaseToBottom(FEGlyph g, DisplayOptions gdo, SliderManager s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_max_pos = gdo.getCoords().nearestY(max_pos, gdo.getLineWidthOffsetSouth());
    g.rescaleWithFixedTop(min_pos, max_pos, new_max_pos, gdo);
  }

  protected static void hintWholeBaseToTop(FEGlyph g, DisplayOptions gdo, SliderManager s_m) {
    int min_pos = s_m.getMin();
    int max_pos = s_m.getMax();
    int new_min_pos = gdo.getCoords().nearestY(min_pos, gdo.getLineWidthOffsetNorth());
    g.rescaleWithFixedBottom(max_pos, min_pos, new_min_pos, gdo);
  }

  protected static void hintX(FEGlyph glyph, DisplayOptions gdo, SliderManager s_m) {
    if (s_m.getNumber() == 0) {
      return;
    }

    PointValueGetter pvg = new PointValueGetter() {
      int getPointValue(FEPoint point) {
        return point.getX();
      }
    };

    NearestValueGetter nvg = new NearestValueGetter() {
      int getNearestValue(DisplayOptions gdo, int v1, Slider s) {
        return gdo.getCoords().nearestX(v1, s.isHomewards() ? gdo.getLineWidthOffsetWest() : gdo.getLineWidthOffsetEast());
      }
    };

    SectionRescaler rescaler = new SectionRescaler() {
      void rescaleSection(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index) {
        int temp_x = point.getX();
        int temp_y = point.getY();
        fepl.scaleRangeX(min_index, index_on_path, new_pos, min_index, index_on_path);

        point.setX(temp_x);
        point.setY(temp_y);
        fepl.scaleRangeX(max_index, index_on_path, new_pos, index_on_path, max_index);
      }
    };

    recursivelyDivide(glyph, gdo, s_m, 0, s_m.getNumber() - 1, pvg, nvg, rescaler);
  }

  protected static void hintY(FEGlyph glyph, DisplayOptions gdo, SliderManager s_m) {
    if (s_m.getNumber() == 0) {
      return;
    }

    PointValueGetter pvg = new PointValueGetter() {
      int getPointValue(FEPoint point) {
        return point.getY();
      }
    };

    NearestValueGetter nvg = new NearestValueGetter() {
      int getNearestValue(DisplayOptions gdo, int v1, Slider s) {
        return gdo.getCoords().nearestY(v1, s.isHomewards() ? gdo.getLineWidthOffsetSouth() : gdo.getLineWidthOffsetNorth());
      }
    };

    SectionRescaler rescaler = new SectionRescaler() {
      void rescaleSection(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index) {
        int temp_x = point.getX();
        int temp_y = point.getY();
        fepl.scaleRangeY(min_index, index_on_path, new_pos, min_index, index_on_path);
        point.setX(temp_x);
        point.setY(temp_y);
        fepl.scaleRangeY(max_index, index_on_path, new_pos, index_on_path, max_index);
      }
    };

    recursivelyDivide(glyph, gdo, s_m, 0, s_m.getNumber() - 1, pvg, nvg, rescaler);
  }

  private static void recursivelyDivide(FEGlyph glyph, DisplayOptions gdo, SliderManager s_m, int min, int max, PointValueGetter pvg, NearestValueGetter nvg, SectionRescaler rescaler) {
    if (max - min < 2) {
      return;
    }

    int idx = (min + max) >> 1;
    Slider s = s_m.getSlider(idx);

    if (s.canMove()) {
      int old_pos = s.getPosition();
      int new_pos = nvg.getNearestValue(gdo, old_pos, s);

      int index_current = -1;

      do {
        FEPointList fepl_of_glyph = glyph.getFEPointList(gdo);
        index_current = getIndexOfNextMatchingPoint(fepl_of_glyph, index_current, old_pos, pvg);
        if (index_current >= 0) {
          FEPath path = glyph.getFEPathList(gdo).getPath(fepl_of_glyph.getPoint(index_current));
          FEPointList fepl = path.getFEPointList();
          FEPoint point = fepl_of_glyph.getPoint(index_current);
          int index_on_path = fepl.getIndexOf(point);
          if (index_on_path >= 0) {

            int min_index = getIndexOfNextSlider(fepl, index_on_path, s_m, -1, pvg);
            int max_index = getIndexOfNextSlider(fepl, index_on_path, s_m, 1, pvg);

            rescaler.rescaleSection(new_pos, fepl, point, index_on_path, min_index, max_index);
          }
        }
      } while (index_current >= 0);
    }

    recursivelyDivide(glyph, gdo, s_m, min, idx, pvg, nvg, rescaler);
    recursivelyDivide(glyph, gdo, s_m, idx, max, pvg, nvg, rescaler);
  }

  private static void rescaleSectionY(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index) {
    int temp_x = point.getX();
    int temp_y = point.getY();
    fepl.scaleRangeY(min_index, index_on_path, new_pos, min_index, index_on_path);
    point.setX(temp_x);
    point.setY(temp_y);
    fepl.scaleRangeY(max_index, index_on_path, new_pos, index_on_path, max_index);
  }

  private static int getIndexOfNextMatchingPoint(FEPointList fepl, int idx, int pos, PointValueGetter pvg) {
    int number = fepl.getNumber();

    if (idx == -1) {
      idx++;
    } else {
      for (; true;) {
        if (++idx >= number) {
          return -1;
        }

        if (pvg.getPointValue(fepl.getPoint(idx)) != pos) {
          break;
        }
      }
    }

    for (; true;) {
      if (pvg.getPointValue(fepl.getPoint(idx)) == pos) {
        return idx;
      }

      if (++idx >= number) {
        return -1;
      }
    }
  }

  private static int getIndexOfNextSlider(FEPointList fepl, int idx, SliderManager s_m, int dir, PointValueGetter pvg) {
    if (Math.abs(dir) != 1) {
      throw new RuntimeException("getIndexOfNextSlider:Looped");
    }

    int looped = idx;
    int pos = pvg.getPointValue(fepl.getPoint(looped));
    int number = fepl.getNumber();

    while (pvg.getPointValue(fepl.getPoint(idx)) == pos) {
      idx += dir;
      if (idx >= number) {
        idx = 0;
      }

      if (idx < 0) {
        idx = number - 1;
      }

      if (idx == looped) {
        return idx;
        // throw new RuntimeException("getIndexOfNextSlider:Looped"); // FIZ
      }
    }

    looped = idx;
    while (!s_m.isSliderAt(pvg.getPointValue(fepl.getPoint(idx)))) {
      idx += dir;
      if (idx >= number) {
        idx = 0;
      }

      if (idx < 0) {
        idx = number - 1;
      }

      if (idx == looped) {
        return idx;
        // throw new RuntimeException("getIndexOfNextSlider:Looped"); // FIZ
      }
    }

    return idx;
  }
}

abstract class PointValueGetter {
  abstract int getPointValue(FEPoint point);
}

abstract class NearestValueGetter {
  abstract int getNearestValue(DisplayOptions gdo, int v1, Slider s);
}

abstract class SectionRescaler {
  abstract void rescaleSection(int new_pos, FEPointList fepl, FEPoint point, int index_on_path, int min_index, int max_index);
}
