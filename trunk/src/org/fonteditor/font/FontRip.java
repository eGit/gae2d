package org.fonteditor.font;

import org.fonteditor.elements.paths.ExecutorOnFEPath;
import org.fonteditor.elements.paths.FEPath;
import org.fonteditor.elements.paths.FEPathList;
import org.fonteditor.instructions.InstructionConstants;
import org.fonteditor.instructions.InstructionStream;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.utilities.general.For;
import org.fonteditor.utilities.log.Log;

import com.jgraph.gaeawt.java.awt.Font;
import com.jgraph.gaeawt.java.awt.Graphics;
import com.jgraph.gaeawt.java.awt.Graphics2D;
import com.jgraph.gaeawt.java.awt.RenderingHints;
import com.jgraph.gaeawt.java.awt.Shape;
import com.jgraph.gaeawt.java.awt.font.FontRenderContext;
import com.jgraph.gaeawt.java.awt.font.GlyphVector;
import com.jgraph.gaeawt.java.awt.geom.PathIterator;

public class FontRip implements InstructionConstants
{
	private static int rip_factor_x = 200;

	private static int rip_factor_y = 200;

	private static Shape[] shape_array;

	private static boolean trace = false;

	public static FEFont rip(String font_name, Graphics g, int char_min,
			int char_max, boolean bold, boolean italic)
	{
		FEFont fefont = doRip(g, font_name, bold, italic, char_min, char_max);

		fefont.scaleRipped();
		return fefont;
	}

	public static FEFont doRip(Graphics g, String font_name, boolean bold,
			boolean italic, int min, int max)
	{
		FEFont fefont = new FEFont(font_name, min, max);
		GlyphArray ga = fefont.getGlyphArray();
		int font_style = (bold ? Font.BOLD : Font.PLAIN)
				| (italic ? Font.ITALIC : Font.PLAIN);

		for (int i = min; i < max; i++)
		{
			ga.add(rip(g, fefont, "" + (char) i, font_name, font_style), i);
		}

		return fefont;
	}

	private static FEGlyph rip(Graphics g, FEFont fefont, String s,
			String font_name, int font_style)
	{
		int number = (int) s.charAt(0); // (int)'@';

		if (trace)
		{
			Log.log("Ripping: " + s + " (" + number + ")");
		}

		shape_array = new Shape[256];

		// First get the shapes into an array...
		Graphics2D g2d = (Graphics2D) (g);

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		FontRenderContext frc = g2d.getFontRenderContext();
		//String font_name = RipperFontSelector.choose_font.choice.getSelectedItem();
		//Log.log(font_name);
		int size = 256;
		Font font = new Font(font_name, font_style, size);
		GlyphVector gv = font.createGlyphVector(frc, "" + (char) number);

		shape_array[number] = gv.getOutline();

		// Possibly process them - extracting statistics, etc...
		// Extract the curves from the shapes...
		FEGlyph glyph = new FEGlyph(fefont, number);

		addToInstructionStream(glyph.getInstructionStream(),
				shape_array[number]);
		glyph.getInstructionStream().add(END_GLYPH);
		glyph.resetRemakeFlag();
		reorderPaths(glyph);
		glyph.resetRemakeFlag();
		return glyph;
	}

	/**
	 * Change the order of paths so that black paths are at the bottom...
	 * Optimise - only do this if it is necessary...
	 */
	private static void reorderPaths(FEGlyph glyph)
	{
		DisplayOptions gdo = DisplayOptions.getGDOForScaling();

		glyph.makeGlyphIfNeeded(gdo);

		// make instructionstream from path list...
		final InstructionStream is_in = glyph.getInstructionStream();
		final InstructionStream is_out = new InstructionStream();
		FEPathList fepathlist = glyph.getInstructionStream().getFEPathList();

		addCertainPaths(is_in, is_out, fepathlist, false);
		addCertainPaths(is_in, is_out, fepathlist, true);
		is_out.add(END_GLYPH);
		glyph.setInstructionStream(is_out);
	}

	private static void addCertainPaths(final InstructionStream is_in,
			final InstructionStream is_out, FEPathList fepathlist,
			final boolean dir)
	{
		fepathlist.executeOnEachPath(new ExecutorOnFEPath()
		{
			public void execute(FEPath p, Object o)
			{
				For.get(o);
				if (p.isClockwise() == dir)
				{
					if (FontRip.isTracing())
					{
						Log.log("dir: " + dir);
					}
					is_in.add(p, is_out);
				}
			}
		}, null);
	}

	private static void addToInstructionStream(InstructionStream is, Shape s)
	{
		PathIterator pi = s.getPathIterator(null);
		float[] coords = new float[6];

		while (!pi.isDone())
		{
			addPoints(is, pi, coords);
			pi.next();
		}
	}

	private static void addPoints(InstructionStream is, PathIterator pi,
			float[] coords)
	{
		int type = pi.currentSegment(coords);

		switch (type)
		{

			case 0:
				if (trace)
				{
					Log.log("(MKIS):MOVE_TO(" + coords[0] + "," + coords[1]
							+ ")");
				}

				is.add(OPEN_PATH);
				break;

			case 1:
				if (trace)
				{
					Log.log("(MKIS):STRAIGHT_LINE(" + coords[0] + ","
							+ coords[1] + ")");
				}

				is.add(STRAIGHT_LINE);
				is.add(ripScaleX(coords[0]));
				is.add(ripScaleY(coords[1]));
				break;

			case 2:
				if (trace)
				{
					Log.log("(MKIS):QUADRATIC_BEZIER((" + coords[0] + ","
							+ coords[1] + "),(" + coords[2] + "," + coords[3]
							+ "))");
				}

				is.add(QUADRATIC_BEZIER);
				is.add(ripScaleX(coords[0]));
				is.add(ripScaleY(coords[1]));
				is.add(ripScaleX(coords[2]));
				is.add(ripScaleY(coords[3]));
				break;

			case 3:
				if (trace)
				{
					Log.log("(MKIS):CUBIC_BEZIER((" + coords[0] + ","
							+ coords[1] + "),(" + coords[2] + "," + coords[3]
							+ "),(" + coords[4] + "," + coords[5] + "))");
				}

				is.add(CUBIC_BEZIER);
				is.add(ripScaleX(coords[0]));
				is.add(ripScaleY(coords[1]));
				is.add(ripScaleX(coords[2]));
				is.add(ripScaleY(coords[3]));
				is.add(ripScaleY(coords[4]));
				is.add(ripScaleY(coords[5]));
				break;

			case 4:
				if (trace)
				{
					Log.log("(MKIS):CLOSE_PATH");
				}

				is.add(CLOSE_PATH);
				break;

			default:
				Log.log("*** UNUSUAL VALUE:" + type);
		}
	}

	private static int ripScaleX(float x)
	{
		return (int) (x * rip_factor_x);
	}

	private static int ripScaleY(float y)
	{
		return (int) (y * rip_factor_y);
	}

	static void setTrace(boolean trace)
	{
		FontRip.trace = trace;
	}

	public static boolean isTracing()
	{
		return trace;
	}
}
