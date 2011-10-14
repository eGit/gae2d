package org.fonteditor;

import org.fonteditor.cache.CachedGlyph;
import org.fonteditor.cache.FEFontCache;
import org.fonteditor.cache.FEGlyphCache;
import org.fonteditor.font.FEFont;
import org.fonteditor.font.FEFontMetrics;
import org.fonteditor.graphics.ImageWrapper;
import org.fonteditor.kerning.Kerning;
import org.fonteditor.kerning.KerningPreprocessor;
import org.fonteditor.kerning.KerningType;
import org.fonteditor.kerning.KerningTypes;
import org.fonteditor.options.coords.Coords;
import org.fonteditor.options.display.DisplayOptions;
import org.fonteditor.utilities.log.Log;

import com.jgraph.gaeawt.java.awt.FontMetrics;
import com.jgraph.gaeawt.java.awt.Graphics;

/**
 * Main class allowing public access to the renderer...
 * Handles a single FontCache...
 * Is responsible for obtaining a reference to a visible Component - so image creation can work...
 */

// Currently needs a visible component instance - so it can create Image objects...
// Totally pointless - and indeed counter-productive - under Java 2 of course...
// ...but *essential* under Java 1.1 :-(

public class FEFontRenderer
{
	private FEFontCache fefontcache = new FEFontCache(); // could be global...

	private KerningType default_kerning_type = KerningTypes.SEMI_PROPORTIONAL;

	private static final char CHARACTER_BAR = '|';

	public FEFontRenderer()
	{
	}

	// Optimise? - cache common kerning distances for speed...
	// ...using a "map" based on the "key" of c1 x c2...?
	public int getKerningDistance(char c1, FEFont font1, DisplayOptions gdo1,
			char c2, FEFont font2, DisplayOptions gdo2, KerningType kerning_type)
	{

		//final Coords coords = gdo1.getCoords();
		//int spacing = 2;
		//coords.scaleY(font1.getHintingCues().getBottomLetterO() >> 3);

		c1 = KerningPreprocessor.process(c1);
		c2 = KerningPreprocessor.process(c2);

		CachedGlyph cached_glyph1 = fefontcache.getCachedGlyph(font1, gdo1, c1);
		CachedGlyph cached_glyph2 = fefontcache.getCachedGlyph(font2, gdo2, c2);

		return kerning_type.getKerningDistance(cached_glyph1, cached_glyph2);
	}

	public void remove(FEFont font)
	{
		fefontcache.remove(font);
	}

	public FEFontCache getFEFontCache()
	{
		return fefontcache;
	}

	public CachedGlyph getCachedGlyph(char c, FEFont font, DisplayOptions gdo)
	{
		return fefontcache.getCachedGlyph(font, gdo, c);
	}

	public int howManyCharactersFit(FEFont font,
			DisplayOptions display_options, int start, char[] chars, int width,
			KerningType kerning_type)
	{
		int number = start;
		int chars_length = chars.length;

		char bar = '|';
		char previous = bar;

		int cwidth = 0;
		while (cwidth < width)
		{
			//for (int i = 0; i < length; i++) {
			char ch = chars[number];
			int kd = getKerningDistance(previous, font, display_options, ch,
					font, display_options, kerning_type);
			cwidth += kd
					+ Kerning.getKerningGap(display_options.getCoords()
							.getAAHeight());

			previous = ch;

			if (++number >= chars_length)
			{
				return number;
			}
		}

		return number - 1;
	}

	public FontMetrics getFontMetrics(FEFont font,
			DisplayOptions display_options)
	{
		return new FEFontMetrics(font, this, display_options,
				default_kerning_type);
	}

	/**
	 * Given a string and the length of a prefix, return the index of the last
	 * space...
	 */
	public static int lengthBeforeLastSpace(String text, int n)
	{
		int i = n - 1;

		if (i < 0)
		{
			return n;
		}

		while (text.charAt(i) != ' ')
		{
			if (--i < 0)
			{
				return n;
			}
		}

		return i;
	}

	public void drawString(Graphics graphics, FEFont font, DisplayOptions gdo,
			String string, int x, int y)
	{
		drawChars(graphics, font, gdo, string.toCharArray(), 0,
				string.length(), x, y);
	}

	public void drawChars(Graphics graphics, FEFont font, DisplayOptions gdo,
			char[] chars, int start, int len, int x, int y)
	{
		if (len <= 0)
		{
			return;
		}

		KerningType kerning_type = default_kerning_type;

		Coords coords = gdo.getCoords();

		char last_c = CHARACTER_BAR;
		x -= getKerningDistance(last_c, font, gdo, last_c, font, gdo,
				kerning_type);
		x -= Kerning.getKerningGap(coords.getAAHeight());

		for (int i = start; i < start + len; i++)
		{
			char c = chars[i];

			if (c > 126)
			{
				Log.log("C:" + (int) c); /// doesn't fire...
			}

			int kd = getKerningDistance(last_c, font, gdo, c, font, gdo,
					kerning_type);

			x += kd + Kerning.getKerningGap(coords.getAAHeight());

			last_c = c;

			CachedGlyph cached_glyph = getCachedGlyph(c, font, gdo);
			ImageWrapper i_w = cached_glyph.getImageWrapper();

			int offset_y = cached_glyph.getOffsetY();

			graphics.drawImage(i_w.getImage(), x, y + offset_y, null);
		}
	}
}

//  public init(Component component) {
//    FEGlyphCache.setComponent(component);
//    initialised = true;
//  }

// Optimise: this is currently an *unnecessarily* expensive operation...

//  public int howManyCharactersFit(FEFont font, DisplayOptions display_options, String text, int width) {
//    int number = 1;
//    int size = 0;
//    char[] data = text.toCharArray();
//    int data_length = data.length;
//
//    do {
//      if (number >= data_length) {
//        return data_length;
//      }
//
//      FontMetrics f_m = getFontMetrics(font, display_options);
//      size = f_m.charsWidth(data, 0, number);
//      number++;
//    } while (size < width);
//
//    return number - 2;
//  }
