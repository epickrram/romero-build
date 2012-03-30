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

package com.epickrram.romero;

import com.epickrram.freewheel.io.PackerEncoderStream;
import com.epickrram.freewheel.io.UnpackerDecoderStream;
import com.epickrram.freewheel.protocol.CodeBookImpl;
import com.epickrram.freewheel.protocol.Translator;
import org.hamcrest.Matcher;
import org.msgpack.packer.MessagePackPacker;
import org.msgpack.unpacker.MessagePackUnpacker;
import org.msgpack.util.json.JSONPacker;
import org.msgpack.util.json.JSONUnpacker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class TranslatorAssert
{
    public static <T> void assertTranslation(final T encodable, final Matcher<T> matcher)
    {
        final CodeBookImpl codeBook = new CodeBookImpl();
        final CodeBookImpl.CodeBookRegistryImpl registry = new CodeBookImpl.CodeBookRegistryImpl(codeBook);
        final Class<T> translatableClass = (Class<T>) encodable.getClass();
        registry.registerTranslatable(translatableClass);

        final Translator<T> translator = codeBook.getTranslator(translatableClass);

        final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        translator.encode(encodable, new PackerEncoderStream(codeBook, new MessagePackPacker(outBuffer)));
        final T decoded = translator.decode(new UnpackerDecoderStream(codeBook,
                new MessagePackUnpacker(new ByteArrayInputStream(outBuffer.toByteArray()))));

        assertThat(decoded, is(matcher));
    }

    public static <T> void assertTranslation(final T encodable)
    {
        assertTranslation(encodable, equalTo(encodable));
    }

}
