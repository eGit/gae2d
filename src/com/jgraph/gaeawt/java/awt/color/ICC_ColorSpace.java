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

import org.apache.harmony.awt.internal.nls.Messages;

import java.io.*;

public class ICC_ColorSpace extends ColorSpace
{
	private static final long serialVersionUID = 3455889114070431483L;

	// Need to keep compatibility with serialized form
	private static final ObjectStreamField[] serialPersistentFields = {
			new ObjectStreamField("thisProfile", ICC_Profile.class), //$NON-NLS-1$
			new ObjectStreamField("minVal", float[].class), //$NON-NLS-1$
			new ObjectStreamField("maxVal", float[].class), //$NON-NLS-1$
			new ObjectStreamField("diffMinMax", float[].class), //$NON-NLS-1$
			new ObjectStreamField("invDiffMinMax", float[].class), //$NON-NLS-1$
			new ObjectStreamField("needScaleInit", Boolean.TYPE) //$NON-NLS-1$
	};

	private float minValues[] = null;

	private float maxValues[] = null;

	public ICC_ColorSpace(ICC_Profile pf)
	{
		super();

		int pfClass = pf.getProfileClass();

		switch (pfClass)
		{
			case ICC_Profile.CLASS_COLORSPACECONVERSION:
			case ICC_Profile.CLASS_DISPLAY:
			case ICC_Profile.CLASS_OUTPUT:
			case ICC_Profile.CLASS_INPUT:
				break; // OK, it is color conversion profile
			default:
				// awt.168=Invalid profile class.
				throw new IllegalArgumentException(
						Messages.getString("awt.168")); //$NON-NLS-1$
		}

		fillMinMaxValues();
	}

	@Override
	public float getMinValue(int component)
	{
		if ((component < 0) || (component > 2))
		{
			// awt.169=Component index out of range
			throw new IllegalArgumentException(Messages.getString("awt.169")); //$NON-NLS-1$
		}

		return minValues[component];
	}

	@Override
	public float getMaxValue(int component)
	{
		if ((component < 0) || (component > 2))
		{
			// awt.169=Component index out of range
			throw new IllegalArgumentException(Messages.getString("awt.169")); //$NON-NLS-1$
		}

		return maxValues[component];
	}

	private void fillMinMaxValues()
	{
		int n = 3;
		maxValues = new float[n];
		minValues = new float[n];

		for (int i = 0; i < n; i++)
		{
			minValues[i] = 0;
			maxValues[i] = 1;
		}
	}
}
