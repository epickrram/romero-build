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

import com.epickrram.romero.common.BuildStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class Serialiser
{
    private final Gson gson;

    public Serialiser()
    {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final TypeAdapterRegistry registry = new TypeAdapterRegistry();
        for(Class<?> cls : registry.getRegisteredTypes())
        {
            gsonBuilder.registerTypeAdapter(cls, registry.getTypeAdapter(cls));
        }
        this.gson = gsonBuilder.create();
    }

    public void writeInto(final Appendable appendable, final MethodRequest request)
    {
        gson.toJson(request, appendable);
    }

    public void writeInto(final Appendable appendable, final MethodResponse response)
    {
        gson.toJson(response, appendable);
    }
}