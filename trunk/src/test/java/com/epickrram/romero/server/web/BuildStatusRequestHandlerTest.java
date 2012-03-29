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

import com.epickrram.romero.TestHelper;
import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.common.TestSuiteIdentifier;
import com.epickrram.romero.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;

import static com.epickrram.romero.TestHelper.runningJob;
import static com.epickrram.romero.common.TestSuiteIdentifier.toMapKey;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class BuildStatusRequestHandlerTest
{
    private static final BuildStatus STATUS = BuildStatus.WAITING_FOR_TESTS_TO_COMPLETE;
    private static final int TOTAL_JOBS = 4564;
    private static final int REMAINING_JOBS = 232;
    private static final String AGENT_1 = "agent1";
    private static final String AGENT_2 = "agent2";
    private static final String TEST_1 = "com.test.1";
    private static final String TEST_2 = "com.test.2";

    @Mock
    private Server server;
    private BuildStatusRequestHandler requestHandler;

    @Test
    public void shouldReturnBuildStatus() throws Exception
    {
        when(server.getStatus()).thenReturn(STATUS);
        when(server.getTotalJobs()).thenReturn(TOTAL_JOBS);
        when(server.getRemainingJobs()).thenReturn(REMAINING_JOBS);
        when(server.getRunningJobs()).thenReturn(
                asList(runningJob(AGENT_1, toMapKey(TEST_1)), runningJob(AGENT_2, toMapKey(TEST_2))));

        final Map<String,String> responseData = requestHandler.handleRequest();

        assertThat(responseData.get("status"), is(STATUS.name()));
        assertThat(responseData.get("totalJobs"), is(valueOf(TOTAL_JOBS)));
        assertThat(responseData.get("remainingJobs"), is(valueOf(REMAINING_JOBS)));
        assertThat(responseData.get("runningJobs"), is(""));
    }

    @Before
    public void setup() throws Exception
    {
        requestHandler = new BuildStatusRequestHandler(server);
    }
}