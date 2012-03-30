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
import com.epickrram.freewheel.protocol.Translatable;

import java.io.IOException;

@Translatable(codeBookId = 2005)
public enum BuildStatus
{
    WAITING_FOR_NEXT_BUILD,
    BUILDING,
    WAITING_FOR_TESTS_TO_COMPLETE;

    public static final class Translator extends AbstractTranslator<BuildStatus>
    {
        private final AbstractTranslator<BuildStatus> delegate = new EnumTranslator<>(BuildStatus.class);

        @Override
        protected void doEncode(final BuildStatus encodable, final EncoderStream encoderStream) throws IOException
        {
            delegate.encode(encodable, encoderStream);
        }

        @Override
        protected BuildStatus doDecode(final DecoderStream decoderStream) throws IOException
        {
            return delegate.decode(decoderStream);
        }
    }
}