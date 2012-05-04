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

package com.epickrram.romero.testing.server.dao;

import com.epickrram.romero.ResultSetMocker;
import com.epickrram.romero.server.CompletedJobRunIdentifier;
import com.epickrram.romero.testing.common.TestExecutionResult;
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.epickrram.romero.MatcherFactory.testExecutionResult;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public final class TestSuiteResultListQueryHandlerTest
{
    private static final String JOB_RUN_ID = "job-run-id";
    private static final long START_TIMESTAMP = 2987392139273L;

    private ResultSetMocker resultSetMocker;
    @Mock
    private PreparedStatement preparedStatement;
    private TestSuiteResultListQueryHandler queryHandler;

    @Before
    public void setUp() throws Exception
    {
        resultSetMocker = new ResultSetMocker();
        queryHandler = new TestSuiteResultListQueryHandler(new CompletedJobRunIdentifier(JOB_RUN_ID, START_TIMESTAMP));
    }

    @Test
    public void shouldPrepareStatement() throws Exception
    {
        queryHandler.prepareStatement(preparedStatement);

        verify(preparedStatement).setString(1, JOB_RUN_ID);
        verify(preparedStatement).setLong(2, START_TIMESTAMP);
    }

    @Test
    public void shouldReturnSingleResult() throws Exception
    {
        final int uniqueTestCase = 0;
        final int uniqueTestSuite = 0;
        final TestStatus status = TestStatus.SUCCESS;

        addResultSetRow(uniqueTestCase, uniqueTestSuite, status);

        resultSetMocker.prepareForReading();

        final List<TestSuiteJobResult> resultList = queryHandler.handleResult(resultSetMocker);

        assertThat(resultList.size(), is(1));

        final TestSuiteJobResult testSuiteJobResult = resultList.get(0);
        final Collection<TestExecutionResult> testExecutionResults = testSuiteJobResult.getTestExecutionResults();

        assertThat(testExecutionResults.size(), is(1));

        final TestExecutionResult testExecutionResult = testExecutionResults.iterator().next();

        assertThat(testExecutionResult, is(testExecutionResult(uniqueTestCase, uniqueTestSuite, status)));
    }

    @Test
    public void shouldReturnMultipleTestCaseResultsInTheSameTestSuite() throws Exception
    {
        for(int i = 0; i < 3; i++)
        {
            addResultSetRow(i, 0, TestStatus.FAILURE);
        }

        resultSetMocker.prepareForReading();

        final List<TestSuiteJobResult> resultList = queryHandler.handleResult(resultSetMocker);

        assertThat(resultList.size(), is(1));

        final TestSuiteJobResult testSuiteJobResult = resultList.get(0);
        final Collection<TestExecutionResult> testExecutionResults = testSuiteJobResult.getTestExecutionResults();

        assertThat(testExecutionResults.size(), is(3));

        final Iterator<TestExecutionResult> iterator = testExecutionResults.iterator();
        final TestExecutionResult first = iterator.next();
        assertThat(first, is(testExecutionResult(0, 0, TestStatus.FAILURE)));

        final TestExecutionResult second = iterator.next();
        assertThat(second, is(testExecutionResult(1, 0, TestStatus.FAILURE)));

        final TestExecutionResult third = iterator.next();
        assertThat(third, is(testExecutionResult(2, 0, TestStatus.FAILURE)));
    }

    @Test
    public void shouldReturnMultipleTestSuiteResults() throws Exception
    {
        for(int j = 0; j < 2; j++)
        {
            for(int i = 0; i < 3; i++)
            {
                addResultSetRow(i, j, TestStatus.ERROR);
            }
        }

        resultSetMocker.prepareForReading();

        final List<TestSuiteJobResult> resultList = queryHandler.handleResult(resultSetMocker);

        assertThat(resultList.size(), is(2));

        final TestSuiteJobResult testSuiteJobResult = resultList.get(1);
        final Collection<TestExecutionResult> testExecutionResults = testSuiteJobResult.getTestExecutionResults();

        assertThat(testExecutionResults.size(), is(3));

        final Iterator<TestExecutionResult> iterator = testExecutionResults.iterator();
        final TestExecutionResult first = iterator.next();
        assertThat(first, is(testExecutionResult(0, 1, TestStatus.ERROR)));

        final TestExecutionResult second = iterator.next();
        assertThat(second, is(testExecutionResult(1, 1, TestStatus.ERROR)));

        final TestExecutionResult third = iterator.next();
        assertThat(third, is(testExecutionResult(2, 1, TestStatus.ERROR)));
    }

    private void addResultSetRow(final int uniqueTestCase, final int uniqueTestSuite, final TestStatus status)
    {
        resultSetMocker.
                addInt("duration_millis", uniqueTestCase).
                addString("test_suite", "test_suite" + uniqueTestSuite).
                addString("test_case", "test_case" + uniqueTestCase).
                addString("status", status.name()).
                addString("stdout", "stdout" + uniqueTestCase).
                addString("stderr", "stderr" + uniqueTestCase).
                addString("stack_trace", "stack_trace" + uniqueTestCase).
                commitRow();
    }
}
