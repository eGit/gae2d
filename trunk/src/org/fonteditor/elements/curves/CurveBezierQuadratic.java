package org.fonteditor.elements.curves;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;

import com.jgraph.gaeawt.java.awt.Polygon;

public class CurveBezierQuadratic extends Curve implements CurveConstants
{
	public CurveBezierQuadratic(FEPoint p1, FEPoint p2, FEPoint p4)
	{
		this.setP1(p1);
		this.setP2(p2);
		this.setP4(p4);
	}

	public void simplyAddPoints(FEPointList fepl)
	{
		fepl.add(getP2());
		fepl.add(getP4());
	}

	public void addPointsToPolygon(Polygon polygon)
	{
		int x0;
		int x1;
		int x3;
		int y0;
		int y1;
		int y3;
		int x_b;
		int x_c;
		int x_d;
		int y_b;
		int y_c;
		int y_d;
		int steps = STEPS_IN_QUADRATIC_BEZIER;

		x0 = getP1().getX();
		y0 = getP1().getY();
		x1 = getP2().getX();
		y1 = getP2().getY();
		x3 = getP4().getX();
		y3 = getP4().getY();

		x_b = 1 * x0 - 2 * x1 + 1 * x3;
		x_c = -2 * x0 + 2 * x1;
		x_d = x0;
		y_b = 1 * y0 - 2 * y1 + 1 * y3;
		y_c = -2 * y0 + 2 * y1;
		y_d = y0;
		polygon.addPoint(x0, y0);
		int step_size = 4096 / steps;

		for (int t = step_size; t < 4096; t += step_size)
		{
			polygon.addPoint((((((x_b * t) >> 12) + x_c) * t) >> 12) + x_d,
					(((((y_b * t) >> 12) + y_c) * t) >> 12) + y_d);
		}
	}

	public void addFinalPointsToPolygon(Polygon polygon)
	{
		addPointsToPolygon(polygon);
	}

	// should really return copies...
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
		return "QuadraticBezier";
	}
}
