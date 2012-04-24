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

import com.epickrram.romero.server.dao.QueryHandler;
import com.epickrram.romero.server.dao.QueryUtil;
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.server.web.TestRunSummary;
import com.epickrram.romero.util.LoggingUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class TestRunSummaryDaoImpl implements TestRunSummaryDao
{
    private static final Logger LOGGER = LoggingUtil.getLogger(TestRunSummaryDaoImpl.class);
    private static final String SELECT_SQL = "SELECT * FROM test_case_result";
    private static final TestRunSummaryComparator TIMESTAMP_DESCENDING_COMPARATOR = new TestRunSummaryComparator();

    private final QueryUtil queryUtil;

    public TestRunSummaryDaoImpl(final QueryUtil queryUtil)
    {
        this.queryUtil = queryUtil;
    }

    @Override
    public Collection<TestRunSummary> getTestRunHistory(final int limit)
    {
        try
        {
            return queryUtil.query(new QueryHandler<Collection<TestRunSummary>>(SELECT_SQL)
            {
                @Override
                public void prepareStatement(final PreparedStatement statement) throws SQLException
                {
                }

                @Override
                public Collection<TestRunSummary> handleResult(final ResultSet resultSet) throws SQLException
                {
                    final List<TestRunSummary> summaryList = new ArrayList<>();
                    TestRunSummaryBuilder builder = new TestRunSummaryBuilder();
                    while(resultSet.next())
                    {
                        final long startTimestamp = resultSet.getLong("start_timestamp");
                        final String jobIdentifier = resultSet.getString("job_run_identifier");

                        if(builder.startTimestamp != 0 && builder.jobRunIdentifier != null &&
                           (startTimestamp != builder.startTimestamp || !jobIdentifier.equals(builder.jobRunIdentifier)))
                        {
                            summaryList.add(builder.create());
                            builder = new TestRunSummaryBuilder();
                        }

                        builder.jobIdentifier(jobIdentifier).
                                duration(resultSet.getLong("duration_millis")).
                                startTimestamp(startTimestamp).
                                testSuite(resultSet.getString("test_suite")).
                                testCase(TestStatus.valueOf(resultSet.getString("status")));
                    }
                    Collections.sort(summaryList, TIMESTAMP_DESCENDING_COMPARATOR);
                    return summaryList;
                }
            });
        }
        catch (SQLException e)
        {
            LOGGER.warning("Failed to retrieve test run history: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private static final class TestRunSummaryBuilder
    {
        private long durationMillis = 0L;
        private int testCaseCount = 0;
        private Map<TestStatus, Integer> statusCountMap = new HashMap<>();
        private Set<String> testSuiteNames = new HashSet<>();
        private String jobRunIdentifier = null;
        private long startTimestamp = 0L;

        TestRunSummary create()
        {
            return new TestRunSummary(jobRunIdentifier, startTimestamp, durationMillis,
                    testSuiteNames.size(), testCaseCount, statusCountMap);
        }

        TestRunSummaryBuilder duration(final long duration)
        {
            this.durationMillis += duration;
            return this;
        }

        TestRunSummaryBuilder testSuite(final String testSuiteName)
        {
            testSuiteNames.add(testSuiteName);
            return this;
        }

        @SuppressWarnings({"UnnecessaryBoxing", "UnnecessaryUnboxing"})
        TestRunSummaryBuilder testCase(final TestStatus status)
        {
            testCaseCount++;
            if(!statusCountMap.containsKey(status))
            {
                statusCountMap.put(status, Integer.valueOf(0));
            }

            statusCountMap.put(status, Integer.valueOf(statusCountMap.get(status).intValue()));
            return this;
        }

        TestRunSummaryBuilder jobIdentifier(final String jobIdentifier)
        {
            this.jobRunIdentifier = jobIdentifier;
            return this;
        }

        TestRunSummaryBuilder startTimestamp(final long startTimestamp)
        {
            this.startTimestamp = startTimestamp;
            return this;
        }
    }

    private static class TestRunSummaryComparator implements Comparator<TestRunSummary>
    {
        @Override
        public int compare(final TestRunSummary o1, final TestRunSummary o2)
        {
            return Long.compare(o2.getStartTimestamp(), o1.getStartTimestamp());
        }
    }
}
