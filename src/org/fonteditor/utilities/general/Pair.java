package org.fonteditor.utilities.general;

public class Pair {
  private Object first;
  private Object second;

  public Pair(Object first, Object second) {
    this.first = first;
    this.second = second;
  }

  public void setFirst(Integer first) {
    this.first = first;
  }

  public Object getFirst() {
    return first;
  }

  public void setSecond(Object second) {
    this.second = second;
  }

  public Object getSecond() {
    return second;
  }
}
