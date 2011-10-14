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

package org.apache.harmony.awt.gl.image;

import java.util.Hashtable;

import com.jgraph.gaeawt.java.awt.image.BufferedImage;
import com.jgraph.gaeawt.java.awt.image.ColorModel;
import com.jgraph.gaeawt.java.awt.image.ImageConsumer;
import com.jgraph.gaeawt.java.awt.image.ImageProducer;
import com.jgraph.gaeawt.java.awt.image.WritableRaster;

public class BufferedImageSource implements ImageProducer
{

	private Hashtable<?, ?> properties;

	private ColorModel cm;

	private WritableRaster raster;

	private int width;

	private int height;

	private ImageConsumer ic;

	public BufferedImageSource(BufferedImage image, Hashtable<?, ?> properties)
	{
		if (properties == null)
		{
			this.properties = new Hashtable<Object, Object>();
		}
		else
		{
			this.properties = properties;
		}

		width = image.getWidth();
		height = image.getHeight();
		cm = image.getColorModel();
		raster = image.getRaster();
	}

	public BufferedImageSource(BufferedImage image)
	{
		this(image, null);
	}

	public boolean isConsumer(ImageConsumer ic)
	{
		return (this.ic == ic);
	}

	public void startProduction(ImageConsumer ic)
	{
		addConsumer(ic);
	}

	public void requestTopDownLeftRightResend(ImageConsumer ic)
	{
	}

	public void removeConsumer(ImageConsumer ic)
	{
		if (this.ic == ic)
		{
			this.ic = null;
		}
	}

	public void addConsumer(ImageConsumer ic)
	{
		this.ic = ic;
		startProduction();
	}

	private void startProduction()
	{
		try
		{
			ic.setDimensions(width, height);
			ic.setProperties(properties);
			ic.setColorModel(cm);
			ic.setHints(ImageConsumer.TOPDOWNLEFTRIGHT
					| ImageConsumer.COMPLETESCANLINES
					| ImageConsumer.SINGLEFRAME | ImageConsumer.SINGLEPASS);
			int data[] = raster.getDataBuffer();
			ic.setPixels(0, 0, width, height, cm, data, 0, width);
			ic.imageComplete(ImageConsumer.STATICIMAGEDONE);
		}
		catch (NullPointerException e)
		{
			if (ic != null)
			{
				ic.imageComplete(ImageConsumer.IMAGEERROR);
			}
		}
	}

}
