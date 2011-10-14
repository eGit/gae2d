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
/*
 * Created on 30.09.2004
 *
 */
package org.apache.harmony.awt.gl.image;

import com.jgraph.gaeawt.java.awt.Rectangle;
import com.jgraph.gaeawt.java.awt.image.Raster;
import com.jgraph.gaeawt.java.awt.image.SampleModel;
import com.jgraph.gaeawt.java.awt.image.WritableRaster;

public class OrdinaryWritableRaster extends WritableRaster {

    public OrdinaryWritableRaster(SampleModel sampleModel,
            int[] dataBuffer, Rectangle aRegion, WritableRaster parent) {
        super(sampleModel, dataBuffer, aRegion, parent);
    }

    public OrdinaryWritableRaster(SampleModel sampleModel,
            int[] dataBuffer) {
        super(sampleModel, dataBuffer);
    }

    public OrdinaryWritableRaster(SampleModel sampleModel) {
        super(sampleModel);
    }

    @Override
    public void setDataElements(int x, int y, int inData) {
        super.setDataElements(x, y, inData);
    }

    @Override
    public void setDataElements(int x, int y, int w, int h, int[] inData) {
        super.setDataElements(x, y, w, h, inData);
    }

    @Override
    public WritableRaster getWritableParent() {
        return super.getWritableParent();
    }

    @Override
    public void setRect(Raster srcRaster) {
        super.setRect(srcRaster);
    }

    @Override
    public void setRect(int dx, int dy, Raster srcRaster) {
        super.setRect(dx, dy, srcRaster);
    }

    @Override
    public void setDataElements(int x, int y, Raster inRaster) {
        super.setDataElements(x, y, inRaster);
    }

    @Override
    public void setPixel(int x, int y, int[] iArray) {
        super.setPixel(x, y, iArray);
    }

    @Override
    public void setPixels(int x, int y, int w, int h, int[] iArray) {
        super.setPixels(x, y, w, h, iArray);
    }

    @Override
    public void setSamples(int x, int y, int w, int h, int b, int[] iArray) {
        super.setSamples(x, y, w, h, b, iArray);
    }

    @Override
    public void setSample(int x, int y, int b, int s) {
        super.setSample(x, y, b, s);
    }
}