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

import java.util.Arrays;

import org.apache.harmony.awt.internal.nls.Messages;

import com.jgraph.gaeawt.java.awt.Transparency;
import com.jgraph.gaeawt.java.awt.color.ColorSpace;

public class DirectColorModel extends PackedColorModel
{
	public DirectColorModel(ColorSpace space, int bits, int rmask, int gmask,
			int bmask, int amask)
	{

		super(space, bits, rmask, gmask, bmask, amask,
				(amask == 0 ? Transparency.OPAQUE : Transparency.TRANSLUCENT));
	}

	public DirectColorModel(int bits, int rmask, int gmask, int bmask, int amask)
	{

		super(ColorSpace.getInstance(), bits, rmask,
				gmask, bmask, amask, (amask == 0 ? Transparency.OPAQUE
						: Transparency.TRANSLUCENT));
	}

	public DirectColorModel(int bits, int rmask, int gmask, int bmask)
	{
		this(bits, rmask, gmask, bmask, 0);
	}

	@Override
	public int getDataElements(int components[], int offset)
	{
		int pixel = 0;
		for (int i = 0; i < numComponents; i++)
		{
			pixel |= (components[offset + i] << offsets[i]) & componentMasks[i];
		}

		return pixel;
	}

	@Override
	public String toString()
	{
		// The output format based on 1.5 release behaviour. 
		// It could be reveled such way:
		// BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		// ColorModel cm = bi.getColorModel();
		// System.out.println(cm.toString());
		String str = "DirectColorModel:" + " rmask = " + //$NON-NLS-1$ //$NON-NLS-2$
				Integer.toHexString(componentMasks[0]) + " gmask = " + //$NON-NLS-1$
				Integer.toHexString(componentMasks[1]) + " bmask = " + //$NON-NLS-1$
				Integer.toHexString(componentMasks[2]) + " amask = " + //$NON-NLS-1$
				(!hasAlpha ? "0" : Integer.toHexString(componentMasks[3])); //$NON-NLS-1$

		return str;
	}

	@Override
	public final int[] getComponents(int[] pixel, int components[], int offset)
	{

		if (components == null)
		{
			components = new int[numComponents + offset];
		}

		int ia[] = (int[]) pixel;

		return getComponents(ia[0], components, offset);
	}

	@Override
	public final WritableRaster createCompatibleWritableRaster(int w, int h)
	{
		if (w <= 0 || h <= 0)
		{
			// awt.22E=w or h is less than or equal to zero
			throw new IllegalArgumentException(Messages.getString("awt.22E")); //$NON-NLS-1$
		}

		int bandMasks[] = componentMasks.clone();

		return Raster.createPackedRaster(w, h, bandMasks);
	}

	@Override
	public boolean isCompatibleRaster(Raster raster)
	{
		SampleModel sm = raster.getSampleModel();
		if (!(sm instanceof SinglePixelPackedSampleModel))
		{
			return false;
		}

		SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel) sm;

		if (sppsm.getNumBands() != numComponents)
		{
			return false;
		}

		int maskBands[] = sppsm.getBitMasks();
		return Arrays.equals(maskBands, componentMasks);
	}

	@Override
	public int getDataElement(int components[], int offset)
	{
		int pixel = 0;
		for (int i = 0; i < numComponents; i++)
		{
			pixel |= (components[offset + i] << offsets[i]) & componentMasks[i];
		}
		return pixel;
	}

	@Override
	public final int[] getComponents(int pixel, int components[], int offset)
	{
		if (components == null)
		{
			components = new int[numComponents + offset];
		}
		for (int i = 0; i < numComponents; i++)
		{
			components[offset + i] = (pixel & componentMasks[i]) >> offsets[i];
		}
		return components;
	}

	public final int getRedMask()
	{
		return componentMasks[0];
	}

	public final int getGreenMask()
	{
		return componentMasks[1];
	}

	public final int getBlueMask()
	{
		return componentMasks[2];
	}

	public final int getAlphaMask()
	{
		if (hasAlpha)
		{
			return componentMasks[3];
		}
		return 0;
	}
}
