package org.fonteditor.elements.curves;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;

import com.jgraph.gaeawt.java.awt.Polygon;

public class CurveStraightLine extends Curve {
  public CurveStraightLine(FEPoint p1, FEPoint p4) {
    this.setP1(p1);
    this.setP4(p4); //?
  }

  public void simplyAddPoints(FEPointList fepl) {
    fepl.add(getP4()); //?
  }

  public void addPointsToPolygon(Polygon polygon) {
    polygon.addPoint(getP1().getX(), getP1().getY());
  }

  public void addFinalPointsToPolygon(Polygon polygon) {
    addPointsToPolygon(polygon);
  }

  public FEPoint returnStartPoint() {
    return getP1();
  }

  public FEPoint returnStartControlPoint() {
    return getP2();
  }

  public FEPoint returnEndControlPoint() {
    return getP3();
  }

  public FEPoint returnEndPoint() {
    return getP4();
  }

  public boolean isStraight() {
  return true;
  }

  String getName() {
    return "StraightLine";
  }
}