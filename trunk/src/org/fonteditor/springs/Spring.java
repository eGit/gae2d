package org.fonteditor.springs;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.graphics.WideLine;
import org.fonteditor.options.display.DisplayOptions;

import com.jgraph.gaeawt.java.awt.Color;
import com.jgraph.gaeawt.java.awt.Graphics;

/**
 * Springs are used in hinting...
 * ...or at least they will be...
 */

public class Spring
{
	private FEPoint fep1;

	private FEPoint fep2;

	public Spring(FEPoint fep1, FEPoint fep2)
	{
		this.fep1 = fep1;
		this.fep2 = fep2;
	}

	void draw(Graphics g, DisplayOptions gdo)
	{
		g.setColor(Color.red);
		WideLine.renderRound(g, fep1, fep2, 300, gdo.getCoords());
	}

	public boolean equals(Object o)
	{
		Spring s = (Spring) o;

		return (s.fep1.equals(fep1) && s.fep2.equals(fep2))
				|| (s.fep1.equals(fep2) && s.fep2.equals(fep1));
	}

	public int hashCode()
	{
		return super.hashCode();
	}
}
