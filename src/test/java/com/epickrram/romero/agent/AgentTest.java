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

import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.epickrram.romero.common.BuildStatus.*;
import static java.util.Collections.singletonList;
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
    private Server<String, String, String> server;
    @Mock
    private Agent.Sleeper sleeper;
    @Mock
    private ClassExecutor<String, String> classExecutor;
    @Mock
    private JobResultHandler<String> resultHandler;
    @Mock
    private ClasspathBuilder classpathBuilder;
    @Mock
    private ExecutionWrapper<String, String> executionWrapper;

    private Agent<String, String, String> agent;


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
        expectTestToBeRun(TEST_CLASS);

        verify(executionWrapper).beforeExecution(Matchers.<JobDefinition>any(), Matchers.<ExecutionContext>any());
        verify(classExecutor).execute(Matchers.<JobDefinition>any(), Matchers.<ExecutionContext>any());
        verify(executionWrapper).afterExecution(Matchers.<JobDefinition>any(), Matchers.<ExecutionContext>any());

        verify(server).getStatus();
        verifyZeroInteractions(sleeper);
    }

    @Test
    public void shouldSendFailureToServer() throws Exception
    {
        doThrow(new RuntimeException("BOOM!")).when(classExecutor).execute(Matchers.<JobDefinition>any(), Matchers.<ExecutionContext>any());
        expectTestToBeRun(TEST_CLASS);

        verify(server).onJobFailure(Matchers.<JobDefinition>any(), Matchers.<String>any());
    }

    @Test
    public void shouldSleepForDurationWhenServerStatusIsWaitingForTestFinish() throws Exception
    {
        when(server.getStatus()).thenReturn(WAITING_FOR_JOBS_TO_COMPLETE);

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

        verifyZeroInteractions(classExecutor);
        verify(server).getStatus();
        verify(sleeper).sleep(anyLong());
    }

    @Before
    public void setup() throws Exception
    {
        agent = new Agent(server, classExecutor, sleeper, AGENT_ID, singletonList(executionWrapper), classpathBuilder, new ClasspathElementScannerImpl());
    }

    private void expectTestToBeRun(final String testClass)
    {
        when(server.getStatus()).thenReturn(BUILDING);
        final JobDefinitionImpl<String, String> jobDefinition =
                new JobDefinitionImpl<>(testClass, "DATA");
        when(server.getNextTestToRun(AGENT_ID)).thenReturn(jobDefinition);

        agent.run();
    }
}