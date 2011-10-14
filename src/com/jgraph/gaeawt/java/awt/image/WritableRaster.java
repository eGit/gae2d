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

import org.apache.harmony.awt.internal.nls.Messages;

import com.jgraph.gaeawt.java.awt.Rectangle;

public class WritableRaster extends Raster
{

	protected WritableRaster(SampleModel sampleModel, int[] dataBuffer,
			Rectangle aRegion, WritableRaster parent)
	{
		super(sampleModel, dataBuffer, aRegion, parent);
	}

	protected WritableRaster(SampleModel sampleModel, int[] dataBuffer)
	{
		this(sampleModel, dataBuffer, new Rectangle(0, 0,
				sampleModel.width, sampleModel.height), null);
	}

	protected WritableRaster(SampleModel sampleModel)
	{
		this(sampleModel, sampleModel.createDataBuffer(), new Rectangle(
				0, 0, sampleModel.width, sampleModel.height), null);
	}

	public void setDataElements(int x, int y, int inData)
	{
		sampleModel.setDataElements(x, y, inData, dataBuffer);
	}

	public void setDataElements(int x, int y, int w, int h, int[] inData)
	{
		sampleModel.setDataElements(x, y, w, h, inData, dataBuffer);
	}

	public WritableRaster getWritableParent()
	{
		return (WritableRaster) parent;
	}

	public void setRect(Raster srcRaster)
	{
		setRect(0, 0, srcRaster);
	}

	public void setRect(int dx, int dy, Raster srcRaster)
	{
		int w = srcRaster.getWidth();
		int h = srcRaster.getHeight();

		int srcX = srcRaster.getMinX();
		int srcY = srcRaster.getMinY();

		int dstX = srcX + dx;
		int dstY = srcY + dy;

		if (dstX < this.minX)
		{
			int minOffX = this.minX - dstX;
			w -= minOffX;
			dstX = this.minX;
			srcX += minOffX;
		}

		if (dstY < this.minY)
		{
			int minOffY = this.minY - dstY;
			h -= minOffY;
			dstY = this.minY;
			srcY += minOffY;
		}

		if (dstX + w > this.minX + this.width)
		{
			int maxOffX = (dstX + w) - (this.minX + this.width);
			w -= maxOffX;
		}

		if (dstY + h > this.minY + this.height)
		{
			int maxOffY = (dstY + h) - (this.minY + this.height);
			h -= maxOffY;
		}

		if (w <= 0 || h <= 0)
		{
			return;
		}

		int iPixelsLine[] = null;
		for (int i = 0; i < h; i++)
		{
			iPixelsLine = srcRaster
					.getPixels(srcX, srcY + i, w, 1, iPixelsLine);
			setPixels(dstX, dstY + i, w, 1, iPixelsLine);
		}
	}

	public void setDataElements(int x, int y, Raster inRaster)
	{
		int dstX = x + inRaster.getMinX();
		int dstY = y + inRaster.getMinY();

		int w = inRaster.getWidth();
		int h = inRaster.getHeight();

		if (dstX < this.minX || dstX + w > this.minX + this.width
				|| dstY < this.minY || dstY + h > this.minY + this.height)
		{
			// awt.63=Coordinates are not in bounds
			throw new ArrayIndexOutOfBoundsException(
					Messages.getString("awt.63")); //$NON-NLS-1$
		}

		int srcX = inRaster.getMinX();
		int srcY = inRaster.getMinY();
		int[] line = null;

		for (int i = 0; i < h; i++)
		{
			line = inRaster.getDataElements(srcX, srcY + i, w, 1);
			setDataElements(dstX, dstY + i, w, 1, line);
		}
	}

	public void setPixel(int x, int y, int iArray[])
	{
		sampleModel.setPixel(x, y, iArray, dataBuffer);
	}

	public void setPixels(int x, int y, int w, int h, int iArray[])
	{
		sampleModel.setPixels(x, y, w, h, iArray, dataBuffer);
	}

	public void setSamples(int x, int y, int w, int h, int b, int iArray[])
	{
		sampleModel.setSamples(x, y, w, h, b, iArray, dataBuffer);
	}

	public void setSample(int x, int y, int b, int s)
	{
		sampleModel.setSample(x, y, b, s, dataBuffer);
	}
}
