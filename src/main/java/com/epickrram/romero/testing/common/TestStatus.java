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

package com.epickrram.romero.testing.common;

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.protocol.AbstractTranslator;
import com.epickrram.freewheel.protocol.Translatable;
import com.epickrram.romero.common.EnumTranslator;

import java.io.IOException;

@Translatable(codeBookId = 2001)
public enum TestStatus
{
    SUCCESS,
    FAILURE,
    ERROR,
    TIMED_OUT,
    IGNORED;

    public static final class Translator extends AbstractTranslator<TestStatus>
    {
        private final AbstractTranslator<TestStatus> delegate = new EnumTranslator<>(TestStatus.class);

        @Override
        protected void doEncode(final TestStatus encodable, final EncoderStream encoderStream) throws IOException
        {
            delegate.encode(encodable, encoderStream);
        }

        @Override
        protected TestStatus doDecode(final DecoderStream decoderStream) throws IOException
        {
            return delegate.decode(decoderStream);
        }
    }
}