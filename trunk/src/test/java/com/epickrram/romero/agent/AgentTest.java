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

import com.epickrram.romero.agent.junit.JUnitTestExecutor;
import com.epickrram.romero.common.TestSuiteIdentifier;
import com.epickrram.romero.common.TestPropertyKeys;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.server.Server;
import com.epickrram.romero.stub.StubJUnitTestData;
import com.epickrram.romero.stub.StubTestWrapperOne;
import com.epickrram.romero.stub.StubTestWrapperTwo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static com.epickrram.romero.common.BuildStatus.*;
import static com.epickrram.romero.common.TestSuiteIdentifier.toMapKey;
import static com.epickrram.romero.common.TestPropertyKeys.SYSTEM_PROPERTY_PREFIX;
import static com.epickrram.romero.stub.StubJUnitTestData.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
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
    @Mock
    private TestCaseJobResultHandler resultHandler;

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
        expectTestToBeRun(TEST_CLASS, new Properties());

        verify(testExecutor).runTest(TEST_CLASS);

        verify(server).getStatus();
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

        verifyZeroInteractions(testExecutor);
        verify(server).getStatus();
        verify(sleeper).sleep(anyLong());
    }

    @Test
    public void shouldSetSystemPropertiesSpecifiedInTestDefinitionForDurationOfTest() throws Exception
    {
        final Properties properties = new Properties();
        properties.setProperty(SYSTEM_PROPERTY_PREFIX + PROP_KEY_1, "value1");
        properties.setProperty(SYSTEM_PROPERTY_PREFIX + PROP_KEY_2, "value2");

        agent = new Agent(server, new JUnitTestExecutor(new JUnitCore(), resultHandler), sleeper, AGENT_ID);

        expectTestToBeRun(StubJUnitTestData.class.getName(), properties);

        assertThat("value1", is(PROP_VALUE_1));
        assertThat("value2", is(PROP_VALUE_2));

        assertThat(System.getProperty(PROP_KEY_1), is(nullValue()));
        assertThat(System.getProperty(PROP_KEY_2), is(nullValue()));
    }

    @Test
    public void shouldInstantiateAndInvokeTestCaseWrappersSpecifiedInTestDefinition() throws Exception
    {
        final Properties properties = new Properties();
        properties.setProperty(TestPropertyKeys.TEST_CASE_WRAPPER_PREFIX + "1", StubTestWrapperOne.class.getName());
        properties.setProperty(TestPropertyKeys.TEST_CASE_WRAPPER_PREFIX + "2", StubTestWrapperTwo.class.getName());

        assertThat(StubTestWrapperOne.beforeTestCaseInvocationCount, is(0));
        assertThat(StubTestWrapperOne.afterTestCaseInvocationCount, is(0));
        assertThat(StubTestWrapperTwo.beforeTestCaseInvocationCount, is(0));
        assertThat(StubTestWrapperTwo.afterTestCaseInvocationCount, is(0));

        expectTestToBeRun(TEST_CLASS, properties);

        assertThat(StubTestWrapperOne.beforeTestCaseInvocationCount, is(1));
        assertThat(StubTestWrapperOne.afterTestCaseInvocationCount, is(1));
        assertThat(StubTestWrapperTwo.beforeTestCaseInvocationCount, is(1));
        assertThat(StubTestWrapperTwo.afterTestCaseInvocationCount, is(1));
    }

    @Before
    public void setup() throws Exception
    {
        agent = new Agent(server, testExecutor, sleeper, AGENT_ID);
    }

    private void expectTestToBeRun(final String testClass, final Properties properties)
    {
        when(server.getStatus()).thenReturn(BUILDING);
        final JobDefinitionImpl<TestSuiteIdentifier, Properties> jobDefinition =
                new JobDefinitionImpl<>(toMapKey(testClass), properties);
        when(server.getNextTestToRun(AGENT_ID)).thenReturn(jobDefinition);

        agent.run();
    }
}