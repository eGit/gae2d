package org.fonteditor.sliders;

import org.fonteditor.elements.paths.ExecutorOnFEPath;
import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.points.FEPoint;
import org.fonteditor.elements.points.FEPointList;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.graphics.WideLine;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.options.coords.Coords;
import org.fonteditor.utilities.general.For;
import org.fonteditor.utilities.general.Utils;

import com.jgraph.gaeawt.java.awt.Color;
import com.jgraph.gaeawt.java.awt.Graphics;

/**
 * Represents a pair of vertical and horizontal SliderManagers...
 * ...this class contains many of the methods for manipulating them.
 */

public class Sliders implements SliderConstants
{
	private SliderManager slider_manager_vertical;

	private SliderManager slider_manager_horizontal;

	public Sliders(FEPath p)
	{
		resetSliders();
		setUpSliders(p);
	}

	public Sliders(FEGlyph glyph)
	{
		resetSliders();
		setUpSliders(glyph.getInstructionStream());
	}

	void resetSliders()
	{
		slider_manager_vertical = new SliderManager();
		slider_manager_horizontal = new SliderManager();
	}

	void setUpSliders(InstructionStream instruction_stream)
	{
		// On each path...
		instruction_stream.getFEPathList().executeOnEachPath(
				new ExecutorOnFEPath()
				{
					public void execute(FEPath p, Object o)
					{
						For.get(o);
						setUpSlidersInternal(p);
					}
				}, null);

		sortAndDedupeSliders();
	}

	void setUpSliders(FEPath p)
	{
		setUpSlidersInternal(p);
		sortAndDedupeSliders();
	}

	void setUpSlidersInternal(FEPath p)
	{
		// On each path...
		// Go through all the points
		// boolean direction = p.getDirection();// set back to null at the bottom if you use this line...
		FEPointList fepl = p.getFEPointList();

		if (fepl.getNumber() > 1)
		{
			FEPoint current_point;
			FEPoint next_point;
			//      FEPoint[] array = fepl.getPoints();
			int increasing;

			increasing = (fepl.getPoint(fepl.getNumber() - 2).getX() < fepl
					.getPoint(fepl.getNumber() - 1).getX()) ? 1 : -1;
			current_point = fepl.getPoint(fepl.getNumber() - 1);
			for (int i = 0; i < fepl.getNumber(); i++)
			{
				int cpx = current_point.getX();

				next_point = fepl.getPoint(i);
				if (Utils.equalsApprox(current_point.getX(), next_point.getX(),
						TOLERANCE))
				{
					boolean dir = current_point.getY() > next_point.getY();

					slider_manager_vertical.add(new Slider(VERTICAL,
							Slider.PARALLEL, dir, Math.abs(next_point.getY()
									- current_point.getY()), current_point));
				}
				else
				{
					if (increasing > 0)
					{
						if (cpx > next_point.getX())
						{
							slider_manager_vertical.add(new Slider(VERTICAL,
									Slider.INFLECTION, true, Math
											.abs(next_point.getY()
													- current_point.getY()),
									current_point));
						}
						// else {
						//  }
					}

					if (increasing < 0)
					{
						if (cpx < next_point.getX())
						{
							slider_manager_vertical.add(new Slider(VERTICAL,
									Slider.INFLECTION, true, Math
											.abs(next_point.getY()
													- current_point.getY()),
									current_point));
						}
						// else {
						//  }
					}

					if (cpx > next_point.getX())
					{
						increasing = -1;
					}

					if (cpx < next_point.getX())
					{
						increasing = 1;
					}
				}
				current_point = next_point;
			}

			increasing = (fepl.getPoint(fepl.getNumber() - 2).getY() < fepl
					.getPoint(fepl.getNumber() - 1).getY()) ? 1 : -1;
			current_point = fepl.getPoint(fepl.getNumber() - 1);

			for (int i = 0; i < fepl.getNumber(); i++)
			{
				int cpy = current_point.getY();

				next_point = fepl.getPoint(i);
				if (Utils.equalsApprox(current_point.getY(), next_point.getY(),
						TOLERANCE))
				{
					boolean dir = current_point.getX() > next_point.getX();

					slider_manager_horizontal.add(new Slider(HORIZONTAL,
							Slider.PARALLEL, dir, Math.abs(next_point.getX()
									- current_point.getX()), current_point));
				}
				else
				{
					if (increasing > 0)
					{
						if (cpy > next_point.getY())
						{
							slider_manager_horizontal.add(new Slider(
									HORIZONTAL, Slider.INFLECTION, true, Math
											.abs(next_point.getX()
													- current_point.getX()),
									current_point));
						}
						// else {
						//  }
					}

					if (increasing < 0)
					{
						if (cpy < next_point.getY())
						{
							slider_manager_horizontal.add(new Slider(
									HORIZONTAL, Slider.INFLECTION, true, Math
											.abs(next_point.getX()
													- current_point.getX()),
									current_point));
						}
						// else {
						//  }
					}

					if (cpy > next_point.getY())
					{
						increasing = -1;
					}

					if (cpy < next_point.getY())
					{
						increasing = 1;
					}
				}
				current_point = next_point;
			}

			// identified points of inflection... ;-)
		}
	}

	void sortAndDedupeSliders()
	{
		// sort...
		slider_manager_vertical.sort();
		slider_manager_horizontal.sort();
		// dedupe...
		slider_manager_vertical.dedupe();
		slider_manager_horizontal.dedupe();
	}

	// draw sliders...
	public void drawHorizontalSliders(Graphics g, Coords c)
	{
		if (slider_manager_horizontal != null)
		{
			for (int i = 0; i < slider_manager_horizontal.getNumber(); i++)
			{
				Slider slider = slider_manager_horizontal.getSlider(i);
				boolean direction = slider.isHomewards();
				int type = slider.getType();
				boolean parallel = (type == Slider.PARALLEL);

				g.setColor(parallel ? Color.red : Color.blue);
				WideLine.renderRound(g, new FEPoint(0, slider.getPosition()),
						new FEPoint(0xFFFF, slider.getPosition()), 0x50, c);
				if (parallel)
				{
					if (direction)
					{
						WideLine.renderRound(g, new FEPoint(0x7FFF - HEAD_SIZE,
								slider.getPosition() - HEAD_SIZE), new FEPoint(
								0x7FFF, slider.getPosition()), 0x50, c);
						WideLine.renderRound(g, new FEPoint(0x7FFF - HEAD_SIZE,
								slider.getPosition() + HEAD_SIZE), new FEPoint(
								0x7FFF, slider.getPosition()), 0x50, c);
					}
					else
					{
						WideLine.renderRound(g,
								new FEPoint(HEAD_SIZE, slider.getPosition()
										- HEAD_SIZE),
								new FEPoint(0, slider.getPosition()), 0x50, c);
						WideLine.renderRound(g,
								new FEPoint(HEAD_SIZE, slider.getPosition()
										+ HEAD_SIZE),
								new FEPoint(0, slider.getPosition()), 0x50, c);
					}
				}
			}
		}
	}

	public void drawVerticalSliders(Graphics g, Coords c)
	{
		if (slider_manager_vertical != null)
		{
			for (int i = 0; i < slider_manager_vertical.getNumber(); i++)
			{
				Slider slider = slider_manager_vertical.getSlider(i);
				boolean direction = slider.isHomewards();
				int type = slider.getType();
				boolean parallel = (type == Slider.PARALLEL);

				g.setColor(parallel ? Color.red : Color.blue);
				WideLine.renderRound(g, new FEPoint(slider.getPosition(), 0),
						new FEPoint(slider.getPosition(), 0xFFFF), 0x50, c);
				if (parallel)
				{
					if (direction)
					{
						WideLine.renderRound(g,
								new FEPoint(slider.getPosition() - HEAD_SIZE,
										0xFFFF - HEAD_SIZE),
								new FEPoint(slider.getPosition(), 0xFFFF),
								0x50, c);
						WideLine.renderRound(g,
								new FEPoint(slider.getPosition() + HEAD_SIZE,
										0xFFFF - HEAD_SIZE),
								new FEPoint(slider.getPosition(), 0xFFFF),
								0x50, c);
					}
					else
					{
						WideLine.renderRound(g,
								new FEPoint(slider.getPosition() - HEAD_SIZE,
										HEAD_SIZE),
								new FEPoint(slider.getPosition(), 0), 0x50, c);
						WideLine.renderRound(g,
								new FEPoint(slider.getPosition() + HEAD_SIZE,
										HEAD_SIZE),
								new FEPoint(slider.getPosition(), 0), 0x50, c);
					}
				}
			}
		}
	}

	public SliderManager getSliderManagerVertical()
	{
		return slider_manager_vertical;
	}

	public SliderManager getSliderManagerHorizontal()
	{
		return slider_manager_horizontal;
	}
}

/*
  public void translate(int dx, int dy) {
    slider_manager_vertical.translate(dx);
    slider_manager_horizontal.translate(dy);
  }

  public void rescaleRangeX(int min, int centre, int max, int new_centre) {
    slider_manager_vertical.rescaleRange(min, centre, max, new_centre);
  }

  public void rescaleRangeY(int min, int centre, int max, int new_centre) {
    slider_manager_horizontal.rescaleRange(min, centre, max, new_centre);
  }

  public void rescaleWithFixedLeft(int fixed, int o, int n) {
    slider_manager_vertical.rescaleWithFixedLeftOrTop(fixed, o, n);
  }

  public void rescaleWithFixedRight(int fixed, int o, int n) {
    slider_manager_vertical.rescaleWithFixedRightOrBottom(fixed, o, n);
  }

  public void rescaleWithFixedTop(int fixed, int o, int n) {
    slider_manager_horizontal.rescaleWithFixedLeftOrTop(fixed, o, n);
  }

  public void rescaleWithFixedBottom(int fixed, int o, int n) {
    slider_manager_horizontal.rescaleWithFixedRightOrBottom(fixed, o, n);
  }
  */

//  public void setSliderManagerVertical(SliderManagerBase slider_manager_vertical) {
//    this.slider_manager_vertical = slider_manager_vertical;
//  }

//  public void setSliderManagerHorizontal(SliderManagerBase slider_manager_horizontal) {
//    this.slider_manager_horizontal = slider_manager_horizontal;
//  }
