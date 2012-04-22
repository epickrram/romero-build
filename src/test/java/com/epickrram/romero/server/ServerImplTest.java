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
import com.epickrram.romero.core.Job;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobRepository;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.testing.server.TestSuiteKeyFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static com.epickrram.romero.MatcherFactory.runningJobs;
import static com.epickrram.romero.TestHelper.runningJob;
import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;
import static com.epickrram.romero.testing.server.StubTestResultBuilder.getTestCaseJobResult;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class ServerImplTest
{
    private static final String IDENTIFIER = "IDENTIFIER";
    private static final String JOB_1 = "JOB-1";
    private static final TestSuiteIdentifier JOB_1_ID = toMapKey(JOB_1);
    private static final String JOB_2 = "JOB-2";
    private static final TestSuiteIdentifier JOB_2_ID = toMapKey(JOB_2);
    private static final String AGENT_ID = "AGENT_ID";

    @Mock
    private JobDefinition<TestSuiteIdentifier, Properties> jobDefinition;
    @Mock
    private JobDefinition<TestSuiteIdentifier, Properties> jobDefinition2;
    @Mock
    private Job<String, TestSuiteJobResult> job;
    @Mock
    private JobRepository<TestSuiteIdentifier, Properties, TestSuiteJobResult> jobRepository;

    private ServerImpl<TestSuiteIdentifier, Properties, TestSuiteJobResult> server;

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

        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_JOBS_TO_COMPLETE));
    }

    @Test
    public void shouldReturnNextAvailableTestDefinition() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true);
        when(jobRepository.getJobToRun()).thenReturn(jobDefinition);

        final JobDefinition<TestSuiteIdentifier, Properties> definition = server.getNextTestToRun(AGENT_ID);

        assertThat(definition.getKey().getTestClass(), is(JOB_1));
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void shouldRecordAgentsActivelyRunningJobs() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true, true, false);
        when(jobRepository.getJobToRun()).thenReturn(jobDefinition, jobDefinition2);

        server.getNextTestToRun(AGENT_ID);

        assertThat(server.getRunningJobs(), runningJobs(runningJob(AGENT_ID, JOB_1_ID)));

        final String agentTwoId = "agent-2";
        server.getNextTestToRun(agentTwoId);

        assertThat(server.getRunningJobs(),
                runningJobs(runningJob(AGENT_ID, JOB_1_ID), runningJob(agentTwoId, JOB_2_ID)));

        server.onJobResult(getTestCaseJobResult(JOB_1));

        assertThat(server.getRunningJobs(), runningJobs(runningJob(agentTwoId, JOB_2_ID)));

        server.onJobResult(getTestCaseJobResult(JOB_2));

        assertThat(server.getRunningJobs(), runningJobs());
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void shouldRemoveRunningJobOnFailure() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true, true, false);
        when(jobRepository.getJobToRun()).thenReturn(jobDefinition, jobDefinition2);

        server.getNextTestToRun(AGENT_ID);

        assertThat(server.getRunningJobs(), runningJobs(runningJob(AGENT_ID, JOB_1_ID)));

        final String stackTrace = "BOOM!";
        server.onJobFailure(jobDefinition, stackTrace);

        assertThat(server.getRunningJobs(), runningJobs());

        verify(jobRepository).onJobFailure(jobDefinition.getKey(), stackTrace);
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void shouldReturnJobStats() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true, true, false);
        when(jobRepository.getJobToRun()).thenReturn(jobDefinition, jobDefinition2);
        when(jobRepository.getJobsRemainingToBeRun()).thenReturn(2, 1, 0, 0, 0);
        when(jobRepository.size()).thenReturn(2);

        assertThat(server.getTotalJobs(), is(2));
        assertThat(server.getJobsRemainingToBeRun(), is(2));
        assertThat(server.getNumberOfRunningJobs(), is(0));

        server.getNextTestToRun(AGENT_ID);

        assertThat(server.getTotalJobs(), is(2));
        assertThat(server.getJobsRemainingToBeRun(), is(1));
        assertThat(server.getNumberOfRunningJobs(), is(1));

        final String agentTwoId = "agent-2";
        server.getNextTestToRun(agentTwoId);

        assertThat(server.getTotalJobs(), is(2));
        assertThat(server.getJobsRemainingToBeRun(), is(0));
        assertThat(server.getNumberOfRunningJobs(), is(2));

        server.onJobResult(getTestCaseJobResult(JOB_1));

        assertThat(server.getTotalJobs(), is(2));
        assertThat(server.getJobsRemainingToBeRun(), is(0));
        assertThat(server.getNumberOfRunningJobs(), is(1));

        server.onJobFailure(jobDefinition2, "BOOM!");

        assertThat(server.getTotalJobs(), is(2));
        assertThat(server.getJobsRemainingToBeRun(), is(0));
        assertThat(server.getNumberOfRunningJobs(), is(0));
    }

    @Test
    public void shouldReturnNullIfNoMoreAvailableTestDefinitions() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true);
        when(jobRepository.getJobToRun()).thenReturn(null);

        final JobDefinition<TestSuiteIdentifier, Properties> definition = server.getNextTestToRun(AGENT_ID);

        assertThat(definition, is(nullValue()));
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
        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_JOBS_TO_COMPLETE));
        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_NEXT_BUILD));

        server.startTestRun(IDENTIFIER);

        verify(jobRepository, times(2)).init(IDENTIFIER);
    }

    @Test
    public void shouldUpdateRepositoryJobOnTestExecutionResult() throws Exception
    {
        server.startTestRun(IDENTIFIER);
        server.getNextTestToRun(AGENT_ID);

        final TestSuiteJobResult result = getTestCaseJobResult(JOB_1);
        
        server.onJobResult(result);

        verify(jobRepository).onJobResult(eq(toMapKey(JOB_1)), same(result));
    }

    @Test
    public void shouldReturnCurrentBuildId() throws Exception
    {
        when(jobRepository.isJobAvailable()).thenReturn(true, false, false);
        when(jobRepository.areJobsComplete()).thenReturn(true);

        assertThat(server.getCurrentBuildId(), is(nullValue()));

        server.startTestRun(IDENTIFIER);

        assertThat(server.getStatus(), is(BuildStatus.BUILDING));
        assertThat(server.getCurrentBuildId(), is(IDENTIFIER));

        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_JOBS_TO_COMPLETE));
        assertThat(server.getCurrentBuildId(), is(IDENTIFIER));

        assertThat(server.getStatus(), is(BuildStatus.WAITING_FOR_NEXT_BUILD));
        assertThat(server.getCurrentBuildId(), is(nullValue()));
    }

    @Before
    public void setUp() throws Exception
    {
        when(jobDefinition.getKey()).thenReturn(JOB_1_ID);
        when(jobDefinition2.getKey()).thenReturn(JOB_2_ID);

        server = new ServerImpl<>(jobRepository, new TestSuiteKeyFactory());
    }

}