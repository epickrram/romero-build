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

import com.epickrram.romero.common.RunningJob;
import com.epickrram.romero.common.TestSuiteIdentifier;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class TestSuiteIdentifierRunningJobTypeAdapter extends TypeAdapter<RunningJob<TestSuiteIdentifier>>
{
    @Override
    public void write(final JsonWriter jsonWriter, final RunningJob<TestSuiteIdentifier> runningJob) throws IOException
    {
        jsonWriter.beginObject();
        jsonWriter.name("testSuite");
        jsonWriter.value(runningJob.getJobKey().getTestClass());
        jsonWriter.name("agentId");
        jsonWriter.value(runningJob.getAgentId());
        jsonWriter.name("startedAt");
        jsonWriter.value(runningJob.getStartedTimestamp());
        jsonWriter.endObject();
    }

    @Override
    public RunningJob<TestSuiteIdentifier> read(final JsonReader jsonReader) throws IOException
    {
        throw new IllegalStateException("For writing only");
    }
}
