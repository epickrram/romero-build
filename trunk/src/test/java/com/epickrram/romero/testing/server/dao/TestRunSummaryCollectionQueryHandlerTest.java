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
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.server.web.TestRunSummary;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static com.epickrram.romero.MatcherFactory.summary;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class TestRunSummaryCollectionQueryHandlerTest
{

    private TestRunSummaryCollectionQueryHandler queryHandler;

    @Before
    public void setUp() throws Exception
    {
        queryHandler = new TestRunSummaryCollectionQueryHandler("");
    }

    @Test
    public void shouldGenerateSingleTestRunSummary() throws Exception
    {
        final ResultSetMocker resultSetMocker = new ResultSetMocker();
        final String jobId = "123";
        final long startTimestamp = 1000L;
        resultSetMocker.
                addString("job_run_identifier", jobId).
                addString("status", "SUCCESS").
                addString("test_suite", "suite1").
                addLong("start_timestamp", startTimestamp).
                addLong("duration_millis", 500L);

        resultSetMocker.prepareForReading();
        final Collection<TestRunSummary> testRunSummaries = queryHandler.handleResult(resultSetMocker);

        assertThat(testRunSummaries.size(), is(1));
        assertThat(testRunSummaries.iterator().next(), is(summary(jobId, startTimestamp)));
    }
    
    @Test
    public void shouldGenerateMultipleSummaries() throws Exception
    {
        final ResultSetMocker resultSetMocker = new ResultSetMocker();
        final String jobOneId = "123";
        final long startTimestampOne = 1000L;
        final String jobTwoId = "123";
        final long startTimestampTwo = 1001L;
        final String jobThreeId = "124";
        final long startTimestampThree = 1001L;
        resultSetMocker.addString("job_run_identifier", jobOneId).
                addString("status", "SUCCESS").
                addString("test_suite", "suite1").
                addLong("start_timestamp", startTimestampOne).
                addLong("duration_millis", 500L).commitRow();
        resultSetMocker.addString("job_run_identifier", jobTwoId).
                addString("status", "SUCCESS").
                addString("test_suite", "suite1").
                addLong("start_timestamp", startTimestampTwo).
                addLong("duration_millis", 500L).commitRow();
        resultSetMocker.addString("job_run_identifier", jobThreeId).
                addString("status", "FAILURE").
                addString("test_suite", "suite1").
                addLong("start_timestamp", startTimestampThree).
                addLong("duration_millis", 500L).commitRow();
        resultSetMocker.addString("job_run_identifier", jobThreeId).
                addString("status", "FAILURE").
                addString("test_suite", "suite1").
                addLong("start_timestamp", startTimestampThree).
                addLong("duration_millis", 500L).commitRow();


        resultSetMocker.prepareForReading();
        final List<TestRunSummary> testRunSummaries = queryHandler.handleResult(resultSetMocker);

        assertThat(testRunSummaries.size(), is(3));
        assertThat(testRunSummaries.get(0), is(summary(jobTwoId, startTimestampTwo)));
        assertThat(testRunSummaries.get(1), is(summary(jobThreeId, startTimestampThree)));
        assertThat(testRunSummaries.get(2), is(summary(jobOneId, startTimestampOne)));

        final TestRunSummary testRunSummary = testRunSummaries.get(1);
        assertThat(testRunSummary.getStatusCountMap().size(), is(5));
        assertThat(testRunSummary.getStatusCountMap().get(TestStatus.FAILURE), is(2));
    }
}
