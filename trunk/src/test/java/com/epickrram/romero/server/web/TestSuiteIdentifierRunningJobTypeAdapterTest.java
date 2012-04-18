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
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.io.StringWriter;

import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class TestSuiteIdentifierRunningJobTypeAdapterTest
{
    @Test
    public void shouldSerialise() throws Exception
    {
        final RunningJob<TestSuiteIdentifier> value = new RunningJob<>("agent-1", toMapKey("foo.bar"), 123456L);
        final Gson gson = new GsonBuilder().
                registerTypeAdapter(RunningJob.class, new TestSuiteIdentifierRunningJobTypeAdapter()).
                create();

        final StringWriter writer = new StringWriter();
        gson.toJson(value, writer);

        assertThat(writer.toString(), is("{\"testSuite\":\"foo.bar\",\"agentId\":\"agent-1\",\"startedAt\":123456}"));
    }
}
