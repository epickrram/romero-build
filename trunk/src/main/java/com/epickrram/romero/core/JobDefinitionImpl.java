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

package com.epickrram.romero.core;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.protocol.AbstractTranslator;
import com.epickrram.freewheel.protocol.Translatable;

import java.io.IOException;

@Translatable(codeBookId = 5000)
public final class JobDefinitionImpl<K, D> implements JobDefinition<K, D>
{
    private final K key;
    private final D data;

    public JobDefinitionImpl(final K key, final D data)
    {
        this.key = key;
        this.data = data;
    }

    @Override
    public K getKey()
    {
        return key;
    }

    @Override
    public D getData()
    {
        return data;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final JobDefinitionImpl that = (JobDefinitionImpl) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    public static final class Translator<K, D> extends AbstractTranslator<JobDefinitionImpl<K, D>>
    {
        @Override
        protected void doEncode(final JobDefinitionImpl<K, D> encodable, final EncoderStream encoderStream) throws IOException
        {
            encoderStream.writeObject(encodable.key);
            encoderStream.writeObject(encodable.data);
        }

        @Override
        protected JobDefinitionImpl<K, D> doDecode(final DecoderStream decoderStream) throws IOException
        {
            final K key = decoderStream.readObject();
            final D data = decoderStream.readObject();

            return new JobDefinitionImpl<>(key, data);
        }
    }
}
