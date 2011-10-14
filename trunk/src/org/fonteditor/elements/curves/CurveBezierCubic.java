package org.fonteditor.elements.curves;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;

import com.jgraph.gaeawt.java.awt.Polygon;

public class CurveBezierCubic extends Curve implements CurveConstants
{
	public CurveBezierCubic(FEPoint p1, FEPoint p2, FEPoint p3, FEPoint p4)
	{
		this.setP1(p1);
		this.setP2(p2);
		this.setP3(p3);
		this.setP4(p4);
	}

	public void simplyAddPoints(FEPointList fepl)
	{
		fepl.add(getP2());
		fepl.add(getP3());
		fepl.add(getP4());
	}

	public void addPointsToPolygon(Polygon polygon)
	{
		int x0;
		int x1;
		int x2;
		int x3;
		int y0;
		int y1;
		int y2;
		int y3;
		int x_a;
		int x_b;
		int x_c;
		int x_d;
		int y_a;
		int y_b;
		int y_c;
		int y_d;
		int steps = STEPS_IN_CUBIC_BEZIER;

		/*
		x0 = Coords.scaleX(p1.x);
		y0 = Coords.scaleY(p1.y);
		    
		x1 = Coords.scaleX(p2.x);
		y1 = Coords.scaleY(p2.y);
		    
		x2 = Coords.scaleX(p3.x);
		y2 = Coords.scaleY(p3.y);
		    
		x3 = Coords.scaleX(p4.x);
		y3 = Coords.scaleY(p4.y);
		*/

		x0 = getP1().getX();
		y0 = getP1().getY();
		x1 = getP2().getX();
		y1 = getP2().getY();
		x2 = getP3().getX();
		y2 = getP3().getY();
		x3 = getP4().getX();
		y3 = getP4().getY();
		x_a = -x0 + 3 * x1 - 3 * x2 + x3;
		x_b = 3 * x0 - 6 * x1 + 3 * x2;
		x_c = -3 * x0 + 3 * x1;
		x_d = x0;
		y_a = -y0 + 3 * y1 - 3 * y2 + y3;
		y_b = 3 * y0 - 6 * y1 + 3 * y2;
		y_c = -3 * y0 + 3 * y1;
		y_d = y0;
		polygon.addPoint(x0, y0);
		int step_size = 4096 / steps;

		for (int t = step_size; t < 4096; t += step_size)
		{
			polygon.addPoint(
					((((((((x_a * t) >> 12) + x_b) * t) >> 12) + x_c) * t) >> 12)
							+ x_d,
					((((((((y_a * t) >> 12) + y_b) * t) >> 12) + y_c) * t) >> 12)
							+ y_d);
		}
	}

	public void addFinalPointsToPolygon(Polygon polygon)
	{
		addPointsToPolygon(polygon);
	}

	// should *really* return copies...?
	public FEPoint returnStartPoint()
	{
		return getP1();
	}

	public FEPoint returnStartControlPoint()
	{
		return getP2();
	}

	public FEPoint returnEndControlPoint()
	{
		return getP3();
	}

	public FEPoint returnEndPoint()
	{
		return getP4();
	}

	String getName()
	{
		return "CubicBezier";
	}
}
