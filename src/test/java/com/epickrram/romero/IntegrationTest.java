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

package com.epickrram.romero;

import com.epickrram.romero.agent.Agent;
import com.epickrram.romero.agent.TestCaseJobResultHandlerImpl;
import com.epickrram.romero.agent.junit.JUnitTestExecutor;
import com.epickrram.romero.agent.junit.StubJUnitTestData;
import com.epickrram.romero.common.TestCaseIdentifier;
import com.epickrram.romero.common.TestCaseJob;
import com.epickrram.romero.common.TestCaseJobResult;
import com.epickrram.romero.core.*;
import com.epickrram.romero.server.ServerImpl;
import com.epickrram.romero.server.TestCaseJobFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Properties;

import static com.epickrram.romero.common.TestCaseIdentifier.toMapKey;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class IntegrationTest
{
    private static final String AGENT_ID = "AGENT-ID";
    private static final String TEST_RUN_IDENTIFIER = "test-run-identifier";
    private static final String TEST_CLASS = StubJUnitTestData.class.getName();
    @Mock
    private JobDefinitionLoader<TestCaseIdentifier, Properties> jobDefinitionLoader;
    @Mock
    private JobEventListener<TestCaseIdentifier, TestCaseJobResult> eventListener;
    @Mock
    private Agent.Sleeper sleeper;
    private ServerImpl server;
    private Agent agent;
    private JobRepository<TestCaseIdentifier, Properties, TestCaseJobResult> jobRepository;

    @Before
    public void setup() throws Exception
    {
        final TestCaseJobFactory jobFactory = new TestCaseJobFactory();
        jobRepository = new JobRepositoryImpl<>(jobDefinitionLoader, jobFactory, eventListener);
        server = new ServerImpl(jobRepository);
        agent = new Agent(server, new JUnitTestExecutor(new JUnitCore(), new TestCaseJobResultHandlerImpl(server)),
                sleeper, AGENT_ID);
    }

    @Test
    public void shouldRunBuild() throws Exception
    {
        when(jobDefinitionLoader.loadJobDefinitions(TEST_RUN_IDENTIFIER)).thenReturn(createJobDefinitionList());

        server.startTestRun(TEST_RUN_IDENTIFIER);

        agent.run();

        verify(eventListener, times(2)).onJobUpdate(any(TestCaseJob.class));

        assertThat(jobRepository.getJob(toMapKey(TEST_CLASS)).getState(), is(JobState.FINISHED));
    }

    private List<JobDefinition<TestCaseIdentifier, Properties>> createJobDefinitionList()
    {
        final TestCaseIdentifier key = new TestCaseIdentifier(TEST_CLASS, 0, 0L);
        final JobDefinition<TestCaseIdentifier, Properties> jobDefinition =
                new JobDefinitionImpl<>(key, new Properties());
        return singletonList(jobDefinition);
    }
}