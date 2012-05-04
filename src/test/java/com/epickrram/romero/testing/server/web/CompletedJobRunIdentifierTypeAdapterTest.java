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
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class CompletedJobRunIdentifierTypeAdapterTest
{
    private static final String JOB_RUN_ID = "job-run-id";
    private static final long START_TIMESTAMP = 12987398345234L;
    private CompletedJobRunIdentifierTypeAdapter typeAdapter;

    @Before
    public void setUp() throws Exception
    {
        typeAdapter = new CompletedJobRunIdentifierTypeAdapter();
    }

    @Test
    public void shouldDeserialise() throws Exception
    {
        final Map<String, String> map = new HashMap<>();
        map.put("jobRunIdentifier", JOB_RUN_ID);
        map.put("startTimestamp", Long.toString(START_TIMESTAMP));
        final String json = new Gson().toJson(map);

        final CompletedJobRunIdentifier identifier = typeAdapter.read(getReader(json));

        assertThat(identifier.getJobRunIdentifier(), is(JOB_RUN_ID));
        assertThat(identifier.getStartTimestamp(), is(START_TIMESTAMP));
    }

    private JsonReader getReader(final String json)
    {
        return new JsonReader(new StringReader(json));
    }
}
