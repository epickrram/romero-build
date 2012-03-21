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

package com.epickrram.romero.agent;

import com.epickrram.romero.common.TestCaseIdentifier;
import com.epickrram.romero.common.TestPropertyKeys;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import static com.epickrram.romero.common.BuildStatus.*;
import static com.epickrram.romero.common.TestCaseIdentifier.toMapKey;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class AgentTest
{
    public static final String TEST_CLASS_FROM_EXTERNAL_JAR = "com.epickrram.romero.StubTestCaseFromExternalJar";
    public static final String EXTERNAL_TEST_RESOURCE_PATH = "src/test/resources/external-test-archive.jar";
    private static final String AGENT_ID = "AGENT-ID";
    private static final String TEST_CLASS = "TEST-CLASS";

    @Mock
    private Server server;
    @Mock
    private Agent.Sleeper sleeper;
    @Mock
    private TestExecutor testExecutor;

    private Agent agent;

    @Test
    public void shouldRetrieveServerStatus() throws Exception
    {
        when(server.getStatus()).thenReturn(WAITING_FOR_NEXT_BUILD);

        agent.run();

        verify(server).getStatus();
        verify(sleeper).sleep(anyLong());
    }

    @Test
    public void shouldRetrieveNextJobWhenServerStatusIsBuilding() throws Exception
    {
        when(server.getStatus()).thenReturn(BUILDING);
        final JobDefinitionImpl<TestCaseIdentifier, Properties> jobDefinition =
                new JobDefinitionImpl<>(toMapKey(TEST_CLASS), new Properties());
        when(server.getNextTestToRun(AGENT_ID)).thenReturn(jobDefinition);

        agent.run();

        verify(server).getStatus();
        verify(testExecutor).runTest(TEST_CLASS);
        verifyZeroInteractions(sleeper);
    }

    @Test
    public void shouldSleepForDurationWhenServerStatusIsWaitingForTestFinish() throws Exception
    {
        when(server.getStatus()).thenReturn(WAITING_FOR_TESTS_TO_COMPLETE);

        agent.run();

        verify(server).getStatus();
        verify(sleeper).sleep(anyLong());
    }

    @Test
    public void shouldSleepForDurationWhenServerStatusIsBuildingButNextTestIsNull() throws Exception
    {
        when(server.getStatus()).thenReturn(BUILDING);
        when(server.getNextTestToRun(AGENT_ID)).thenReturn(null);

        agent.run();

        verify(server).getStatus();
        verifyZeroInteractions(testExecutor);
        verify(sleeper).sleep(anyLong());
    }

    @Test
    public void shouldCreateTestCaseWrappersFromClasspathIfSpecifiedInTestDefinition() throws Exception
    {
        when(server.getStatus()).thenReturn(BUILDING);
        final Properties properties = new Properties();

        final URL url = new File(EXTERNAL_TEST_RESOURCE_PATH).toURI().toURL();
        properties.setProperty(TestPropertyKeys.CLASSPATH_URL + ".tests", url.toExternalForm());

        final JobDefinitionImpl<TestCaseIdentifier, Properties> jobDefinition =
                new JobDefinitionImpl<>(toMapKey(TEST_CLASS_FROM_EXTERNAL_JAR), properties);
        when(server.getNextTestToRun(AGENT_ID)).thenReturn(jobDefinition);

        agent.run();

        verify(testExecutor).runTest(TEST_CLASS_FROM_EXTERNAL_JAR);
    }

    @Before
    public void setup() throws Exception
    {
        agent = new Agent(server, testExecutor, sleeper, AGENT_ID);
    }


}