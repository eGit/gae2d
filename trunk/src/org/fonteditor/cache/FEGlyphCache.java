package org.fonteditor.cache;

import org.fonteditor.font.FEFont;
import org.fonteditor.font.FEGlyph;
import org.fonteditor.graphics.FontImageProcessor;
import org.fonteditor.graphics.GreyByteArrayTranslated;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.options.coords.Coords;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.utilities.claim.Claim;

import com.jgraph.gaeawt.java.awt.Image;

/**
 * Cache of multiple glyphs for a specified font and set of display options...
 */

public class FEGlyphCache
{

	private CachedGlyph[] cache;

	private int size;

	public FEGlyphCache(FEFont fefont)
	{
		this.size = fefont.getMax();

		cache = new CachedGlyph[this.size];
	}

	CachedGlyph getCachedGlyph(char c, FEFont fefont, DisplayOptions gdo)
	{
		if (c >= size)
		{
			c = '*'; // problems? - if so, use "*".
		}

		if (c < 32)
		{
			c = '*'; // problems? - if so, use "*".
		}

		if (cache[c] == null)
		{
			makeGlyph(c, fefont, gdo);
		}

		return cache[c];
	}

	private void makeGlyph(char c, FEFont fefont, DisplayOptions gdo)
	{
		Coords coords = gdo.getCoords();

		int scale_factor_x = coords.getWidth() / coords.getAAWidth();
		int scale_factor_y = coords.getHeight() / coords.getAAHeight();

		FEGlyph feg = fefont.getGlyphArray().getGlyph(c);

		Claim.claim(feg != null, "FEG = null: [" + c + "] - (" + (int) c + ")");

		int x_max = feg.getMaxX(gdo);
		int y_max = feg.getMaxY(gdo);

		//feg.getFEPointList(gdo).dump();

		// Log.log("x_max:" + x_max + " y_max:" + y_max);

		if (gdo.getPen().getWidth() > 0)
		{
			x_max += gdo.getLineWidthOffsetEast()
					- gdo.getLineWidthOffsetWest();
			y_max += gdo.getLineWidthOffsetSouth()
					- gdo.getLineWidthOffsetNorth();
		}

		x_max = coords.scaleX(x_max);
		y_max = coords.scaleY(y_max);

		x_max = ((x_max + scale_factor_x) / scale_factor_x) * scale_factor_x; // + scale_factor_x;
		y_max = ((y_max + scale_factor_y) / scale_factor_y) * scale_factor_y; // + scale_factor_y;

		if (x_max < scale_factor_x)
		{
			x_max = scale_factor_x;
		}

		if (y_max < scale_factor_y)
		{
			y_max = scale_factor_y;
		}

		ImageWrapper tti = new ImageWrapper(x_max, y_max);

		Image i = tti.getImage();

		Claim.claim(i != null, "Severe component/image problems...");

		if (i != null)
		{
			feg.draw(i.getGraphics(), gdo);

			//gdo.getCoords().dump();

			//ImageWrapperTranslated image = FontImageProcessor.fontScale(tti, scale_factor_x, scale_factor_y);
			//cache[c] = new CachedGlyph(image.getImageWrapper(), image.getOffsetY());

			GreyByteArrayTranslated gbat = FontImageProcessor
					.fontScaleToByteArray(tti, scale_factor_x, scale_factor_y);
			cache[c] = new CachedGlyph(gbat.getGreyByteArray(),
					gbat.getOffsetY());

			// GreyByteArrayTranslated gbat = GraphicsConverter.convert(image);
			// ImageWrapperTranslated image2 = GraphicsConverter.convert(gbat);

			//Log.log("Rendering character:" + (int)c);
		}
	}

	void remove(char c)
	{
		cache[c] = null;
	}
}
