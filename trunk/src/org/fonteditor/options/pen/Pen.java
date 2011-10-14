package org.fonteditor.options.pen;

import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.options.coords.Coords;

import com.jgraph.gaeawt.java.awt.Graphics;

public abstract class Pen
{
	private int width;

	public abstract void drawStroke(Graphics g, FEPoint from, FEPoint to,
			Coords c);

	public abstract void quickDrawStroke(Graphics g, FEPoint from, FEPoint to,
			Coords c);

	public int getTop()
	{
		return width;
	}

	public int getBottom()
	{
		return width;
	}

	public int getLeft()
	{
		return width;
	}

	public int getRight()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getWidth()
	{
		return width;
	}

	boolean isZeroWidth()
	{
		if (getTop() != 0)
		{
			return false;
		}

		if (getBottom() != 0)
		{
			return false;
		}

		if (getLeft() != 0)
		{
			return false;
		}

		if (getRight() != 0)
		{
			return false;
		}

		return true;
	}

	public int hashCode()
	{
		int hash_code = (width << 3) - (width << 9) ^ 0x19975468;

		return hash_code;
	}
}
