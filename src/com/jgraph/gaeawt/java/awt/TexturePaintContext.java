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
 * @author Denis M. Kishenko
 */
package com.jgraph.gaeawt.java.awt;

import com.jgraph.gaeawt.java.awt.geom.AffineTransform;
import com.jgraph.gaeawt.java.awt.geom.Point2D;
import com.jgraph.gaeawt.java.awt.geom.Rectangle2D;
import com.jgraph.gaeawt.java.awt.image.BufferedImage;
import com.jgraph.gaeawt.java.awt.image.ColorModel;
import com.jgraph.gaeawt.java.awt.image.Raster;
import com.jgraph.gaeawt.java.awt.image.WritableRaster;

class TexturePaintContext implements PaintContext
{

	/**
	 * The ColorModel object of destination raster
	 */
	ColorModel cm;

	/**
	 * The BufferedImage object used as texture  
	 */
	BufferedImage img;

	/**
	 * The Rectangle2D bounds of texture piece to be painted  
	 */
	Rectangle2D anchor;

	/**
	 * The paint transformation
	 */
	AffineTransform t;

	/**
	 * The source DataBuffer object of texture image
	 */
	int[] srcBuf;

	/**
	 * The destination DataBuffer object of output rester
	 */
	int[] dstBuf;

	/**
	 * The source WritableRaster object of texture image
	 */
	WritableRaster srcRaster;

	/**
	 * The destination WritableRaster object of texture image
	 */
	WritableRaster dstRaster;

	/**
	 * The width of the texture image
	 */
	int srcWidth;

	/**
	 * The height of the texture image
	 */
	int srcHeight;

	/**
	 * The temporary pre-calculated temporary values
	 */
	int sx, sy, hx, hy, vx, vy;

	int m00, m01, m10, m11;

	int imgW, imgH;

	int px, py;

	/**
	 * The integer array of weight components for bilinear interpolation
	 */
	int[] weight = new int[4];

	/**
	 * The temporary values  
	 */
	int[] value = new int[4];

	static class IntSimple extends TexturePaintContext
	{

		/**
		 * Constructs a new IntSimple.TexturePaintContext works with DataBufferInt rasters.
		 * This is simple paint context uses NEAREST NEIGHBOUR interpolation.   
		 * @param img - the BufferedImage object used as texture
		 * @param anchor - the Rectangle2D bounds of texture piece to be painted
		 * @param t - the AffineTransform applied to texture painting
		 */
		public IntSimple(BufferedImage img, Rectangle2D anchor,
				AffineTransform t)
		{
			super(img, anchor, t);
		}

		@Override
		public Raster getRaster(int dstX, int dstY, int dstWidth, int dstHeight)
		{
			prepare(dstX, dstY, dstWidth, dstHeight);
			int k = 0;
			for (int j = 0; j < dstHeight; j++)
			{
				for (int i = 0; i < dstWidth; i++)
				{
					dstBuf[k++] = srcBuf[(sx >> 8) + (sy >> 8) * srcWidth];
					sx = check(sx + hx, imgW);
					sy = check(sy + hy, imgH);
				}
				sx = check(sx + vx, imgW);
				sy = check(sy + vy, imgH);
			}
			return dstRaster;
		}

	}

	static class CommonSimple extends TexturePaintContext
	{

		/**
		 * Constructs a new CommonSimple.TexturePaintContext works with any raster type. 
		 * This is simple paint context uses NEAREST NEIGHBOUR interpolation.   
		 * @param img - the BufferedImage object used as texture
		 * @param anchor - the Rectangle2D bounds of texture piece to be painted
		 * @param t - the AffineTransform applied to texture painting
		 */
		public CommonSimple(BufferedImage img, Rectangle2D anchor,
				AffineTransform t)
		{
			super(img, anchor, t);
		}

		@Override
		public Raster getRaster(int dstX, int dstY, int dstWidth, int dstHeight)
		{
			prepare(dstX, dstY, dstWidth, dstHeight);
			for (int j = 0; j < dstHeight; j++)
			{
				for (int i = 0; i < dstWidth; i++)
				{
					dstRaster.setDataElements(dstX + i, dstY + j,
							srcRaster.getDataElements(sx >> 8, sy >> 8));
					sx = check(sx + hx, imgW);
					sy = check(sy + hy, imgH);
				}
				sx = check(sx + vx, imgW);
				sy = check(sy + vy, imgH);
			}
			return dstRaster;
		}

	}

	static class IntBilinear extends TexturePaintContext
	{

		/**
		 * Constructs a new IntSimple.TexturePaintContext works with DataBufferInt rasters. 
		 * This paint context uses BILINEAR interpolation.   
		 * @param img - the BufferedImage object used as texture
		 * @param anchor - the Rectangle2D bounds of texture piece to be painted
		 * @param t - the AffineTransform applied to texture painting
		 */
		public IntBilinear(BufferedImage img, Rectangle2D anchor,
				AffineTransform t)
		{
			super(img, anchor, t);
		}

		@Override
		public Raster getRaster(int dstX, int dstY, int dstWidth, int dstHeight)
		{
			prepare(dstX, dstY, dstWidth, dstHeight);
			int k = 0;
			for (int j = 0; j < dstHeight; j++)
			{
				for (int i = 0; i < dstWidth; i++)
				{
					int wx1 = sx & 0xFF;
					int wy1 = sy & 0xFF;
					int wx0 = 0xFF - wx1;
					int wy0 = 0xFF - wy1;

					weight[0] = wx0 * wy0;
					weight[1] = wx1 * wy0;
					weight[2] = wx0 * wy1;
					weight[3] = wx1 * wy1;

					int x0 = sx >> 8;
					int y0 = sy >> 8;
					int x1 = check(x0 + 1, srcWidth);
					int y1 = check(y0 + 1, srcHeight);

					y0 *= srcWidth;
					y1 *= srcWidth;

					value[0] = srcBuf[x0 + y0];
					value[1] = srcBuf[x1 + y0];
					value[2] = srcBuf[x0 + y1];
					value[3] = srcBuf[x1 + y1];

					int color = 0;
					for (int n = 0; n < 32; n += 8)
					{
						int comp = 0;
						for (int m = 0; m < 4; m++)
						{
							comp += ((value[m] >> n) & 0xFF) * weight[m];
						}
						color |= (comp >> 16) << n;
					}

					dstBuf[k++] = color;

					sx = check(sx + hx, imgW);
					sy = check(sy + hy, imgH);
				}
				sx = check(sx + vx, imgW);
				sy = check(sy + vy, imgH);
			}
			return dstRaster;
		}

	}

	static class CommonBilinear extends TexturePaintContext
	{

		/**
		 * Constructs a new CommonSimple.TexturePaintContext works with any raster type. 
		 * This paint context uses BILINEAR interpolation.   
		 * @param img - the BufferedImage object used as texture
		 * @param anchor - the Rectangle2D bounds of texture piece to be painted
		 * @param t - the AffineTransform applied to texture painting
		 */
		public CommonBilinear(BufferedImage img, Rectangle2D anchor,
				AffineTransform t)
		{
			super(img, anchor, t);
		}

		@Override
		public Raster getRaster(int dstX, int dstY, int dstWidth, int dstHeight)
		{
			prepare(dstX, dstY, dstWidth, dstHeight);
			for (int j = 0; j < dstHeight; j++)
			{
				for (int i = 0; i < dstWidth; i++)
				{
					int wx1 = sx & 0xFF;
					int wy1 = sy & 0xFF;
					int wx0 = 0xFF - wx1;
					int wy0 = 0xFF - wy1;

					weight[0] = wx0 * wy0;
					weight[1] = wx1 * wy0;
					weight[2] = wx0 * wy1;
					weight[3] = wx1 * wy1;

					int x0 = sx >> 8;
					int y0 = sy >> 8;
					int x1 = check(x0 + 1, srcWidth);
					int y1 = check(y0 + 1, srcHeight);

					value[0] = srcRaster.getDataElements(x0, y0);
					value[1] = srcRaster.getDataElements(x1, y0);
					value[2] = srcRaster.getDataElements(x0, y1);
					value[3] = srcRaster.getDataElements(x1, y1);

					int color = 0;
					for (int n = 0; n < 32; n += 8)
					{
						int comp = 0;
						for (int m = 0; m < 4; m++)
						{
							comp += ((value[m] >> n) & 0xFF) * weight[m];
						}
						color |= (comp >> 16) << n;
					}
					dstRaster.setDataElements(dstX + i, dstY + j, color);

					sx = check(sx + hx, imgW);
					sy = check(sy + hy, imgH);
				}
				sx = check(sx + vx, imgW);
				sy = check(sy + vy, imgH);
			}
			return dstRaster;
		}

	}

	public TexturePaintContext(BufferedImage img, Rectangle2D anchor,
			AffineTransform t)
	{
		this.cm = img.getColorModel();
		this.img = img;
		this.anchor = anchor;
		this.t = t;

		srcWidth = img.getWidth();
		srcHeight = img.getHeight();
		imgW = srcWidth << 8;
		imgH = srcHeight << 8;
		double det = t.getDeterminant();
		double multW = imgW / (anchor.getWidth() * det);
		double multH = -imgH / (anchor.getHeight() * det);

		m11 = (int) (t.getScaleY() * multW);
		m01 = (int) (t.getShearX() * multW);
		m00 = (int) (t.getScaleX() * multH);
		m10 = (int) (t.getShearY() * multH);
		Point2D p = t.transform(
				new Point2D.Double(anchor.getX(), anchor.getY()), null);
		px = (int) p.getX();
		py = (int) p.getY();

		hx = check2(m11, imgW);
		hy = check2(m10, imgH);

		srcRaster = img.getRaster();
		srcBuf = srcRaster.getDataBuffer();
	}

	/**
	 * Prepares pre-calculated values  
	 */
	void prepare(int dstX, int dstY, int dstWidth, int dstHeight)
	{
		vx = check2(-m01 - m11 * dstWidth, imgW);
		vy = check2(-m00 - m10 * dstWidth, imgH);
		int dx = dstX - px;
		int dy = dstY - py;
		sx = check2(dx * m11 - dy * m01, imgW);
		sy = check2(dx * m10 - dy * m00, imgH);
		dstRaster = cm.createCompatibleWritableRaster(dstWidth, dstHeight);
		dstBuf = dstRaster.getDataBuffer();
	}

	public void dispose()
	{
	}

	public ColorModel getColorModel()
	{
		return cm;
	}

	/**
	 * Checks point overrun of texture anchor 
	 */
	int check(int value, int max)
	{
		if (value >= max)
		{
			return value - max;
		}
		return value;
	}

	/**
	 * Checks point overrun of texture anchor 
	 */
	int check2(int value, int max)
	{
		value = value % max;
		return value < 0 ? max + value : value;
	}

	public Raster getRaster(int dstX, int dstY, int dstWidth, int dstHeight)
	{
		return dstRaster;
	}

}
