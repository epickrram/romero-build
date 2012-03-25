//////////////////////////////////////////////////////////////////////////////////
//   Copyright 2011   Mark Price     mark at epickrram.com                      //
//                                                                              //
//   Licensed under the Apache License, Version 2.0 (the "License");            //
//   you may not use this file except in compliance with the License.           //
//   You may obtain a copy of the License at                                    //
//                                                                              //
//       http://www.apache.org/licenses/LICENSE-2.0                             //
//                                                                              //
//   Unless required by applicable law or agreed to in writing, software        //
//   distributed under the License is distributed on an "AS IS" BASIS,          //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   //
//   See the License for the specific language governing permissions and        //
//   limitations under the License.                                             //
//////////////////////////////////////////////////////////////////////////////////

package com.epickrram.romero.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public final class UrlLoaderImpl implements UrlLoader
{
    @Override
    public InputStream openUrlStream(final String url) throws IOException
    {
        final URLConnection urlConnection = new URL(url).openConnection();
        return urlConnection.getInputStream();
    }

    @Override
    public InputStream openUrlStream(final URL url) throws IOException
    {
        return url.openConnection().getInputStream();
    }

    @Override
    public File downloadUrl(final String url, final boolean deleteOnExit) throws IOException
    {
        final File tmpFile = File.createTempFile("romero-", ".tmp");
        if(deleteOnExit)
        {
            tmpFile.deleteOnExit();
        }

        final InputStream inputStream = openUrlStream(url);
        final FileOutputStream outputStream = new FileOutputStream(tmpFile, false);
        copy(inputStream, outputStream);
        outputStream.flush();
        close(inputStream);
        close(outputStream);

        return tmpFile;
    }

    private static void copy(final InputStream inputStream, final OutputStream outputStream) throws IOException
    {
        final BufferedInputStream in = new BufferedInputStream(inputStream);
        final BufferedOutputStream out = new BufferedOutputStream(outputStream);

        int read;
        while((read = in.read()) != -1)
        {
            out.write(read);
        }

        out.flush();
    }

    private static void close(final Closeable closeable)
    {
        if(closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch(IOException e)
            {
                // ignore
            }
        }
    }
}
