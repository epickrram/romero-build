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

package com.epickrram.romero.server;

import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.common.TestExecutionResult;
import com.epickrram.romero.core.Job;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static com.epickrram.romero.server.StubTestExecutionResultBuilder.getTestExecutionResult;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class ServerImplTest
{
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String JOB_1 = "JOB-1";
    private static final String AGENT_ID = "AGENT_ID";

    @Mock
    private JobDefinition<String, Properties> jobDefinition;
    @Mock
    private Job<String, TestExecutionResult> job;
    @Mock
    private JobRepository<String, Properties, TestExecutionResult> jobRepository;

    private ServerImpl server;

    @Test
    public void shouldSwitchToBuildingStatusAfterInitialisation() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true);
        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_NEXT_BUILD));

        server.startTestRun(IDENTIFIER);

        assertThat(server.getStatus(), is(BuildStatus.BUILDING));

        verify(jobRepository).init(IDENTIFIER);
    }

    @Test
    public void shouldSwitchToWaitingForTestsToCompleteStatusWhenNoPendingTestsRemain() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(false);

        server.startTestRun(IDENTIFIER);

        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_TESTS_TO_COMPLETE));
    }

    @Test
    public void shouldReturnNextAvailableTestDefinition() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true);
        when(jobRepository.getJobToRun()).thenReturn(jobDefinition);

        final String nextTestClassToRun = server.getNextTestClassToRun(AGENT_ID);

        assertThat(nextTestClassToRun, is(JOB_1));
    }

    @Test
    public void shouldReturnNullIfNoMoreAvailableTestDefinitions() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true);
        when(jobRepository.getJobToRun()).thenReturn(null);

        final String nextTestClassToRun = server.getNextTestClassToRun(AGENT_ID);

        assertThat(nextTestClassToRun, is(nullValue()));
    }

    @Test
    public void shouldNotInitialiseIfAlreadyBuilding() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true, true, false);
        when(jobRepository.areJobsComplete()).thenReturn(true);

        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_NEXT_BUILD));

        server.startTestRun(IDENTIFIER);

        assertThat(server.getStatus(), is(BuildStatus.BUILDING));

        server.startTestRun(IDENTIFIER);

        verify(jobRepository).init(IDENTIFIER);
        verify(jobRepository, times(2)).isJobAvailable();
        verify(jobRepository, times(2)).areJobsComplete();
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    public void shouldInitialiseIfAllJobsAreComplete() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true, false, false);
        when(jobRepository.areJobsComplete()).thenReturn(true);
        
        server.startTestRun(IDENTIFIER);

        assertThat(server.getStatus(), is(BuildStatus.BUILDING));
        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_TESTS_TO_COMPLETE));
        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_NEXT_BUILD));

        server.startTestRun(IDENTIFIER);

        verify(jobRepository, times(2)).init(IDENTIFIER);
    }

    @Test
    public void shouldUpdateRepositoryJobOnTestExecutionResult() throws Exception
    {
        server.startTestRun(IDENTIFIER);
        server.getNextTestClassToRun(AGENT_ID);

        final TestExecutionResult result = getTestExecutionResult(JOB_1);
        
        server.onTestExecutionResult(result);

        verify(jobRepository).onJobResult(JOB_1, result);
    }

    @Before
    public void setUp() throws Exception
    {
        when(jobDefinition.getKey()).thenReturn(JOB_1);

        server = new ServerImpl(jobRepository);
    }
}