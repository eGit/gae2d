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
package org.apache.sanselan.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.sanselan.SanselanConstants;

public class IOUtils implements SanselanConstants
{
    /**
     * This class should never be instantiated.
     */
    private IOUtils()
    {
    }

    /**
     * Reads an InputStream to the end.
     * <p>
     *
     * @param is
     *            The InputStream to read.
     * @return A byte array containing the contents of the InputStream
     * @see InputStream
     */
    public static byte[] getInputStreamBytes(InputStream is) throws IOException
    {
        ByteArrayOutputStream os = null;

        try
        {
            os = new ByteArrayOutputStream(4096);

            is = new BufferedInputStream(is);

            int count;
            byte[] buffer = new byte[4096];
            while ((count = is.read(buffer, 0, 4096)) > 0)
            {
                os.write(buffer, 0, count);
            }

            os.flush();

            return os.toByteArray();
        } finally
        {
            try
            {
                if (os != null)
                    os.close();
            } catch (IOException e)
            {
                Debug.debug(e);
            }
        }
    }

    /**
     * Reads a File into memory.
     * <p>
     *
     * @param file
     *            The File to read.
     * @return A byte array containing the contents of the File
     * @see InputStream
     */
    public static byte[] getFileBytes(File file) throws IOException
    {
        InputStream is = null;

        try
        {
            is = new FileInputStream(file);

            return getInputStreamBytes(is);
        } finally
        {
            try
            {
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
                Debug.debug(e);
            }
        }
    }

    public static void copyStreamToStream(InputStream src, OutputStream dst)
            throws IOException
    {
        copyStreamToStream(src, dst, true);
    }

    public static void copyStreamToStream(InputStream src, OutputStream dst,
            boolean close_streams) throws IOException
    {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try
        {
            bis = new BufferedInputStream(src);
            bos = new BufferedOutputStream(dst);

            int count;
            byte[] buffer = new byte[4096];
            while ((count = bis.read(buffer, 0, buffer.length)) > 0)
                dst.write(buffer, 0, count);

            bos.flush();
        } finally
        {
            if (close_streams)
            {
                try
                {
                    if (bis != null)
                        bis.close();
                } catch (IOException e)
                {
                    Debug.debug(e);
                }
                try
                {
                    if (bos != null)
                        bos.close();
                } catch (IOException e)
                {
                    Debug.debug(e);
                }
            }
        }

    }
}