package org.fonteditor.sliders;

import org.fonteditor.utilities.sort.Comparator;
import org.fonteditor.utilities.sort.QuickSort;

/**
 * Manager for the sliders...
 */

public class SliderManager {
  private Slider[] new_array;
  private static final int INCREMENT = 8;
  private int number = 0;
  private int max_number = 0;
  private Slider[] array = new Slider[max_number];

  void add(Slider base) {
    if (number >= max_number) {
      makeMore();
    }
    array[number++] = base;
  }

  private void makeMore() {
    new_array = new Slider[max_number + INCREMENT];
    System.arraycopy(array, 0, new_array, 0, max_number);
    array = new_array;
    max_number += INCREMENT;
  }

  public void deselect() {
    for (int i = 0; i < number; i++) {
      array[i].setSelected(false);
    }
  }

  public int getNumberSelected() {
    int cnt = 0;

    for (int i = 0; i < number; i++) {
      if (array[i].areAnySelected()) {
        cnt++;
      }
    }
    return cnt;
  }

  void sort() {
    Comparator sc = new Comparator() {
    // Compare two SliderBase. Callback for sort.
    // effectively returns a-b;
    // e.g. +1 (or any +ve number) if a > b
    //       0                     if a == b
    //      -1 (or any -ve number) if a < b
  public final int compare(Object a, Object b) {
        if (a == null) {
          return 0;
        }
        return ((Slider) a).compareTo((Slider) b);
      }

      /* compare comparators */
      public final boolean equals(Object o) {
        return (this == o);
      }

      public int hashCode() {
        return super.hashCode();
      }
    };

    QuickSort.sort(array, sc);
  }

  // the "longest" slider wins out...
  void dedupe() {
    //dump("in");
    int out_index = 0;

    //Log.log("number" + number);
    //Log.log("array" + array.length);
    if (number > 1) {
      array[out_index++] = array[0];
      for (int i = 1; i < number; i++) {
        Slider s1 = array[i];
        Slider s2 = array[out_index - 1];

        if (s1 != null) {
          if (s1.simpleCompareTo(s2) != 0) {
            array[out_index++] = array[i];
          }
        }
      }
      number = out_index;
    }
    //dump("out");
  }

  public int getMin() {
    return (number > 0) ? array[0].getPosition() : -1;
  }

  public int getMax() {
    return (number > 0) ? array[number - 1].getPosition() : -1;
  }

  private int nextImportantSlider(int n, int direction) {
    n += direction;
    while ((n > 0) && (n < number - 1)) {
      if (array[n].canMove()) {
        return n;
      }
      n += direction;
    }
    return n;
  }

  public boolean isSliderAt(int value) {
    for (int i = 0; i < number; i++) {
      Slider s = array[i];

      if (s.getPosition() == value) {
        return true;
      }
    }
    return false;
  }

  public void select(int position) {
    for (int i = 0; i < number; i++) {
      if (Math.abs(array[i].getPosition() - position) < 0x200) {
        array[i].setSelected(true);
      }
    }
  }

  public int getNumber() {
    return number;
  }

  public Slider getSlider(int i) {
    return array[i];
  }
}

//  private void dump(String pfx) {
//    Log.log("--------------------");
//    for (int i = 0; i < number; i++) {
//      SliderBase s1 = array[i];
//      Log.log(pfx + "(" + i + "/" + number + ") - " + s1.position + " - " + s1.type);
//    }
//  }

//  void translate(int delta) {
//    for (int i = 0; i < number; i++) {
//      array[i].setPosition(array[i].getPosition() + delta);
//    }
//  }

//  private void moveSlider(int old_pos, int new_pos) {
//    for (int i = 0; i < number; i++) {
//      if (array[i].getPosition() == old_pos) {
//        array[i].setPosition(new_pos);
//     }
//    }
//  }

/*
  // min---centre---max (correct)...
  void rescaleRange(int min, int centre, int max, int new_centre) {
    if (new_centre < centre) {
      rescaleWithFixedLeftOrTop(min, centre, new_centre);
      rescaleWithFixedRightOrBottom(max, centre, new_centre);
    }
    if (centre < new_centre) {
      rescaleWithFixedRightOrBottom(max, centre, new_centre);
      rescaleWithFixedLeftOrTop(min, centre, new_centre);
    }
  }
  */

/*
  public void rescaleWithFixedLeftOrTop(int fixed, int o, int n) {
    int d1 = (o - fixed) >> 1;

    if (d1 == 0) {
      return;
    }
    int d2 = (n - fixed) >> 1;

    for (int i = 0; i < number; i++) {
      SliderBase p = array[i];

      if ((p.getPosition() >= fixed) && (p.getPosition() <= o)) {
        p.setPosition(fixed + ((p.getPosition() - fixed) * d2) / d1);
      }
    }
  }

  public void rescaleWithFixedRightOrBottom(int fixed, int o, int n) {
    int d1 = (fixed - o) >> 1;

    if (d1 == 0) {
      return;
    }
    int d2 = (fixed - n) >> 1;

    for (int i = 0; i < number; i++) {
      SliderBase p = array[i];

      if ((p.getPosition() >= o) && (p.getPosition() <= fixed)) {
        p.setPosition(fixed - ((fixed - p.getPosition()) * d2) / d1);
      }
    }
  }
  */

//  boolean canMove(int n) {
//    return array[n].canMove();
//  }