/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
/**
 * @author Oleg V. Khaschansky
 */

package com.jgraph.gaeawt.java.awt.image;

import org.apache.harmony.awt.internal.nls.Messages;

public class BufferedImageFilter extends ImageFilter implements Cloneable
{
	private BufferedImageOp op;

	private WritableRaster raster;

	private int iData[];

	private byte bData[];

	private int width;

	private int height;

	private ColorModel cm;

	private boolean forcedRGB = false;

	public BufferedImageFilter(BufferedImageOp op)
	{
		if (op == null)
		{
			throw new NullPointerException(Messages.getString("awt.05")); //$NON-NLS-1$
		}
		this.op = op;
	}

	public BufferedImageOp getBufferedImageOp()
	{
		return op;
	}

	@Override
	public void setDimensions(int width, int height)
	{
		this.width = width;
		this.height = height;
		// Stop image consuming if no pixels expected.
		if (width <= 0 || height <= 0)
		{
			consumer.imageComplete(ImageConsumer.STATICIMAGEDONE);
			reset();
		}
	}

	@Override
	public void setColorModel(ColorModel model)
	{
		if (this.cm != null && this.cm != model && raster != null)
		{
			forceRGB();
		}
		else
		{
			this.cm = model;
		}
	}

	@Override
	public void setPixels(int x, int y, int w, int h, ColorModel model,
			int[] pixels, int off, int scansize)
	{
		setPixels(x, y, w, h, model, pixels, off, scansize, false);
	}

	@Override
	public void imageComplete(int status)
	{
		if (status == STATICIMAGEDONE || status == SINGLEFRAMEDONE)
		{
			BufferedImage bim = new BufferedImage(cm, raster, null);
			bim = op.filter(bim, null);
			int[] dstDb = bim.getRaster().getDataBuffer();
			ColorModel dstCm = bim.getColorModel();
			int dstW = bim.getWidth();
			int dstH = bim.getHeight();

			consumer.setDimensions(dstW, dstH);

			consumer.setColorModel(dstCm);
			consumer.setPixels(0, 0, dstW, dstH, dstCm, dstDb, 0, dstW);
		}
		else if (status == IMAGEERROR || status == IMAGEABORTED)
		{
			reset();
		}

		consumer.imageComplete(status);
	}

	private void setPixels(int x, int y, int w, int h, ColorModel model,
			int[] pixels, int off, int scansize, boolean isByteData)
	{
		// Check bounds
		// Need to copy only the pixels that will fit into the destination area
		if (x < 0)
		{
			w -= x;
			off += x;
			x = 0;
		}

		if (y < 0)
		{
			h -= y;
			off += y * scansize;
			y = 0;
		}

		if (x + w > width)
		{
			w = width - x;
		}

		if (y + h > height)
		{
			h = height - y;
		}

		if (w <= 0 || h <= 0)
		{
			return;
		}

		// Check model
		if (this.cm == null)
		{
			setColorModel(model);
		}
		else if (model == null)
		{
			model = this.cm;
		}
		else if (!model.equals(this.cm))
		{
			forceRGB();
		}

		boolean canArraycopy = true;
		// Process pixels

		if (isByteData)
		{ // There are int data already but the new data are bytes
			forceRGB();
			canArraycopy = false;
		}
		else if (!forcedRGB || model.equals(ColorModel.getRGBdefault()))
		{
			canArraycopy = true;
		}

		off += x;
		int maxOffset = off + h * scansize;
		int dstOffset = x + y * width;

		if (canArraycopy)
		{
			Object dstArray = isByteData ? (Object) bData : (Object) iData;
			for (; off < maxOffset; off += scansize, dstOffset += width)
			{
				System.arraycopy(pixels, off, dstArray, dstOffset, w);
			}
		}
		else
		{
			// RGB conversion
			for (; off < maxOffset; off += scansize, dstOffset += width)
			{
				int srcPos = off;
				int dstPos = dstOffset;
				int maxDstPos = dstOffset + w;
				for (; dstPos < maxDstPos; dstPos++, srcPos++)
				{
					iData[dstPos] = pixels[srcPos];
				}
			}
		}
	}

	private void forceRGB()
	{
		if (!forcedRGB)
		{
			forcedRGB = true;
			int size = width * height;
			int rgbData[] = new int[size];

			if (bData != null)
			{
				for (int i = 0; i < size; i++)
				{
					rgbData[i] = bData[i];
				}
			}
			else if (iData != null)
			{
				for (int i = 0; i < size; i++)
				{
					rgbData[i] = iData[i];
				}
			}

			cm = ColorModel.getRGBdefault();
			int masks[] = new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff,
					0xff000000 };
			raster = Raster.createPackedRaster(rgbData, width, height, width,
					masks);
			iData = rgbData;
			bData = null;
		}
	}

	private void reset()
	{
		width = 0;
		height = 0;
		forcedRGB = false;
		cm = null;
		iData = null;
		bData = null;
		raster = null;
	}
}
