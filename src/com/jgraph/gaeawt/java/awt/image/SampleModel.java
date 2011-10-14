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

public abstract class SampleModel
{

	protected int width;

	protected int height;

	protected int numBands;

	public SampleModel(int w, int h, int numBands)
	{
		if (w <= 0 || h <= 0)
		{
			// awt.22E=w or h is less than or equal to zero
			throw new IllegalArgumentException(Messages.getString("awt.22E")); //$NON-NLS-1$
		}

		double squre = ((double) w) * ((double) h);
		if (squre >= Integer.MAX_VALUE)
		{
			// awt.22F=The product of w and h is greater than Integer.MAX_VALUE
			throw new IllegalArgumentException(Messages.getString("awt.22F")); //$NON-NLS-1$
		}

		if (numBands < 1)
		{
			// awt.231=Number of bands must be more then 0
			throw new IllegalArgumentException(Messages.getString("awt.231")); //$NON-NLS-1$
		}

		this.width = w;
		this.height = h;
		this.numBands = numBands;

	}

	public abstract int getDataElements(int x, int y, int[] data);

	public int[] getDataElements(int x, int y, int w, int h, int[] data)
	{
		int idx = 0;
		int idata[] = new int[w * h];

		for (int i = y; i < y + h; i++)
		{
			for (int j = x; j < x + w; j++)
			{
				idata[idx++] = getDataElements(j, i, data);
			}
		}

		return idata;
	}

	public abstract void setDataElements(int x, int y, int value,
			int[] data);

	public void setDataElements(int x, int y, int w, int h, int[] values,
			int[] data)
	{
		int idx = 0;
		
		for (int i = y; i < y + h; i++)
		{
			for (int j = x; j < x + w; j++)
			{
				setDataElements(j, i, values[idx++], data);
			}
		}
	}

	public abstract SampleModel createSubsetSampleModel(int bands[]);

	public abstract SampleModel createCompatibleSampleModel(int a0, int a1);

	public int[] getPixel(int x, int y, int iArray[], int[] data)
	{
		if (x < 0 || y < 0 || x >= this.width || y >= this.height)
		{
			// awt.63=Coordinates are not in bounds
			throw new ArrayIndexOutOfBoundsException(
					Messages.getString("awt.63")); //$NON-NLS-1$
		}
		int pixel[];

		if (iArray == null)
		{
			pixel = new int[numBands];
		}
		else
		{
			pixel = iArray;
		}

		for (int i = 0; i < numBands; i++)
		{
			pixel[i] = getSample(x, y, i, data);
		}

		return pixel;
	}

	public void setPixel(int x, int y, int iArray[], int[] data)
	{
		if (x < 0 || y < 0 || x >= this.width || y >= this.height)
		{
			// awt.63=Coordinates are not in bounds
			throw new ArrayIndexOutOfBoundsException(
					Messages.getString("awt.63")); //$NON-NLS-1$
		}
		for (int i = 0; i < numBands; i++)
		{
			setSample(x, y, i, iArray[i], data);
		}
	}

	public abstract int getSample(int x, int y, int b, int[] data);

	public int[] getPixels(int x, int y, int w, int h, int iArray[],
			int[] data)
	{
		if (x < 0 || y < 0 || x + w > this.width || y + h > this.height)
		{
			// awt.63=Coordinates are not in bounds
			throw new ArrayIndexOutOfBoundsException(
					Messages.getString("awt.63")); //$NON-NLS-1$
		}
		int pixels[];
		int idx = 0;

		if (iArray == null)
		{
			pixels = new int[w * h * numBands];
		}
		else
		{
			pixels = iArray;
		}

		for (int i = y; i < y + h; i++)
		{
			for (int j = x; j < x + w; j++)
			{
				for (int n = 0; n < numBands; n++)
				{
					pixels[idx++] = getSample(j, i, n, data);
				}
			}
		}
		return pixels;
	}

	public void setPixels(int x, int y, int w, int h, int iArray[],
			int[] data)
	{
		if (x < 0 || y < 0 || x + w > this.width || y + h > this.height)
		{
			// awt.63=Coordinates are not in bounds
			throw new ArrayIndexOutOfBoundsException(
					Messages.getString("awt.63")); //$NON-NLS-1$
		}
		int idx = 0;
		for (int i = y; i < y + h; i++)
		{
			for (int j = x; j < x + w; j++)
			{
				for (int n = 0; n < numBands; n++)
				{
					setSample(j, i, n, iArray[idx++], data);
				}
			}
		}
	}

	public abstract void setSample(int x, int y, int b, int s, int[] data);

	public int[] getSamples(int x, int y, int w, int h, int b, int iArray[],
			int[] data)
	{
		int samples[];
		int idx = 0;

		if (iArray == null)
		{
			samples = new int[w * h];
		}
		else
		{
			samples = iArray;
		}

		for (int i = y; i < y + h; i++)
		{
			for (int j = x; j < x + w; j++)
			{
				samples[idx++] = getSample(j, i, b, data);
			}
		}

		return samples;
	}

	public void setSamples(int x, int y, int w, int h, int b, int iArray[],
			int[] data)
	{
		int idx = 0;
		for (int i = y; i < y + h; i++)
		{
			for (int j = x; j < x + w; j++)
			{
				setSample(j, i, b, iArray[idx++], data);
			}
		}
	}

	public abstract int[] createDataBuffer();

	public abstract int getSampleSize(int band);

	public abstract int[] getSampleSize();

	public final int getWidth()
	{
		return width;
	}

	public final int getNumBands()
	{
		return numBands;
	}

	public final int getHeight()
	{
		return height;
	}
}
