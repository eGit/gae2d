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
 * @author Pavel Dolgov, Anton Avtamonov
 */
package org.apache.harmony.awt;

import org.apache.harmony.awt.gl.MultiRectArea;

import com.jgraph.gaeawt.java.awt.Rectangle;

public class ClipRegion extends Rectangle {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3075461419585512220L;
	private final MultiRectArea clip;

    public ClipRegion(final MultiRectArea clip) {
        this.clip = new MultiRectArea(clip);
        setBounds(clip.getBounds());
    }

    public MultiRectArea getClip() {
        return clip;
    }

    @Override
    public String toString() {
        String str = clip.toString();
        int i = str.indexOf('[');
        str = str.substring(i);
        if (clip.getRectCount() == 1) {
            str = str.substring(1, str.length() - 1);
        }
        return getClass().getName() + str;
    }


    public void intersect(final Rectangle rect) {
        clip.intersect(rect);
    }

    @Override
    public boolean isEmpty() {
        return clip.isEmpty();
    }
}
