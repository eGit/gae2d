package org.fonteditor.elements.curves;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.utilities.log.Log;

import com.jgraph.gaeawt.java.awt.Polygon;

public abstract class Curve {
  private FEPoint p1 = null;
  private FEPoint p2 = null; // ugly...
  private FEPoint p3 = null; // ugly...
  private FEPoint p4 = null;
  //private int instruction_pointer;

  public abstract void addPointsToPolygon(Polygon polygon);

  public abstract void addFinalPointsToPolygon(Polygon polygon);

  public abstract FEPoint returnStartPoint();

  public abstract FEPoint returnEndPoint();

  public abstract void simplyAddPoints(FEPointList fepl);

  public abstract FEPoint returnStartControlPoint();

  public abstract FEPoint returnEndControlPoint();

  public void dump() {
    Log.log("    Curve:" + getName() + " (" + p1.getX() + "," + p1.getY() + ")");
  }

  String getName() {
    return "ERROR - base class!";
  }

  public void setP1(FEPoint p1) {
    this.p1 = p1;
  }

  public FEPoint getP1() {
    return p1;
  }

  public void setP4(FEPoint p4) {
    this.p4 = p4;
  }

  public FEPoint getP4() {
    return p4;
  }

  void setP2(FEPoint p2) {
    this.p2 = p2;
  }

  FEPoint getP2() {
    return p2;
  }

  void setP3(FEPoint p3) {
    this.p3 = p3;
  }

  FEPoint getP3() {
    return p3;
  }
  
  public boolean isStraight() {
  return false;
  }

  //public void setInstructionPointer(int instruction_pointer) {
  // this.instruction_pointer = instruction_pointer;
  //}

  //public int getInstructionPointer() {
  //  return instruction_pointer;
  //}
}
