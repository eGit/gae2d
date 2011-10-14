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

import org.apache.harmony.awt.gl.image.OrdinaryWritableRaster;
import org.apache.harmony.awt.internal.nls.Messages;

import com.jgraph.gaeawt.java.awt.Point;
import com.jgraph.gaeawt.java.awt.Rectangle;

public class Raster
{

	protected int[] dataBuffer;

	protected int height;

	protected int minX;

	protected int minY;

	protected int numBands;

	protected int numDataElements;

	protected Raster parent;

	protected SampleModel sampleModel;

	protected int width;

	public static WritableRaster createPackedRaster(int[] dataBuffer,
			int w, int h, int scanlineStride, int bandMasks[], Point location)
	{
		if (w <= 0 || h <= 0)
		{
			// awt.22E=w or h is less than or equal to zero
			throw new RasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
		}

		if (location == null)
		{
			location = new Point(0, 0);
		}

		if ((long) location.x + w > Integer.MAX_VALUE
				|| (long) location.y + h > Integer.MAX_VALUE)
		{
			// awt.276=location.x + w or location.y + h results in integer overflow
			throw new RasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
		}

		if (bandMasks == null)
		{
			// awt.27C=bandMasks is null
			throw new RasterFormatException(Messages.getString("awt.27C")); //$NON-NLS-1$
		}

		SinglePixelPackedSampleModel sampleModel = new SinglePixelPackedSampleModel(
				w, h, scanlineStride, bandMasks);

		return new OrdinaryWritableRaster(sampleModel, dataBuffer);
	}

	public static WritableRaster createPackedRaster(int w, int h,
			int bandMasks[], Point location)
	{
		if (w <= 0 || h <= 0)
		{
			// awt.22E=w or h is less than or equal to zero
			throw new RasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
		}

		if (location == null)
		{
			location = new Point(0, 0);
		}

		if ((long) location.x + w > Integer.MAX_VALUE
				|| (long) location.y + h > Integer.MAX_VALUE)
		{
			// awt.276=location.x + w or location.y + h results in integer overflow
			throw new RasterFormatException(Messages.getString("awt.276")); //$NON-NLS-1$
		}

		if (bandMasks == null)
		{
			// awt.27C=bandMasks is null
			throw new NullPointerException(Messages.getString("awt.27C")); //$NON-NLS-1$
		}

		int[] data = new int[w * h];

		return createPackedRaster(data, w, h, w, bandMasks, location);
	}

	public static WritableRaster createWritableRaster(SampleModel sm,
			int[] db)
	{
		return new OrdinaryWritableRaster(sm, db);
	}

	protected Raster(SampleModel sampleModel, int[] dataBuffer)
	{

		this(sampleModel, dataBuffer, new Rectangle(0, 0,
				sampleModel.getWidth(), sampleModel.getHeight()), null);
	}

	protected Raster(SampleModel sampleModel, int[] dataBuffer,
			Rectangle aRegion, Raster parent)
	{

		if (sampleModel == null || dataBuffer == null || aRegion == null)
		{
			// awt.281=sampleModel, dataBuffer or aRegion is null
			throw new NullPointerException(Messages.getString("awt.281")); //$NON-NLS-1$
		}

		if (aRegion.width <= 0 || aRegion.height <= 0)
		{
			// awt.282=aRegion has width or height less than or equal to zero
			throw new RasterFormatException(Messages.getString("awt.282")); //$NON-NLS-1$
		}

		if ((long) aRegion.x + (long) aRegion.width > Integer.MAX_VALUE)
		{
			// awt.283=Overflow X coordinate of Raster
			throw new RasterFormatException(Messages.getString("awt.283")); //$NON-NLS-1$
		}

		if ((long) aRegion.y + (long) aRegion.height > Integer.MAX_VALUE)
		{
			// awt.284=Overflow Y coordinate of Raster
			throw new RasterFormatException(Messages.getString("awt.284")); //$NON-NLS-1$
		}

		this.sampleModel = sampleModel;
		this.dataBuffer = dataBuffer;
		this.minX = aRegion.x;
		this.minY = aRegion.y;
		this.width = aRegion.width;
		this.height = aRegion.height;
		this.parent = parent;
		this.numBands = sampleModel.getNumBands();
	}

	protected Raster(SampleModel sampleModel)
	{
		this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(
				0, 0, sampleModel.getWidth(),
				sampleModel.getHeight()), null);
	}

	public WritableRaster createCompatibleWritableRaster()
	{
		return new OrdinaryWritableRaster(sampleModel);
	}

	public WritableRaster createCompatibleWritableRaster(int w, int h)
	{
		if (w <= 0 || h <= 0)
		{
			// awt.22E=w or h is less than or equal to zero
			throw new RasterFormatException(Messages.getString("awt.22E")); //$NON-NLS-1$
		}

		SampleModel sm = sampleModel.createCompatibleSampleModel(w, h);

		return new OrdinaryWritableRaster(sm);
	}

	public Rectangle getBounds()
	{
		return new Rectangle(minX, minY, width, height);
	}

	public int[] getDataBuffer()
	{
		return dataBuffer;
	}
	
	public void setDataBuffer(int[] buffer)
	{
		dataBuffer = buffer;;
	}

	public int[] getDataElements(int x, int y, int w, int h)
	{
		return sampleModel.getDataElements(x, y, w, h, dataBuffer);
	}

	public int getDataElements(int x, int y)
	{
		return sampleModel.getDataElements(x, y, dataBuffer);
	}

	public final int getHeight()
	{
		return height;
	}

	public final int getMinX()
	{
		return minX;
	}

	public final int getMinY()
	{
		return minY;
	}

	public final int getNumBands()
	{
		return numBands;
	}

	public final int getNumDataElements()
	{
		return numDataElements;
	}

	public Raster getParent()
	{
		return parent;
	}

	public int[] getPixel(int x, int y, int iArray[])
	{
		return sampleModel.getPixel(x, y, iArray, dataBuffer);
	}

	public int[] getPixels(int x, int y, int w, int h, int iArray[])
	{
		return sampleModel.getPixels(x, y, w, h, iArray, dataBuffer);
	}

	public int getSample(int x, int y, int b)
	{
		return sampleModel.getSample(x, y, b, dataBuffer);
	}

	public SampleModel getSampleModel()
	{
		return sampleModel;
	}

	public int[] getSamples(int x, int y, int w, int h, int b, int iArray[])
	{
		return sampleModel.getSamples(x, y, w, h, b, iArray, dataBuffer);
	}

	public final int getWidth()
	{
		return width;
	}
}
