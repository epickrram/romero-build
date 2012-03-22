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

package com.epickrram.romero.server.web;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;

public abstract class RequestHandler<I, O>
{
    private final Class<I> inputTypeClass;

    RequestHandler(final Class<I> inputTypeClass)
    {
        this.inputTypeClass = inputTypeClass;
    }

    void handleRequest(final Reader reader, final Appendable appendable) throws IOException
    {
        final Gson gson = new Gson();
        final I input = isVoidInputType() ? null : gson.fromJson(reader, inputTypeClass);
        final O output = handleRequest(input);
        gson.toJson(output, appendable);
    }

    abstract O handleRequest(final I input);

    private boolean isVoidInputType()
    {
        return inputTypeClass == Void.class;
    }
}