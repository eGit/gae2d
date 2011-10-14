package org.fonteditor.graphics;

import org.fonteditor.utilities.general.For;
import org.fonteditor.utilities.log.Log;

import com.jgraph.gaeawt.java.awt.Graphics;
import com.jgraph.gaeawt.java.awt.Image;
import com.jgraph.gaeawt.java.awt.image.BufferedImage;
import com.jgraph.gaeawt.java.awt.image.PixelGrabber;

// Don't try writing to JPEGs.  It will have no effect...

/**
 * This is a thin wrapper over a Java image.
 * The image is nurmally backed by a pixel array -
 * for use with MemoryImageSource.
 */

public class ImageWrapper implements Cloneable
{
	private BufferedImage image;

	private int[] source;

	private int width;

	private int height;

	private boolean get_fresh = false;

	public Object clone()
	{
		int[] source_old = getArray();
		int[] source_new = new int[getArray().length];

		System.arraycopy(source_old, 0, source_new, 0, source_old.length);
		return new ImageWrapper(source_new, getWidth(), getHeight());
	}

	/** Constructor from existing image...
	 */
	public ImageWrapper(BufferedImage image)
	{
		setImage(image);
	}

	public ImageWrapper()
	{
		image = null;
		width = -1;
		height = -1;
		source = null;
	}

	public ImageWrapper(int[] a, int w, int h, boolean x)
	{
		For.get(x);
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		image.getRaster().setDataBuffer(a);
	}

	public ImageWrapper(int w, int h)
	{
		setImage(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
	}

	//  public ImageWrapper(int w, int h) {
	//    setImage(toolkit.createImage(w, h));
	//  }

	public ImageWrapper(int[] a, int w, int h)
	{
		createImageFromArray(a, w, h);
	}

	private void createImageFromArray(int[] a, int w, int h)
	{
		source = a;
		width = w;
		height = h;
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		image.getRaster().setDataBuffer(a);
	}

	public void setWidthAndHeight()
	{
		width = image.getWidth(null);
		height = image.getHeight(null);
	}

	// used for JPEGs...
	public void setImage(BufferedImage image)
	{
		this.image = image;

		if (image != null)
		{
			setWidthAndHeight();
		}

		source = null;
		get_fresh = true;
	}

	public Image getImage()
	{
		return image;
	}

	public Graphics getGraphics()
	{
		return image.getGraphics();
	}

	public void freshImage()
	{
		get_fresh = true;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int[] getArray()
	{
		if ((source == null) || (source.length < 1))
		{
			width = getWidth();
			height = getHeight();
			source = new int[width * height];
			//debug("Making new image:" + (width * height));
		}

		if (get_fresh)
		{
			get_fresh = false;
			PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height,
					source, 0, width);

			try
			{
				pg.grabPixels();
			}
			catch (InterruptedException e)
			{
				Log.log(e.toString());
			}
		}

		return source;
	}

	public int getPoint(int x, int y)
	{
		return getArray()[x + y * width];
	}
}

//  /**
//   *  Copy constructor :-(
//   */
//  public ImageWrapper(ImageWrapper tti) {
//    width = tti.getWidth();
//    height = tti.getHeight();
//    int[] in_pix = tti.getArray();
//    int[] out_pix = new int[in_pix.length];
//
//    for (int i = 0; i < width; i++) {
//      for (int j = 0; j < height; j++) {
//        out_pix[i + width * j] = in_pix[i + width * j];
//      }
//    }
//    createImageFromArray(out_pix, width, height);
//  }
