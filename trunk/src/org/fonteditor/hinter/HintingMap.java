package org.fonteditor.hinter;

public class HintingMap {
  private HintingMapElement[] element = new HintingMapElement[0];
  
  void add(HintingMapElement e) {
    HintingMapElement[] old = this.element;
    int size = old.length;
    this.element = new HintingMapElement[size + 1];
    System.arraycopy(old, 0, this.element, 0, size);
    setElement(size, e);
  }

  void setElement(int i, HintingMapElement element) {
    this.element[i] = element;
  }

  HintingMapElement getElement(int i) {
    return element[i];
  }
  
  int getLength() {
    return element.length;
  }
}
