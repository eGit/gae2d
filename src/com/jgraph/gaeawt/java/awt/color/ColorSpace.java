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
 */
package com.jgraph.gaeawt.java.awt.color;

import java.io.Serializable;

import org.apache.harmony.awt.internal.nls.Messages;

public abstract class ColorSpace implements Serializable
{

	private static final long serialVersionUID = -409452704308689724L;

	private static ColorSpace cs_LRGB = null;

	protected ColorSpace()
	{
	}

	public String getName(int idx)
	{
		if (idx < 0 || idx > 2)
		{
			// awt.16A=Invalid component index: {0}
			throw new IllegalArgumentException(Messages.getString(
					"awt.16A", idx)); //$NON-NLS-1$
		}

		return "Unnamed color component #" + idx; //$NON-NLS-1$
	}

	public float getMinValue(int component)
	{
		if (component < 0 || component > 2)
		{
			// awt.16A=Invalid component index: {0}
			throw new IllegalArgumentException(Messages.getString(
					"awt.16A", component)); //$NON-NLS-1$
		}
		return 0;
	}

	public float getMaxValue(int component)
	{
		if (component < 0 || component > 2)
		{
			// awt.16A=Invalid component index: {0}
			throw new IllegalArgumentException(Messages.getString(
					"awt.16A", component)); //$NON-NLS-1$
		}
		return 1;
	}

	public static ColorSpace getInstance()
	{
		if (cs_LRGB == null)
		{
			cs_LRGB = new ICC_ColorSpace(new ICC_ProfileStub());
		}

		return cs_LRGB;
	}
}