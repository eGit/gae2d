package org.fonteditor.utilities.general;

public class Range {
  private int min;
  private int max;

  public Range(int min, int max) {
    this.min = min;
    this.max = max;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }

  public boolean doesOverlap(Range other) {
    if (max < other.min) {
      return false;
    }
    return !(min > other.max);
  }

  public Range overlap(Range other) {
    if (!doesOverlap(other)) {
      return null;
    }
    int new_min = (min < other.min) ? other.min : min;
    int new_max = (max > other.max) ? other.max : max;

    return new Range(new_min, new_max);
  }
}
