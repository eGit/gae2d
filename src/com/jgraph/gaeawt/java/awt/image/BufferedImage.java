/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * @author Igor V. Stolyarov
 */
package com.jgraph.gaeawt.java.awt.image;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.harmony.awt.gl.ImageSurface;
import org.apache.harmony.awt.gl.Surface;
import org.apache.harmony.awt.gl.image.BufferedImageGraphics2D;
import org.apache.harmony.awt.gl.image.BufferedImageSource;
import org.apache.harmony.awt.internal.nls.Messages;

import com.jgraph.gaeawt.java.awt.Graphics;
import com.jgraph.gaeawt.java.awt.Graphics2D;
import com.jgraph.gaeawt.java.awt.Image;
import com.jgraph.gaeawt.java.awt.Point;
import com.jgraph.gaeawt.java.awt.Rectangle;
import com.jgraph.gaeawt.java.awt.Transparency;

public class BufferedImage extends Image implements WritableRenderedImage,
		Transparency
{

	public static final int TYPE_INT_RGB = 1;

	public static final int TYPE_INT_ARGB = 2;

	private static final int RED_MASK = 0x00ff0000;

	private static final int GREEN_MASK = 0x0000ff00;

	private static final int BLUE_MASK = 0x000000ff;

	private ColorModel cm;

	private final WritableRaster raster;

	private final int imageType;

	private Hashtable<?, ?> properties;

	// Surface of the Buffered Image - used for blitting one Buffered Image 
	// on the other one or on the Component
	private final ImageSurface imageSurf;

	public BufferedImage(ColorModel cm, WritableRaster raster, Hashtable<?, ?> properties)
	{
		if (!cm.isCompatibleRaster(raster))
		{
			// awt.4D=The raster is incompatible with this ColorModel
			throw new IllegalArgumentException(Messages.getString("awt.4D")); //$NON-NLS-1$
		}

		if (raster.getMinX() != 0 || raster.getMinY() != 0)
		{
			// awt.228=minX or minY of this raster not equal to zero
			throw new IllegalArgumentException(Messages.getString("awt.228")); //$NON-NLS-1$
		}

		this.cm = cm;
		this.raster = raster;
		this.properties = properties;

		imageType = Surface.getType(cm, raster);

		imageSurf = createImageSurface(imageType);
	}

	public BufferedImage(int width, int height, int imageType)
	{

		switch (imageType)
		{
			case TYPE_INT_RGB:
				cm = new DirectColorModel(24, RED_MASK, GREEN_MASK, BLUE_MASK);
				raster = cm.createCompatibleWritableRaster(width, height);
				break;

			case TYPE_INT_ARGB:
				cm = ColorModel.getRGBdefault();
				raster = cm.createCompatibleWritableRaster(width, height);
				break;

			default:
				// awt.224=Unknown image type
				throw new IllegalArgumentException(
						Messages.getString("awt.224")); //$NON-NLS-1$
		}
		this.imageType = imageType;
		imageSurf = createImageSurface(imageType);
	}

	@Override
	public Object getProperty(String name, ImageObserver observer)
	{
		return getProperty(name);
	}

	public Object getProperty(String name)
	{
		if (name == null)
		{
			// awt.225=Property name is null
			throw new NullPointerException(Messages.getString("awt.225")); //$NON-NLS-1$
		}
		if (properties == null)
		{
			return Image.UndefinedProperty;
		}
		Object property = properties.get(name);
		if (property == null)
		{
			property = Image.UndefinedProperty;
		}
		return property;
	}

	public Vector<RenderedImage> getSources()
	{
		return null;
	}

	public String[] getPropertyNames()
	{
		if (properties == null)
		{
			return null;
		}
		Vector<String> v = new Vector<String>();
		for (Enumeration<?> e = properties.keys(); e.hasMoreElements();)
		{
			try
			{
				v.add((String) e.nextElement());
			}
			catch (ClassCastException ex)
			{
			}
		}
		int size = v.size();
		if (size > 0)
		{
			String names[] = new String[size];
			for (int i = 0; i < size; i++)
			{
				names[i] = v.elementAt(i);
			}
			return names;
		}
		return null;
	}

	@Override
	public String toString()
	{
		return "BufferedImage@" + Integer.toHexString(hashCode()) + //$NON-NLS-1$
				": type = " + imageType + " " + cm + " " + raster; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public WritableRaster getWritableTile(int tileX, int tileY)
	{
		return raster;
	}

	public WritableRaster getRaster()
	{
		return raster;
	}

	public void removeTileObserver(TileObserver to)
	{
	}

	public void addTileObserver(TileObserver to)
	{
	}

	public SampleModel getSampleModel()
	{
		return raster.getSampleModel();
	}

	public void setData(Raster r)
	{

		Rectangle from = r.getBounds();
		Rectangle to = raster.getBounds();
		Rectangle intersection = to.intersection(from);

		int minX = intersection.x;
		int minY = intersection.y;
		int w = intersection.width;
		int h = intersection.height;

		int[] data = null;

		data = r.getDataElements(minX, minY, w, h);
		raster.setDataElements(minX, minY, w, h, data);
	}

	public Raster getTile(int tileX, int tileY)
	{
		if (tileX == 0 && tileY == 0)
		{
			return raster;
		}
		// awt.226=Both tileX and tileY are not equal to 0
		throw new ArrayIndexOutOfBoundsException(Messages.getString("awt.226")); //$NON-NLS-1$
	}

	@Override
	public ImageProducer getSource()
	{
		return new BufferedImageSource(this, properties);
	}

	@Override
	public int getWidth(ImageObserver observer)
	{
		return raster.getWidth();
	}

	@Override
	public int getHeight(ImageObserver observer)
	{
		return raster.getHeight();
	}

	public ColorModel getColorModel()
	{
		return cm;
	}

	public Point[] getWritableTileIndices()
	{
		Point points[] = new Point[1];
		points[0] = new Point(0, 0);
		return points;
	}

	public Graphics2D createGraphics()
	{
		return new BufferedImageGraphics2D(this);
	}

	@Override
	public Graphics getGraphics()
	{
		return createGraphics();
	}

	public int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize)
	{
		if (rgbArray == null)
		{
			rgbArray = new int[offset + h * scansize];
		}

		int off = offset;
		for (int y = startY; y < startY + h; y++, off += scansize)
		{
			int i = off;
			for (int x = startX; x < startX + w; x++, i++)
			{
				rgbArray[i] = raster.getDataElements(x, y);
			}
		}
		return rgbArray;
	}

	public void setRGB(int startX, int startY, int w, int h, int[] rgbArray,
			int offset, int scansize)
	{
		int off = offset;
		for (int y = startY; y < startY + h; y++, off += scansize)
		{
			int i = off;
			for (int x = startX; x < startX + w; x++, i++)
			{
				raster.setDataElements(x, y, rgbArray[i]);
			}
		}
	}

	public synchronized void setRGB(int x, int y, int rgb)
	{
		raster.setDataElements(x, y, rgb);
	}

	public boolean isTileWritable(int tileX, int tileY)
	{
		if (tileX == 0 && tileY == 0)
		{
			return true;
		}
		// awt.226=Both tileX and tileY are not equal to 0
		throw new IllegalArgumentException(Messages.getString("awt.226")); //$NON-NLS-1$
	}

	public void releaseWritableTile(int tileX, int tileY)
	{
	}

	public int getRGB(int x, int y)
	{
		return raster.getDataElements(x, y);
	}

	public boolean hasTileWriters()
	{
		return true;
	}

	@Override
	public void flush()
	{
		imageSurf.dispose();
	}

	public int getWidth()
	{
		return raster.getWidth();
	}

	public int getType()
	{
		return imageType;
	}

	public int getTileWidth()
	{
		return raster.getWidth();
	}

	public int getTileHeight()
	{
		return raster.getHeight();
	}

	public int getNumYTiles()
	{
		return 1;
	}

	public int getNumXTiles()
	{
		return 1;
	}

	public int getMinY()
	{
		return raster.getMinY();
	}

	public int getMinX()
	{
		return raster.getMinX();
	}

	public int getMinTileY()
	{
		return 0;
	}

	public int getMinTileX()
	{
		return 0;
	}

	public int getHeight()
	{
		return raster.getHeight();
	}

	private ImageSurface createImageSurface(int type)
	{
		return new ImageSurface(getColorModel(), getRaster(), type);
	}

	public ImageSurface getImageSurface()
	{
		return imageSurf;
	}

	public int getTransparency()
	{
		return cm.getTransparency();
	}
}
