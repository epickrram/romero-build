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

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static com.epickrram.romero.common.proxy.SerialisationTestFixture.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public final class DeserialiserTest
{
    private Deserialiser deserialiser;

    @Before
    public void setUp() throws Exception
    {
        final Gson gson = new Gson();
        deserialiser = new Deserialiser(gson);
    }

    @Test
    public void shouldDeserialiseMethodCall() throws Exception
    {
        final StringReader reader = new StringReader(SERIALISED_METHOD_REQUEST);
        final MethodRequest methodRequest = deserialiser.readMethodRequest(reader);

        assertThat(methodRequest.getClassName(), is(CLASS_NAME));
        assertThat(methodRequest.getMethodName(), is(METHOD_NAME));
        assertThat(methodRequest.getArguments(), equalTo(METHOD_ARGS));
    }

    @Test
    public void shouldDeserialiseMethodResponse() throws Exception
    {
        final StringReader reader = new StringReader(SERIALISED_METHOD_RESPONSE);
        final MethodResponse methodResponse = deserialiser.readMethodResponse(reader);

        assertThat(methodResponse.getExceptionMessage(), is(nullValue()));
        assertThat(methodResponse.getResult(), equalTo(METHOD_ARGS[0]));
    }
}
