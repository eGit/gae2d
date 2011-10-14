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
 * @author Oleg V. Khaschansky
 *
 * @date: Oct 14, 2005
 */

package com.jgraph.gaeawt.java.awt.image;

import org.apache.harmony.awt.internal.nls.Messages;

import com.jgraph.gaeawt.java.awt.AlphaComposite;
import com.jgraph.gaeawt.java.awt.Graphics2D;
import com.jgraph.gaeawt.java.awt.RenderingHints;
import com.jgraph.gaeawt.java.awt.geom.Point2D;
import com.jgraph.gaeawt.java.awt.geom.Rectangle2D;

public class LookupOp implements BufferedImageOp, RasterOp
{
	private final LookupTable lut;

	private RenderingHints hints;

	private final boolean canUseIpp;

	// We don't create levels/values when it is possible to reuse old
	private int cachedLevels[];

	private int cachedValues[];

	// Number of channels for which cache is valid.
	// If negative number of channels is same as positive but skipAlpha was specified
	private int validForChannels;

	static int levelInitializer[] = new int[0x10000];

	static
	{
		// TODO
		// System.loadLibrary("imageops");

		for (int i = 1; i <= 0x10000; i++)
		{
			levelInitializer[i - 1] = i;
		}
	}

	public LookupOp(LookupTable lookup, RenderingHints hints)
	{
		if (lookup == null)
		{
			throw new NullPointerException(Messages.getString(
					"awt.01", "lookup")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		lut = lookup;
		this.hints = hints;
		canUseIpp = lut instanceof ByteLookupTable;
	}

	public final LookupTable getTable()
	{
		return lut;
	}

	public final RenderingHints getRenderingHints()
	{
		return hints;
	}

	public final Point2D getPoint2D(Point2D srcPt, Point2D dstPt)
	{
		if (dstPt == null)
		{
			dstPt = new Point2D.Float();
		}

		dstPt.setLocation(srcPt);
		return dstPt;
	}

	public final Rectangle2D getBounds2D(Raster src)
	{
		return src.getBounds();
	}

	public final Rectangle2D getBounds2D(BufferedImage src)
	{
		return getBounds2D(src.getRaster());
	}

	public WritableRaster createCompatibleDestRaster(Raster src)
	{
		return src.createCompatibleWritableRaster();
	}

	public BufferedImage createCompatibleDestImage(BufferedImage src,
			ColorModel dstCM)
	{
		if (dstCM == null)
		{
			dstCM = src.getColorModel();
		}

		WritableRaster r = dstCM.isCompatibleSampleModel(src.getSampleModel()) ? src
				.getRaster().createCompatibleWritableRaster(src.getWidth(),
						src.getHeight())
				: dstCM.createCompatibleWritableRaster(src.getWidth(),
						src.getHeight());

		return new BufferedImage(dstCM, r, null);
	}

	public final WritableRaster filter(Raster src, WritableRaster dst)
	{
		if (dst == null)
		{
			dst = createCompatibleDestRaster(src);
		}
		else
		{
			if (src.getNumBands() != dst.getNumBands())
			{
				throw new IllegalArgumentException(
						Messages.getString("awt.237")); //$NON-NLS-1$            }
			}
			if (src.getWidth() != dst.getWidth())
			{
				throw new IllegalArgumentException(
						Messages.getString("awt.28F")); //$NON-NLS-1$            }
			}
			if (src.getHeight() != dst.getHeight())
			{
				throw new IllegalArgumentException(
						Messages.getString("awt.290")); //$NON-NLS-1$            }
			}
		}

		if (lut.getNumComponents() != 1
				&& lut.getNumComponents() != src.getNumBands())
		{
			// awt.238=The number of arrays in the LookupTable does not meet the restrictions
			throw new IllegalArgumentException(Messages.getString("awt.238")); //$NON-NLS-1$
		}

		// TODO
		// if (!canUseIpp || ippFilter(src, dst, BufferedImage.TYPE_CUSTOM, false) != 0)
		if (slowFilter(src, dst, false) != 0)
		{
			// awt.21F=Unable to transform source
			throw new ImagingOpException(Messages.getString("awt.21F")); //$NON-NLS-1$
		}

		return dst;
	}

	public final BufferedImage filter(BufferedImage src, BufferedImage dst)
	{
		ColorModel srcCM = src.getColorModel();

		// Check if the number of scaling factors matches the number of bands
		int nComponents = srcCM.getNumComponents();
		int nLUTComponents = lut.getNumComponents();
		boolean skipAlpha;
		if (srcCM.hasAlpha())
		{
			if (nLUTComponents == 1 || nLUTComponents == nComponents - 1)
			{
				skipAlpha = true;
			}
			else if (nLUTComponents == nComponents)
			{
				skipAlpha = false;
			}
			else
			{
				// awt.229=Number of components in the LUT does not match the number of bands
				throw new IllegalArgumentException(
						Messages.getString("awt.229")); //$NON-NLS-1$
			}
		}
		else if (nLUTComponents == 1 || nLUTComponents == nComponents)
		{
			skipAlpha = false;
		}
		else
		{
			// awt.229=Number of components in the LUT does not match the number of bands
			throw new IllegalArgumentException(Messages.getString("awt.229")); //$NON-NLS-1$
		}

		BufferedImage finalDst = null;
		if (dst == null)
		{
			dst = createCompatibleDestImage(src, null);
		}
		else
		{
			if (src.getWidth() != dst.getWidth())
			{
				throw new IllegalArgumentException(
						Messages.getString("awt.291")); //$NON-NLS-1$
			}

			if (src.getHeight() != dst.getHeight())
			{
				throw new IllegalArgumentException(
						Messages.getString("awt.292")); //$NON-NLS-1$
			}

			if (!srcCM.equals(dst.getColorModel()))
			{
				// Treat BufferedImage.TYPE_INT_RGB and
				// BufferedImage.TYPE_INT_ARGB as same
				if (!((src.getType() == BufferedImage.TYPE_INT_RGB || src
						.getType() == BufferedImage.TYPE_INT_ARGB) && (dst
						.getType() == BufferedImage.TYPE_INT_RGB || dst
						.getType() == BufferedImage.TYPE_INT_ARGB)))
				{
					finalDst = dst;
					dst = createCompatibleDestImage(src, null);
				}
			}
		}

		// TODO
		//if (!canUseIpp || ippFilter(src.getRaster(), dst.getRaster(), src.getType(), skipAlpha) != 0)
		if (slowFilter(src.getRaster(), dst.getRaster(), skipAlpha) != 0)
		{
			// awt.21F=Unable to transform source
			throw new ImagingOpException(Messages.getString("awt.21F")); //$NON-NLS-1$
		}

		if (finalDst != null)
		{
			Graphics2D g = finalDst.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(dst, 0, 0, null);
		}
		else
		{
			finalDst = dst;
		}

		return dst;
	}

	private final int slowFilter(Raster src, WritableRaster dst,
			boolean skipAlpha)
	{
		int minSrcX = src.getMinX();
		int minDstX = dst.getMinX();
		int minSrcY = src.getMinY();
		int minDstY = dst.getMinY();

		int skippingChannels = skipAlpha ? 1 : 0;
		int numBands2Process = src.getNumBands() - skippingChannels;

		int numBands = src.getNumBands();
		int srcHeight = src.getHeight();
		int srcWidth = src.getWidth();

		int[] pixels = null;
		int offset = lut.getOffset();

		if (lut instanceof ByteLookupTable)
		{
			byte[][] byteData = ((ByteLookupTable) lut).getTable();
			pixels = src.getPixels(minSrcX, minSrcY, srcWidth, srcHeight,
					pixels);

			if (lut.getNumComponents() != 1)
			{
				for (int i = 0; i < pixels.length; i += numBands)
				{
					for (int b = 0; b < numBands2Process; b++)
					{
						pixels[i + b] = byteData[b][pixels[i + b] - offset] & 0xFF;
					}
				}
			}
			else
			{
				for (int i = 0; i < pixels.length; i += numBands)
				{
					for (int b = 0; b < numBands2Process; b++)
					{
						pixels[i + b] = byteData[0][pixels[i + b] - offset] & 0xFF;
					}
				}
			}

			dst.setPixels(minDstX, minDstY, srcWidth, srcHeight, pixels);
		}
		else
		{
			int pixel[] = new int[src.getNumBands()];
			int maxY = minSrcY + srcHeight;
			int maxX = minSrcX + srcWidth;
			for (int srcY = minSrcY, dstY = minDstY; srcY < maxY; srcY++, dstY++)
			{
				for (int srcX = minSrcX, dstX = minDstX; srcX < maxX; srcX++, dstX++)
				{
					src.getPixel(srcX, srcY, pixel);
					lut.lookupPixel(pixel, pixel);
					dst.setPixel(dstX, dstY, pixel);
				}
			}
		}

		return 0;
	}

	private final void createByteLevels(int channels, boolean skipAlpha,
			int levels[], int values[], int channelsOrder[])
	{
		byte data[][] = ((ByteLookupTable) lut).getTable();
		int nLevels = data[0].length;
		int offset = lut.getOffset();

		// Use one data array for all channels or use several data arrays
		int dataIncrement = data.length > 1 ? 1 : 0;

		for (int ch = 0, dataIdx = 0; ch < channels; dataIdx += dataIncrement, ch++)
		{
			int channelOffset = channelsOrder == null ? ch : channelsOrder[ch];
			int channelBase = nLevels * channelOffset;

			// Skip last channel if needed, zero values are OK -
			// no changes to the channel information will be done in IPP
			if ((channelOffset == channels - 1 && skipAlpha)
					|| (dataIdx >= data.length))
			{
				continue;
			}

			System.arraycopy(levelInitializer, offset, levels, channelBase,
					nLevels);
			for (int from = 0, to = channelBase; from < nLevels; from++, to++)
			{
				values[to] = data[dataIdx][from] & 0xFF;
			}
		}
	}

	private final int ippFilter(Raster src, WritableRaster dst, int imageType,
			boolean skipAlpha)
	{
		int res;

		int srcStride, dstStride;
		int channels;
		int offsets[] = null;
		int channelsOrder[] = null;

		channels = 4;
		srcStride = src.getWidth() * 4;
		dstStride = dst.getWidth() * 4;
		channelsOrder = new int[] { 2, 1, 0, 3 };

		int levels[] = null, values[] = null;
		int channelMultiplier = skipAlpha ? -1 : 1;
		if (channelMultiplier * channels == validForChannels)
		{ // use existing levels/values
			levels = cachedLevels;
			values = cachedValues;
		}
		else
		{ // create new levels/values
			if (lut instanceof ByteLookupTable)
			{
				byte data[][] = ((ByteLookupTable) lut).getTable();
				levels = new int[channels * data[0].length];
				values = new int[channels * data[0].length];
				createByteLevels(channels, skipAlpha, levels, values,
						channelsOrder);
			}

			// cache levels/values
			validForChannels = channelMultiplier * channels;
			cachedLevels = levels;
			cachedValues = values;
		}

		int[] srcData = src.getDataBuffer();
		int[] dstData = dst.getDataBuffer();

		res = ippLUT(srcData, src.getWidth(), src.getHeight(), srcStride,
				dstData, dst.getWidth(), dst.getHeight(), dstStride, levels,
				values, channels, offsets, false);

		return res;
	}

	final static native int ippLUT(Object src, int srcWidth, int srcHeight,
			int srcStride, Object dst, int dstWidth, int dstHeight,
			int dstStride, int levels[], int values[], int channels,
			int offsets[], boolean linear);
}
