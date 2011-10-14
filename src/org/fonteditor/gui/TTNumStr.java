package org.fonteditor.gui;

public class TTNumStr {
  private int number;
  private String string;

  public TTNumStr(int n, String s) {
    number = n;
    string = s;
  }

  void setNumber(int number) {
    this.number = number;
  }

  int getNumber() {
    return number;
  }

  void setString(String string) {
    this.string = string;
  }

  String getString() {
    return string;
  }
}
