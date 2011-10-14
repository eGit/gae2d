package org.fonteditor.elements.points;

import org.fonteditor.instructions.InstructionStream;

public class FEPoint {
  private int x;
  private int y;

  private int instruction_pointer; // for use keeping track of things in the editor...

  // int index; //  for use keeping track of things in overall point list...?
  public FEPoint(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public FEPoint() {
  }

  //  // copy contructor - kill this...
  //  public FEPoint(FEPoint p) {
  //    this.x = p.x;
  //    this.y = p.y;
  //    this.selected = p.selected;
  //    this.instruction_pointer = p.instruction_pointer;
  //  }

  public FEPoint(InstructionStream is) {
    instruction_pointer = is.getInstructionPointer();
    this.x = -1;
    this.y = -1;
  }

  public int squaredDistanceFrom(FEPoint p) {
    int dx = (p.getX() - x) >> 1;
    int dy = (p.getY() - y) >> 1;

    return dx * dx + dy * dy;
  }

  public int quickDistanceFrom(FEPoint p) {
    return (Math.abs(p.getX() - x) + Math.abs(p.getY() - y)) >> 1;
  }

  public boolean equals(Object o) {
    FEPoint fep = (FEPoint) o;

    return ((fep.x == x) && (fep.y == y));
  }

  public int hashCode() {
    return super.hashCode();
  }

  public void setInstructionPointer(int instruction_pointer) {
    this.instruction_pointer = instruction_pointer;
  }

  public int getInstructionPointer() {
    return instruction_pointer;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getX() {
    return x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getY() {
    return y;
  }
}
