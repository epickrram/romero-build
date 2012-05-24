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

import com.epickrram.romero.agent.*;
import com.epickrram.romero.core.*;
import com.epickrram.romero.server.JobRunListener;
import com.epickrram.romero.server.ServerImpl;
import com.epickrram.romero.stub.StubJUnitTestData;
import com.epickrram.romero.testing.agent.ClasspathBuilderImpl;
import com.epickrram.romero.testing.agent.SystemPropertySetterExecutionWrapper;
import com.epickrram.romero.testing.agent.junit.JUnitClassExecutor;
import com.epickrram.romero.testing.common.TestPropertyKeys;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.testing.server.TestCaseJobFactory;
import com.epickrram.romero.testing.server.TestSuiteKeyFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.epickrram.romero.MatcherFactory.testCaseJobsWithStates;
import static com.epickrram.romero.agent.AgentTest.EXTERNAL_TEST_RESOURCE_PATH;
import static com.epickrram.romero.agent.AgentTest.TEST_CLASS_FROM_EXTERNAL_JAR;
import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class IntegrationTest
{
    private static final String AGENT_ID = "AGENT-ID";
    private static final String TEST_RUN_IDENTIFIER = "test-run-identifier";
    private static final String TEST_CLASS = StubJUnitTestData.class.getName();

    @Mock
    private JobDefinitionLoader<TestSuiteIdentifier, Properties> jobDefinitionLoader;
    @Mock
    private JobEventListener<TestSuiteIdentifier, TestSuiteJobResult> eventListener;
    @Mock
    private Agent.Sleeper sleeper;
    @Mock
    private JobRunListener jobRunListener;
    private ServerImpl<TestSuiteIdentifier, Properties, TestSuiteJobResult> server;
    private Agent<TestSuiteIdentifier, Properties, TestSuiteJobResult> agent;
    private JobRepository<TestSuiteIdentifier, Properties, TestSuiteJobResult> jobRepository;

    @Before
    public void setup() throws Exception
    {
        final TestCaseJobFactory jobFactory = new TestCaseJobFactory();
        jobRepository = new JobRepositoryImpl<>(jobDefinitionLoader, jobFactory, eventListener);
        server = new ServerImpl<>(jobRepository, new TestSuiteKeyFactory(), jobRunListener);
        final JobResultHandler<TestSuiteJobResult> resultHandler = new JobResultHandlerImpl<>(server);

        final List<ExecutionWrapper<TestSuiteIdentifier, Properties>> executionWrapperList =
                Collections.singletonList((ExecutionWrapper<TestSuiteIdentifier, Properties>) new SystemPropertySetterExecutionWrapper());
        agent = new Agent<>(server, new JUnitClassExecutor(new JUnitCore(), resultHandler),
                sleeper, AGENT_ID, executionWrapperList, new ClasspathBuilderImpl(), new ClasspathElementScannerImpl());
    }

    @Test
    public void shouldRunBuild() throws Exception
    {
        when(jobDefinitionLoader.loadJobDefinitions(TEST_RUN_IDENTIFIER)).thenReturn(createJobDefinitionList());

        server.startJobRun(TEST_RUN_IDENTIFIER);

        agent.run();

        verify(eventListener, times(2)).onJobUpdate(argThat(
                testCaseJobsWithStates(TEST_CLASS, JobState.RUNNING, JobState.FINISHED)));

        assertThat(jobRepository.getJob(toMapKey(TEST_CLASS)).getState(), is(JobState.FINISHED));
    }

    @Test
    public void shouldLoadTestClassesFromExternalSource() throws Exception
    {
        final Properties properties = new Properties();

        final URL url = new File(EXTERNAL_TEST_RESOURCE_PATH).toURI().toURL();
        properties.setProperty(TestPropertyKeys.CLASSPATH_URL_PREFIX + ".tests", url.toExternalForm());

        final List<JobDefinition<TestSuiteIdentifier, Properties>> definitionList =
                createJobDefinitionList(toMapKey(TEST_CLASS_FROM_EXTERNAL_JAR), properties);
        when(jobDefinitionLoader.loadJobDefinitions(TEST_RUN_IDENTIFIER)).thenReturn(definitionList);

        server.startJobRun(TEST_RUN_IDENTIFIER);

        agent.run();

        verify(eventListener, times(2)).onJobUpdate(argThat(
                testCaseJobsWithStates(TEST_CLASS_FROM_EXTERNAL_JAR, JobState.RUNNING, JobState.FINISHED)));

        assertThat(jobRepository.getJob(toMapKey(TEST_CLASS_FROM_EXTERNAL_JAR)).getState(), is(JobState.FINISHED));
    }

    private List<JobDefinition<TestSuiteIdentifier, Properties>> createJobDefinitionList()
    {
        final String testClass = TEST_CLASS;
        final TestSuiteIdentifier key = new TestSuiteIdentifier(testClass, 0, 0L);
        final Properties properties = new Properties();
        return createJobDefinitionList(key, properties);
    }

    private List<JobDefinition<TestSuiteIdentifier, Properties>> createJobDefinitionList(final TestSuiteIdentifier key, final Properties properties)
    {
        final JobDefinition<TestSuiteIdentifier, Properties> jobDefinition = new JobDefinitionImpl<>(key, properties);
        return singletonList(jobDefinition);
    }
}