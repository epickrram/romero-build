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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class IoUtil
{
    public static String readClasspathResource(final String resource) throws IOException
    {
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if(inputStream == null)
        {
            throw new IllegalArgumentException("Cannot find classpath resource: " + resource);
        }

        final StringBuilder buffer = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                buffer.append(line).append('\n');
            }
        }
        finally
        {
            close(reader);
        }
        return buffer.toString();
    }

    public static void close(final Closeable closeable)
    {
        if(closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }
    }
}
