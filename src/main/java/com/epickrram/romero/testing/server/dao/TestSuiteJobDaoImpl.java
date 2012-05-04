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

import com.epickrram.romero.server.CompletedJobRunIdentifier;
import com.epickrram.romero.server.dao.QueryUtil;
import com.epickrram.romero.server.dao.UpdateOnlyQueryHandler;
import com.epickrram.romero.testing.common.TestExecutionResult;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.util.LoggingUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TestSuiteJobDaoImpl implements TestSuiteJobDao
{
    private static final Logger LOGGER = LoggingUtil.getLogger(TestSuiteJobDaoImpl.class);
    private static final String INSERT_SQL = "\n" +
            "INSERT INTO test_case_result (job_run_identifier, start_timestamp, \n" +
            "duration_millis, test_suite, test_case, status, stdout, stderr, stack_trace) \n" +
            "VALUES (?,?,?,?,?,?,?,?,?)";

    private final QueryUtil queryUtil;

    public TestSuiteJobDaoImpl(final QueryUtil queryUtil)
    {
        this.queryUtil = queryUtil;
    }

    @Override
    public void onTestSuiteJobResult(final String jobIdentifier, final long startTimestamp, final TestSuiteJobResult jobResult)
    {
        for (final TestExecutionResult testExecutionResult : jobResult.getTestExecutionResults())
        {
            try
            {
                queryUtil.update(new UpdateOnlyQueryHandler(INSERT_SQL)
                {
                    @Override
                    public void prepareStatement(final PreparedStatement statement) throws SQLException
                    {
                        statement.setString(1, jobIdentifier);
                        statement.setLong(2, startTimestamp);
                        statement.setInt(3, (int) testExecutionResult.getDurationMillis());
                        statement.setString(4, testExecutionResult.getTestClass());
                        statement.setString(5, testExecutionResult.getTestMethod());
                        statement.setString(6, testExecutionResult.getTestStatus().name());
                        statement.setString(7, testExecutionResult.getStdout());
                        statement.setString(8, testExecutionResult.getStderr());
                        statement.setString(9, testExecutionResult.getThrowable());
                    }
                });
            }
            catch (SQLException e)
            {
                LOGGER.log(Level.WARNING, "Failed to record job result for " + jobResult.getTestClass(), e);
            }
        }
    }

    @Override
    public void onTestSuiteFailureToComplete(final String jobIdentifier, final long startTimestamp, final TestSuiteIdentifier testSuiteIdentifier)
    {
    }

    @Override
    public List<TestSuiteJobResult> getTestSuiteJobResultList(final CompletedJobRunIdentifier completedJobId)
    {
        try
        {
            return queryUtil.query(new TestSuiteResultListQueryHandler(completedJobId));
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to retrieve test suite results for " + completedJobId, e);
        }

        return Collections.emptyList();
    }


}