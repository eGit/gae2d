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

import java.io.IOException;
import java.io.InputStream;

final class ICC_ProfileStub extends ICC_Profile {
    private static final long serialVersionUID = 501389760875253507L;

    public ICC_ProfileStub() {
    }

    @Override
    public void setData(int tagSignature, byte[] tagData) {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    @Override
    public byte[] getData(int tagSignature) {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    @Override
    public byte[] getData() {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    @Override
    protected void finalize() {
    }

    @Override
    public int getProfileClass() {
        return CLASS_COLORSPACECONVERSION;
    }

    @Override
    public int getMinorVersion() {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    @Override
    public int getMajorVersion() {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    public static ICC_Profile getInstance(String fileName) throws IOException {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    public static ICC_Profile getInstance(InputStream s) throws IOException {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    public static ICC_Profile getInstance(byte[] data) {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }

    public static ICC_Profile getInstance(int cspace) {
        throw new UnsupportedOperationException("Stub cannot perform this operation"); //$NON-NLS-1$
    }
}