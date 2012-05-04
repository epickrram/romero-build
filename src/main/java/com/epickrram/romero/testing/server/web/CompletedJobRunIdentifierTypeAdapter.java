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

package com.epickrram.romero.testing.server.web;

import com.epickrram.romero.server.CompletedJobRunIdentifier;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

final class CompletedJobRunIdentifierTypeAdapter extends TypeAdapter<CompletedJobRunIdentifier>
{
    @Override
    public void write(final JsonWriter jsonWriter,
                      final CompletedJobRunIdentifier completedJobRunIdentifier) throws IOException
    {
        throw new IllegalStateException("This TypeAdapter cannot be used for writing " + completedJobRunIdentifier);
    }

    @Override
    public CompletedJobRunIdentifier read(final JsonReader jsonReader) throws IOException
    {
        jsonReader.beginObject();
        final CompletedJobRunIdentifier.Builder builder = new CompletedJobRunIdentifier.Builder();
        while(jsonReader.hasNext())
        {
            final String name = jsonReader.nextName();
            if("jobRunIdentifier".equals(name))
            {
                builder.jobRunIdentifier(jsonReader.nextString());
            }
            else if("startTimestamp".equals(name))
            {
                builder.startTimestamp(jsonReader.nextLong());
            }
        }
        jsonReader.endObject();
        return builder.create();
    }
}
