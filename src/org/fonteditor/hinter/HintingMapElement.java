package org.fonteditor.hinter;

public class HintingMapElement {
  private int value_old;
  private int value_new;
  private boolean is_top;

  HintingMapElement(int value_old, int value_new, boolean is_top) {
    setValueOld(value_old);
    setValueNew(value_new);
    setIsTop(is_top);
  }

  void setValueOld(int value_old) {
    this.value_old = value_old;
  }

  int getValueOld() {
    return value_old;
  }

  void setValueNew(int value_new) {
    this.value_new = value_new;
  }

  int getValueNew() {
    return value_new;
  }

  void setIsTop(boolean is_top) {
    this.is_top = is_top;
  }

  boolean getIsTop() {
    return is_top;
  }
}
