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

package com.epickrram.romero.common.proxy;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.protocol.AbstractTranslator;

import java.io.IOException;
import java.util.Properties;

public final class PropertiesTranslator extends AbstractTranslator<Properties>
{
    @Override
    protected void doEncode(final Properties properties, final EncoderStream encoderStream) throws IOException
    {
        encoderStream.writeBoolean(properties == null);
        if(properties != null)
        {
            encoderStream.writeInt(properties.size());
            for(final String key : properties.stringPropertyNames())
            {
                encoderStream.writeString(key);
                encoderStream.writeString(properties.getProperty(key));
            }
        }
    }

    @Override
    protected Properties doDecode(final DecoderStream decoderStream) throws IOException
    {
        final boolean isNull = decoderStream.readBoolean();
        if(isNull)
        {
            return null;
        }
        final int numberOfProperties = decoderStream.readInt();
        final Properties properties = new Properties();
        for(int i = 0; i < numberOfProperties; i++)
        {
            final String key = decoderStream.readString();
            final String value = decoderStream.readString();
            properties.setProperty(key, value);
        }

        return properties;
    }
}
