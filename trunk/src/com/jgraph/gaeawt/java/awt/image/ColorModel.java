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

public abstract class ColorModel implements Transparency
{
	int componentMasks[];

	int offsets[];

	float scales[];

	protected int pixel_bits; // Pixel length in bits

	ColorSpace cs;

	boolean hasAlpha;

	int transparency;

	int numComponents;

	int[] bits; // Array of components masks 

	int[] maxValues = null; // Max values that may be represent by color
							// components

	int maxBitLength; // Max length color components in bits

	/** caches alpha channel color model **/
	private static ColorModel RGBdefault;

	/** caches non-alpha channel color model **/
	private static ColorModel nonAlphaDefault;

	protected ColorModel(int pixel_bits, int[] bits, ColorSpace cspace,
			boolean hasAlpha, int transparency)
	{

		if (pixel_bits < 1)
		{
			// awt.26B=The number of bits in the pixel values is less than 1
			throw new IllegalArgumentException(Messages.getString("awt.26B")); //$NON-NLS-1$
		}

		if (bits == null)
		{
			// awt.26C=bits is null
			throw new NullPointerException(Messages.getString("awt.26C")); //$NON-NLS-1$
		}

		int sum = 0;
		for (int element : bits)
		{
			if (element < 0)
			{
				// awt.26D=The elements in bits is less than 0
				throw new IllegalArgumentException(
						Messages.getString("awt.26D")); //$NON-NLS-1$
			}
			sum += element;
		}

		if (sum < 1)
		{
			// awt.26E=The sum of the number of bits in bits is less than 1
			throw new NullPointerException(Messages.getString("awt.26E")); //$NON-NLS-1$
		}

		if (cspace == null)
		{
			// awt.26F=The cspace is null
			throw new IllegalArgumentException(Messages.getString("awt.26F")); //$NON-NLS-1$
		}

		if (transparency < Transparency.OPAQUE
				|| transparency > Transparency.TRANSLUCENT)
		{
			// awt.270=The transparency is not a valid value
			throw new IllegalArgumentException(Messages.getString("awt.270")); //$NON-NLS-1$
		}

		this.pixel_bits = pixel_bits;
		this.bits = bits.clone();

		maxValues = new int[bits.length];
		maxBitLength = 0;
		for (int i = 0; i < maxValues.length; i++)
		{
			maxValues[i] = (1 << bits[i]) - 1;
			if (bits[i] > maxBitLength)
			{
				maxBitLength = bits[i];
			}
		}

		cs = cspace;
		this.hasAlpha = hasAlpha;

		if (hasAlpha)
		{
			numComponents = 4;
		}
		else
		{
			numComponents = 3;
		}

		this.transparency = transparency;
	}

	public ColorModel(int bits)
	{
		if (bits < 1)
		{
			// awt.271=The number of bits in bits is less than 1
			throw new IllegalArgumentException(Messages.getString("awt.271")); //$NON-NLS-1$
		}

		pixel_bits = bits;
		cs = ColorSpace.getInstance();
		hasAlpha = true;
		transparency = Transparency.TRANSLUCENT;

		numComponents = 4;

		this.bits = null;
	}

	public abstract int getDataElements(int[] components, int offset);

	@Override
	public String toString()
	{
		// The output format based on 1.5 release behaviour. 
		// ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB,
		// false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		// System.out.println(cm.toString());
		return "ColorModel: Color Space = " + cs.toString() + "; has alpha = " //$NON-NLS-1$ //$NON-NLS-2$
				+ hasAlpha + "; transparency = " + transparency //$NON-NLS-1$
				+ "; pixel bits = " + pixel_bits;
	}

	public int[] getComponents(int[] pixel, int[] components, int offset)
	{
		throw new UnsupportedOperationException("This method is not " + //$NON-NLS-1$
				"supported by this ColorModel"); //$NON-NLS-1$
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof PackedColorModel))
		{
			return false;
		}
		PackedColorModel cm = (PackedColorModel) obj;

		return (pixel_bits == cm.getPixelSize()
				&& hasAlpha == cm.hasAlpha()
				&& transparency == cm.getTransparency()
				&& numComponents == cm.getNumComponents()
				&& Arrays.equals(bits, cm.getComponentSize()) && Arrays.equals(
				componentMasks, cm.getMasks()));
	}

	public WritableRaster createCompatibleWritableRaster(int w, int h)
	{
		throw new UnsupportedOperationException("This method is not " + //$NON-NLS-1$
				"supported by this ColorModel"); //$NON-NLS-1$
	}

	public boolean isCompatibleSampleModel(SampleModel sm)
	{
		if (sm == null)
		{
			return false;
		}
		if (!(sm instanceof SinglePixelPackedSampleModel))
		{
			return false;
		}
		SinglePixelPackedSampleModel esm = (SinglePixelPackedSampleModel) sm;

		return ((esm.getNumBands() == numComponents) && Arrays.equals(
				esm.getBitMasks(), componentMasks));
	}

	public SampleModel createCompatibleSampleModel(int w, int h)
	{
		throw new UnsupportedOperationException("This method is not " + //$NON-NLS-1$
				"supported by this ColorModel"); //$NON-NLS-1$
	}

	
	public boolean isCompatibleRaster(Raster raster)
	{
		throw new UnsupportedOperationException("This method is not " + //$NON-NLS-1$
				"supported by this ColorModel"); //$NON-NLS-1$
	}

	public final ColorSpace getColorSpace()
	{
		return cs;
	}

	public int getDataElement(int[] components, int offset)
	{
		throw new UnsupportedOperationException("This method is not " + //$NON-NLS-1$
				"supported by this ColorModel"); //$NON-NLS-1$
	}

	public abstract int[] getComponents(int pixel, int components[], int offset);

	public int getComponentSize(int componentIdx)
	{
		if (bits == null)
		{
			// awt.26C=bits is null
			throw new NullPointerException(Messages.getString("awt.26C")); //$NON-NLS-1$
		}

		if (componentIdx < 0 || componentIdx >= bits.length)
		{
			// awt.274=componentIdx is greater than the number of components or less than zero
			throw new ArrayIndexOutOfBoundsException(
					Messages.getString("awt.274")); //$NON-NLS-1$
		}

		return bits[componentIdx];
	}

	public int[] getComponentSize()
	{
		if (bits != null)
		{
			return bits.clone();
		}
		return null;
	}

	public final boolean hasAlpha()
	{
		return hasAlpha;
	}

	@Override
	public int hashCode()
	{
		int hash = 0;
		int tmp;

		if (hasAlpha)
		{
			hash ^= 1;
			hash <<= 8;
		}

		tmp = hash >>> 24;
		hash ^= transparency;
		hash <<= 8;
		hash |= tmp;

		tmp = hash >>> 24;
		hash ^= pixel_bits;
		hash <<= 8;
		hash |= tmp;

		if (bits != null)
		{

			for (int element : bits)
			{
				tmp = hash >>> 24;
				hash ^= element;
				hash <<= 8;
				hash |= tmp;
			}

		}

		return hash;
	}

	public int getTransparency()
	{
		return transparency;
	}

	public int getPixelSize()
	{
		return pixel_bits;
	}

	public int getNumComponents()
	{
		return numComponents;
	}

	public static ColorModel getRGBdefault()
	{
		if (RGBdefault == null)
		{
			RGBdefault = new DirectColorModel(32, 0x00ff0000, 0x0000ff00,
					0x000000ff, 0xff000000);
		}
		return RGBdefault;
	}

	public static ColorModel getNonAlphaDefault()
	{
		if (nonAlphaDefault == null)
		{
			nonAlphaDefault = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
		}
		return nonAlphaDefault;
	}

	protected void parseComponents()
	{
		offsets = new int[numComponents];
		scales = new float[numComponents];
		for (int i = 0; i < numComponents; i++)
		{
			int off = 0;
			int mask = componentMasks[i];
			while ((mask & 1) == 0)
			{
				mask >>>= 1;
				off++;
			}
			offsets[i] = off;
			if (bits[i] == 0)
			{
				scales[i] = 256.0f; // May be any value different from zero,
				// because will dividing by zero
			}
			else
			{
				scales[i] = 255.0f / maxValues[i];
			}
		}

	}

	@Override
	public void finalize()
	{
		// This method is added for the API compatibility
		// Don't need to call super since Object's finalize is always empty
	}
}
