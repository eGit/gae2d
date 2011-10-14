/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.sanselan;

import com.jgraph.gaeawt.java.awt.color.ColorSpace;
import com.jgraph.gaeawt.java.awt.color.ICC_ColorSpace;
import com.jgraph.gaeawt.java.awt.color.ICC_Profile;
import com.jgraph.gaeawt.java.awt.image.BufferedImage;
import com.jgraph.gaeawt.java.awt.image.ColorModel;
import com.jgraph.gaeawt.java.awt.image.DirectColorModel;
import com.jgraph.gaeawt.java.awt.image.ImagingOpException;

/**
 * This class is a mess and needs to be cleaned up.
 */
public class ColorTools {

    public BufferedImage relabelColorSpace(BufferedImage bi, ICC_Profile profile)
            throws ImagingOpException {
        ICC_ColorSpace cs = new ICC_ColorSpace(profile);

        return relabelColorSpace(bi, cs);
    }

    public BufferedImage relabelColorSpace(BufferedImage bi, ColorSpace cs)
            throws ImagingOpException {
        // This does not do the conversion. It tries to relabel the
        // BufferedImage
        // with its actual (presumably correct) Colorspace.
        // use this when the image is mislabeled, presumably having been
        // wrongly assumed to be sRGB

        ColorModel cm = deriveColorModel(bi, cs);

        return relabelColorSpace(bi, cm);

    }

    public BufferedImage relabelColorSpace(BufferedImage bi, ColorModel cm)
            throws ImagingOpException {
        // This does not do the conversion. It tries to relabel the
        // BufferedImage
        // with its actual (presumably correct) Colorspace.
        // use this when the image is mislabeled, presumably having been
        // wrongly assumed to be sRGB

        BufferedImage result = new BufferedImage(cm, bi.getRaster(), null);

        return result;
    }

    public ColorModel deriveColorModel(BufferedImage bi, ColorSpace cs)
            throws ImagingOpException {
        // boolean hasAlpha = (bi.getAlphaRaster() != null);
        return deriveColorModel(bi, cs, false);
    }

    public ColorModel deriveColorModel(BufferedImage bi, ColorSpace cs,
            boolean force_no_alpha) throws ImagingOpException {
        return deriveColorModel(bi.getColorModel(), cs, force_no_alpha);
    }

    public ColorModel deriveColorModel(ColorModel old_cm, ColorSpace cs,
            boolean force_no_alpha) throws ImagingOpException {

        if (old_cm instanceof DirectColorModel) {
            DirectColorModel dcm = (DirectColorModel) old_cm;

            int old_mask = dcm.getRedMask() | dcm.getGreenMask()
                    | dcm.getBlueMask() | dcm.getAlphaMask();

            int old_bits = count_bits_in_mask(old_mask);

            return new DirectColorModel(cs, old_bits, dcm.getRedMask(), dcm
                    .getGreenMask(), dcm.getBlueMask(), dcm.getAlphaMask());
        }


        throw new ImagingOpException("Could not clone unknown ColorModel Type.");
    }

    private int count_bits_in_mask(int i) {
        int count = 0;
        while (i != 0) {
            count += (i & 1);
            // uses the unsigned version of java's right shift operator,
            // so that left hand bits are zeroed.
            i >>>= 1;
        }
        return count;
    }
}