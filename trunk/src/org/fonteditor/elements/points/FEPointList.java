package org.fonteditor.elements.points;

import org.fonteditor.elements.paths.PathConstants;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.utilities.callback.CallBack;
import org.fonteditor.utilities.log.Log;

import com.jgraph.gaeawt.java.awt.Point;

public class FEPointList implements PathConstants
{
	private FEPoint[] new_array;

	private static final int INCREMENT = 16; // needs generic collections...

	private int number = 0;

	private int number_of_points = 0;

	private FEPoint[] points = new FEPoint[number_of_points];

	private void makeMore()
	{
		new_array = new FEPoint[number_of_points + INCREMENT];
		System.arraycopy(points, 0, new_array, 0, number_of_points);
		points = new_array;
		number_of_points += INCREMENT;
	}

	public FEPoint add(InstructionStream is)
	{
		if (number >= number_of_points)
		{
			makeMore();
		}
		points[number] = new FEPoint(is);
		return points[number++];
	}

	public FEPoint add(FEPoint p)
	{
		if (number >= number_of_points)
		{
			makeMore();
		}
		points[number] = p;
		return points[number++];
	}

	//...
	public int getMinX()
	{
		int min = Integer.MAX_VALUE;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];

			if (p.getX() <= min)
			{
				min = p.getX();
			}
		}
		return min;
	}

	public int getMaxX()
	{
		int max = 0;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];

			if (p.getX() >= max)
			{
				max = p.getX();
			}
		}

		return max;
	}

	public int getMinY()
	{
		int min = Integer.MAX_VALUE;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];

			if (p.getY() <= min)
			{
				min = p.getY();
			}
		}
		return min;
	}

	public int getMaxY()
	{
		int max = 0;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];

			if (p.getY() >= max)
			{
				max = p.getY();
			}
		}
		return max;
	}

	public void translate(int dx, int dy)
	{
		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];

			p.setX(p.getX() + dx);
			p.setY(p.getY() + dy);
		}
	}

	// min---centre---max
	public void rescaleRangeX(int min, int centre, int max, int new_centre)
	{
		if (new_centre < centre)
		{
			rescaleWithFixedLeft(min, centre, new_centre);
			rescaleWithFixedRight(max, centre, new_centre);
		}
		if (centre < new_centre)
		{
			rescaleWithFixedRight(max, centre, new_centre);
			rescaleWithFixedLeft(min, centre, new_centre);
		}
	}

	// min---centre---max
	public void rescaleRangeY(int min, int centre, int max, int new_centre)
	{
		if (new_centre < centre)
		{
			rescaleWithFixedTop(min, centre, new_centre);
			rescaleWithFixedBottom(max, centre, new_centre);
		}
		if (centre < new_centre)
		{
			rescaleWithFixedBottom(max, centre, new_centre);
			rescaleWithFixedTop(min, centre, new_centre);
		}
	}

	public void rescaleWithFixedLeft(int fixed, int o, int n)
	{
		int d1 = (o - fixed) >> 1;

		if (d1 == 0)
		{
			return;
		}

		int d2 = (n - fixed) >> 1;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];
			int x = p.getX();

			if ((x >= fixed) && (x <= o))
			{
				p.setX(fixed + ((x - fixed) * d2) / d1);
			}
		}
	}

	public void rescaleWithFixedRight(int fixed, int o, int n)
	{
		int d1 = (fixed - o) >> 1;

		if (d1 == 0)
		{
			return;
		}

		int d2 = (fixed - n) >> 1;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];
			int x = p.getX();

			if ((x >= o) && (x <= fixed))
			{
				p.setX(fixed - ((fixed - x) * d2) / d1);
			}
		}
	}

	public void rescaleWithFixedTop(int fixed, int o, int n)
	{
		int d1 = (o - fixed) >> 1;

		if (d1 == 0)
		{
			return;
		}
		int d2 = (n - fixed) >> 1;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];
			int y = p.getY();

			if ((y >= fixed) && (y <= o))
			{
				p.setY(fixed + ((y - fixed) * d2) / d1);
			}
		}
	}

	public void rescaleWithFixedBottom(int fixed, int o, int n)
	{
		int d1 = (fixed - o) >> 1;

		if (d1 == 0)
		{
			return;
		}
		int d2 = (fixed - n) >> 1;

		for (int i = 0; i < number; i++)
		{
			FEPoint p = points[i];
			int y = p.getY();

			if ((y >= o) && (y <= fixed))
			{
				p.setY(fixed - ((fixed - y) * d2) / d1);
			}
		}
	}

	private void rescaleWithFixedLeft(int fixed, int o, int n, int idx_min,
			int idx_max)
	{
		//idx_max = (idx_max + 1) % number;
		int d1 = (o - fixed) >> 1;

		if (d1 == 0)
		{
			return;
		}
		int d2 = (n - fixed) >> 1;

		for (int i = idx_min; i != idx_max; i = (i + 1) % number)
		{
			FEPoint p = points[i];

			if ((p.getX() >= fixed) && (p.getX() <= o))
			{
				p.setX(fixed + ((p.getX() - fixed) * d2) / d1);
			}
		}
	}

	private void rescaleWithFixedRight(int fixed, int o, int n, int idx_min,
			int idx_max)
	{
		//idx_max = (idx_max + 1) % number;
		int d1 = (fixed - o) >> 1;

		if (d1 == 0)
		{
			return;
		}
		int d2 = (fixed - n) >> 1;

		for (int i = idx_min; i != idx_max; i = (i + 1) % number)
		{
			FEPoint p = points[i];

			if ((p.getX() >= o) && (p.getX() <= fixed))
			{
				p.setX(fixed - ((fixed - p.getX()) * d2) / d1);
			}
		}
	}

	private void rescaleWithFixedTop(int fixed, int o, int n, int idx_min,
			int idx_max)
	{
		int d1 = (o - fixed) >> 1;

		if (d1 == 0)
		{
			return;
		}

		int d2 = (n - fixed) >> 1;

		for (int i = idx_min; i != idx_max; i = (i + 1) % number)
		{
			FEPoint p = points[i];

			if ((p.getY() >= fixed) && (p.getY() <= o))
			{
				p.setY(fixed + ((p.getY() - fixed) * d2) / d1);
			}
		}
	}

	private void rescaleWithFixedBottom(int fixed, int o, int n, int idx_min,
			int idx_max)
	{
		int d1 = (fixed - o) >> 1;

		if (d1 == 0)
		{
			return;
		}

		int d2 = (fixed - n) >> 1;

		for (int i = idx_min; i != idx_max; i = (i + 1) % number)
		{
			FEPoint p = points[i];

			if ((p.getY() >= o) && (p.getY() <= fixed))
			{
				p.setY(fixed - ((fixed - p.getY()) * d2) / d1);
			}
		}
	}

	public void scaleRangeX(int index_fixed, int index_moving, int new_pos,
			int index_min, int index_max)
	{
		int x1 = points[index_fixed].getX();
		int x2 = points[index_moving].getX();

		if (x1 > x2)
		{
			rescaleWithFixedRight(x1, x2, new_pos, index_min, index_max);
			//Log.log("rescaleWithFixedRight:");
		}
		if (x2 > x1)
		{
			rescaleWithFixedLeft(x1, x2, new_pos, index_min, index_max);
			//Log.log("rescaleWithFixedLeft:");
		}
	}

	public void scaleRangeY(int index_fixed, int index_moving, int new_pos,
			int index_min, int index_max)
	{
		int y1 = points[index_fixed].getY();
		int y2 = points[index_moving].getY();

		if (y1 > y2)
		{
			rescaleWithFixedBottom(y1, y2, new_pos, index_min, index_max);
			//Log.log("rescaleWithFixedBottom:");
		}
		if (y2 > y1)
		{
			rescaleWithFixedTop(y1, y2, new_pos, index_min, index_max);
			//Log.log("rescaleWithFixedTop:");
		}
	}

	public void executeOnEachPoint(CallBack e)
	{
		for (int i = 0; i < number; i++)
		{
			e.callback(points[i]);
		}
	}

	public boolean contains(FEPoint p)
	{
		return indexOf(p) >= 0;
	}

	public int indexOf(FEPoint p)
	{
		for (int i = 0; i < number; i++)
		{
			if (p.equals(points[i]))
			{
				return i;
			}
		}

		return -1;
	}

	public int getIndexOf(FEPoint fepoint)
	{
		for (int i = 0; i < number; i++)
		{
			if (fepoint == points[i])
			{
				//       if (fepoint.equals(points[i])) {
				return i;
			}
		}

		return -1;
	}

	public void adjustInstructionsFromPoints(InstructionStream is)
	{
		for (int i = 0; i < number; i++)
		{
			FEPoint fepoint = points[i];
			int ip = fepoint.getInstructionPointer();

			//if (ip != 0) {
			//  Log.log("ip:" + ip);
			//}
			//Log.log("from " + is.getInstructionAt(ip) + " to " + fepoint.x);
			//is.sgetInstructionAt(ip++, fepoint.y);
			is.setInstructionAt(ip++, fepoint.getX() >> 2);
			is.setInstructionAt(ip++, fepoint.getY() >> 2);
			//is.setInstructionAt(ip++, 0);
			//is.setInstructionAt(ip++, 0);
		}
	}

	public boolean hasX(int x)
	{
		for (int i = 0; i < number; i++)
		{
			FEPoint fepoint = points[i];

			if (fepoint.getX() == x)
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasY(int y)
	{
		for (int i = 0; i < number; i++)
		{
			FEPoint fepoint = points[i];

			if (fepoint.getY() == y)
			{
				return true;
			}
		}
		return false;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public int getNumber()
	{
		return number;
	}

	public void setPoints(FEPoint[] points)
	{
		this.points = points;
	}

	public FEPoint getPoint(int i)
	{
		return points[i];
	}

	public FEPoint safelyGetPoint(int i)
	{
		while (i < 0)
		{
			i += number;
		}
		while (i >= number)
		{
			i -= number;
		}

		return points[i];
	}

	public void dump()
	{
		Log.log("PointList dump - Number:" + number);
	}
}

/*
void clearSelection() {
   for (int i = 0; i < number; i++) {
      FEPoint p = points[i];
     
      p.selected = false;
   }
  
   number_of_points_selected = 0;
}
*/

/*
  public void select(int i) {
    if (!points[i].areAnySelected()) {
      points[i].setSelected(true);
      number_of_points_selected++;
    }
  }

  void deselect(int i) {
    if (points[i].areAnySelected()) {
      points[i].setSelected(false);
      number_of_points_selected--;
    }
  }
*/

/*
  public void deselect() {
    for (int i = 0; i < number; i++) {
      deselect(i);
    }
  }
*/

/*
  public void countSelectedPoints() {
    number_of_points_selected = 0;
    for (int i = 0; i < number; i++) {
      if (points[i].areAnySelected()) {
        number_of_points_selected++;
      }
    }
  }
*/

//  private void setNumberOfPointsSelected(int number_of_points_selected) {
//    this.number_of_points_selected = number_of_points_selected;
//  }
//
//  public int getNumberOfPointsSelected() {
//    return number_of_points_selected;
//  }

/*
  public void rescaleWithFixedLeft(SliderManagerBase s_m, int fixed, int o, int n) {
    rescaleWithFixedLeft(fixed, o, n);
    s_m.rescaleWithFixedLeftOrTop(fixed, o, n);
  }

  public void rescaleWithFixedTop(SliderManagerBase s_m, int fixed, int o, int n) {
    rescaleWithFixedTop(fixed, o, n);
    s_m.rescaleWithFixedLeftOrTop(fixed, o, n);
  }
*/

//  private void reset() {
//    number_of_points = 0;
//    number_of_points_selected = 0;
//  }
//  public int numberSelected() {
//    return number_of_points_selected;
//  }

//  //...
//  int getIndexOfWestmost() {
//    int min = Integer.MAX_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//
//      if (p.x <= min) {
//        min = p.x;
//        index = i;
//      }
//    }
//
//    return index;
//  }
//
//  int getIndexOfEastmost() {
//    int max = Integer.MIN_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//
//      if (p.x >= max) {
//        max = p.x;
//        index = i;
//      }
//    }
//
//    return index;
//  }
//
//  int getIndexOfNorthmost() {
//    int min = Integer.MAX_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//
//      if (p.y <= min) {
//        min = p.y;
//        index = i;
//      }
//    }
//
//    return index;
//  }
//
//  int getIndexOfSouthmost() {
//    int max = Integer.MIN_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//
//      if (p.y >= max) {
//        max = p.y;
//        index = i;
//      }
//    }
//
//    return index;
//  }
//
//  int getIndexOfNorthWestmost() {
//    int max = Integer.MIN_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//      int val = 0 - p.y - p.x;
//
//      if (val >= max) {
//        max = val;
//        index = i;
//      }
//    }
//
//    return index;
//  }
//
//  int getIndexOfNorthEastmost() {
//    int max = Integer.MIN_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//      int val = p.x - p.y;
//
//      if (val >= max) {
//        max = val;
//        index = i;
//      }
//    }
//
//    return index;
//  }
//
//  int getIndexOfSouthWestmost() {
//    int max = Integer.MIN_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//      int val = p.y - p.x;
//
//      if (val >= max) {
//        max = val;
//        index = i;
//      }
//    }
//
//    return index;
//  }
//
//  int getIndexOfSouthEastmost() {
//    int max = Integer.MIN_VALUE;
//    int index = 0;
//
//    for (int i = 0; i < number; i++) {
//      FEPoint p = points[i];
//      int val = p.y + p.x;
//
//      if (val >= max) {
//        max = val;
//        index = i;
//      }
//    }
//
//    return index;
//  }

//  // min---centre---max
//  void rescaleRangeX(int min, int centre, int max, int new_centre, int idx_min, int idx_max) {
//    if (new_centre < centre) {
//      rescaleWithFixedLeft(min, centre, new_centre, idx_min, idx_max);
//      rescaleWithFixedRight(max, centre, new_centre, idx_min, idx_max);
//    }
//
//    if (centre < new_centre) {
//      rescaleWithFixedRight(max, centre, new_centre, idx_min, idx_max);
//      rescaleWithFixedLeft(min, centre, new_centre, idx_min, idx_max);
//    }
//  }
//
//  // min---centre---max
//  void rescaleRangeY(int min, int centre, int max, int new_centre, int idx_min, int idx_max) {
//    if (new_centre < centre) {
//      rescaleWithFixedTop(min, centre, new_centre, idx_min, idx_max);
//      rescaleWithFixedBottom(max, centre, new_centre, idx_min, idx_max);
//    }
//
//    if (centre < new_centre) {
//      rescaleWithFixedBottom(max, centre, new_centre, idx_min, idx_max);
//      rescaleWithFixedTop(min, centre, new_centre, idx_min, idx_max);
//    }
//  }
