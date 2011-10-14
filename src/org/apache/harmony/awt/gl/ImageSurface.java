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
 * Created on 10.11.2005
 *
 */
package org.apache.harmony.awt.gl;

import org.apache.harmony.awt.internal.nls.Messages;

import com.jgraph.gaeawt.java.awt.image.ColorModel;
import com.jgraph.gaeawt.java.awt.image.WritableRaster;

/**
 * This class represent Surface for different types of Images (BufferedImage, 
 * OffscreenImage and so on) 
 */
public class ImageSurface extends Surface
{

	boolean nativeDrawable = false;

	int surfaceType;

	int csType;

	ColorModel cm;

	WritableRaster raster;

	int[] data;

	boolean needToRefresh = true;

	boolean dataTaken = false;

	private long cachedDataPtr; // Pointer for cached Image Data

	public ImageSurface(ColorModel cm, WritableRaster raster)
	{
		this(cm, raster, Surface.getType(cm, raster));
	}

	public ImageSurface(ColorModel cm, WritableRaster raster, int type)
	{
		if (!cm.isCompatibleRaster(raster))
		{
			// awt.4D=The raster is incompatible with this ColorModel
			throw new IllegalArgumentException(Messages.getString("awt.4D")); //$NON-NLS-1$
		}
		this.cm = cm;
		this.raster = raster;
		surfaceType = type;

		data = raster.getDataBuffer();
		transparency = cm.getTransparency();
		width = raster.getWidth();
		height = raster.getHeight();

		csType = Linear_RGB_CS;
	}

	@Override
	public ColorModel getColorModel()
	{
		return cm;
	}

	@Override
	public WritableRaster getRaster()
	{
		return raster;
	}

	@Override
	public Object getData()
	{
		return data;
	}

	@Override
	public boolean isNativeDrawable()
	{
		return nativeDrawable;
	}

	@Override
	public int getSurfaceType()
	{
		return surfaceType;
	}

	@Override
	public synchronized void dispose()
	{
		if (surfaceDataPtr != 0L)
		{
			surfaceDataPtr = 0L;
		}
	}

	public long getCachedData(boolean alphaPre)
	{
		return cachedDataPtr;
	}

	/**
	 * Supposes that new raster is compatible with an old one
	 * @param r
	 */
	public void setRaster(WritableRaster r)
	{
		raster = r;
		data = r.getDataBuffer();
		this.width = r.getWidth();
		this.height = r.getHeight();
	}

	@Override
	public long lock()
	{
		// TODO
		return 0;
	}

	@Override
	public void unlock()
	{
		//TODO
	}

	@Override
	public Surface getImageSurface()
	{
		return this;
	}

	public void dataChanged()
	{
		needToRefresh = true;
		clearValidCaches();
	}

	public void dataTaken()
	{
		dataTaken = true;
		needToRefresh = true;
		clearValidCaches();
	}

	public void dataReleased()
	{
		dataTaken = false;
		needToRefresh = true;
		clearValidCaches();
	}

	@Override
	public void invalidate()
	{
		needToRefresh = true;
		clearValidCaches();
	}

	@Override
	public void validate()
	{
		if (!needToRefresh)
		{
			return;
		}
		if (!dataTaken)
		{
			needToRefresh = false;
		}
	}

	@Override
	public boolean invalidated()
	{
		return needToRefresh | dataTaken;
	}
}
