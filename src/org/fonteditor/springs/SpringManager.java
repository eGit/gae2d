package org.fonteditor.springs;

import org.fonteditor.options.display.DisplayOptions;

import com.jgraph.gaeawt.java.awt.Graphics;

/**
 * Manager for Springs.
 */

public class SpringManager {
  private int number = 0;
  private int number_of_springs = 0;
  private Spring[] springs;
  private Spring[] new_springs;
  private static final int INCREMENT = 16;

  //  private FEPath[] paths = new FEPath[number_of_springs];
  //  private void reset() {
  //    number = 0;
  //    number_of_springs = 0;
  //  }
  public Spring add(Spring spring) {
    for (int i = number; --i >= 0;) {
      if (spring.equals(springs[i])) {
        return null;
      }
    }
    if (number >= number_of_springs) {
      makeMore();
    }
    springs[number] = spring;
    return springs[number++];
  }


  private void makeMore() {
    new_springs = new Spring[number_of_springs + INCREMENT];
    if (springs != null) {
      System.arraycopy(springs, 0, new_springs, 0, number_of_springs);
    }
    springs = new_springs;
    number_of_springs += INCREMENT;
  }


  public void draw(Graphics g, DisplayOptions gdo) {
    for (int i = number; --i >= 0;) {
      Spring s = springs[i];

      s.draw(g, gdo);
    }
  }
}