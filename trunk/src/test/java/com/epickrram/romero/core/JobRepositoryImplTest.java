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

package com.epickrram.romero.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class JobRepositoryImplTest
{
    private static final String IDENTIFIER = "82174";
    private static final String RESULT = "RESULT";
    private static final String JOB_1 = "JOB-1";
    private static final String JOB_2 = "JOB-2";

    @Mock
    private JobDefinitionLoader<String, String> jobDefinitionLoader;
    @Mock
    private JobFactory<String, String, String> jobFactory;
    @Mock
    private Job<String, String> jobOne;
    @Mock
    private Job<String, String> jobTwo;
    @Mock
    private JobDefinition<String, String> jobDefinitionOne;
    @Mock
    private JobDefinition<String, String> jobDefinitionTwo;
    @Mock
    private JobEventListener<String, String> jobEventListener;

    private JobRepositoryImpl<String, String, String> jobRepository;

    @Test
    public void shouldLoadJobsOnInit() throws Exception
    {
        jobRepository.init(IDENTIFIER);

        verify(jobDefinitionLoader).loadJobDefinitions(IDENTIFIER);
    }

    @Test
    public void shouldTransitionJobsInOrder() throws Exception
    {
        jobRepository.init(IDENTIFIER);

        when(jobOne.transitionTo(JobState.RUNNING)).thenReturn(true, false);
        when(jobTwo.transitionTo(JobState.RUNNING)).thenReturn(true, false);

        assertThat(jobRepository.getJobToRun(), is(jobDefinitionOne));
        assertThat(jobRepository.getJobToRun(), is(jobDefinitionTwo));
        assertThat(jobRepository.getJobToRun(), is(nullValue()));

        verify(jobEventListener).onJobUpdate(jobOne);
        verify(jobEventListener).onJobUpdate(jobTwo);
    }

    @Test
    public void shouldIndicateNoJobsAreAvailable() throws Exception
    {
        jobRepository.init(IDENTIFIER);

        when(jobOne.getState()).thenReturn(JobState.FINISHED);
        when(jobTwo.getState()).thenReturn(JobState.RUNNING);

        assertThat(jobRepository.isJobAvailable(), is(false));
    }

    @Test
    public void shouldUpdateJobWithResult() throws Exception
    {
        jobRepository.init(IDENTIFIER);

        jobRepository.onJobResult(JOB_1, RESULT);

        verify(jobOne).addResult(RESULT, jobEventListener);
    }

    @Test
    public void shouldNotBlowUpIfResultIsReceivedForUnknownJob() throws Exception
    {
        jobRepository.init(IDENTIFIER);

        jobRepository.onJobResult("OTHER_JOB", RESULT);
    }

    @Before
    public void setUp() throws Exception
    {
        when(jobDefinitionLoader.loadJobDefinitions(IDENTIFIER)).
                thenReturn(asList(jobDefinitionOne, jobDefinitionTwo));
        when(jobOne.getKey()).thenReturn(JOB_1);
        when(jobTwo.getKey()).thenReturn(JOB_2);
        when(jobDefinitionOne.getKey()).thenReturn(JOB_1);
        when(jobDefinitionTwo.getKey()).thenReturn(JOB_2);
        when(jobFactory.createJob(jobDefinitionOne)).thenReturn(jobOne);
        when(jobFactory.createJob(jobDefinitionTwo)).thenReturn(jobTwo);
        jobRepository = new JobRepositoryImpl<>(jobDefinitionLoader, jobFactory, jobEventListener);
    }
}