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

package com.epickrram.romero.common;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.protocol.AbstractTranslator;

import java.io.IOException;

public final class EnumTranslator<E extends Enum> extends AbstractTranslator<E>
{
    private final Class<E> value;

    public EnumTranslator(final Class<E> value)
    {
        this.value = value;
    }

    @Override
    protected void doEncode(final E encodable, final EncoderStream encoderStream) throws IOException
    {
        encoderStream.writeInt(encodable.ordinal());
    }

    @Override
    protected E doDecode(final DecoderStream decoderStream) throws IOException
    {
        final int ordinal = decoderStream.readInt();
        return value.getEnumConstants()[ordinal];
    }
}
