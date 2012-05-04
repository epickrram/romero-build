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
import com.epickrram.romero.server.dao.QueryHandler;
import com.epickrram.romero.testing.common.TestExecutionResult;
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.common.TestSuiteJobResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class TestSuiteResultListQueryHandler extends QueryHandler<List<TestSuiteJobResult>>
{
    private static final String SELECT_RESULTS_SQL = "\n" +
        "SELECT * FROM test_case_result WHERE job_run_identifier = ? AND start_timestamp = ? ORDER BY test_suite, test_case ASC";

    private final CompletedJobRunIdentifier completedJobId;

    public TestSuiteResultListQueryHandler(final CompletedJobRunIdentifier completedJobId)
    {
        super(SELECT_RESULTS_SQL);
        this.completedJobId = completedJobId;
    }

    @Override
    public void prepareStatement(final PreparedStatement statement) throws SQLException
    {
        statement.setString(1, completedJobId.getJobRunIdentifier());
        statement.setLong(2, completedJobId.getStartTimestamp());
    }

    @Override
    public List<TestSuiteJobResult> handleResult(final ResultSet resultSet) throws SQLException
    {
        final List<TestSuiteJobResult> resultList = new ArrayList<>();

        TestSuiteJobResult.Builder testSuiteBuilder = new TestSuiteJobResult.Builder();

        while(resultSet.next())
        {
            final String testSuiteClass = resultSet.getString("test_suite");
            final String testCase = resultSet.getString("test_case");
            final int durationMillis = resultSet.getInt("duration_millis");
            final TestStatus status = TestStatus.valueOf(resultSet.getString("status"));
            final String stdout = resultSet.getString("stdout");
            final String stderr = resultSet.getString("stderr");
            final String stackTrace = resultSet.getString("stack_trace");

            if(!testSuiteClass.equals(testSuiteBuilder.getTestClass()) && testSuiteBuilder.getTestClass() != null)
            {
                resultList.add(testSuiteBuilder.newInstance());
                testSuiteBuilder = new TestSuiteJobResult.Builder();
            }

            testSuiteBuilder.testClass(testSuiteClass);

            final TestExecutionResult testExecutionResult = new TestExecutionResult.Builder().
                    testClass(testSuiteClass).testMethod(testCase).testStatus(status).
                    durationMillis(durationMillis).stdout(stdout).stderr(stderr).
                    throwable(stackTrace).newInstance();

            testSuiteBuilder.testExecutionResult(testExecutionResult);
        }

        if(testSuiteBuilder.getTestClass() != null)
        {
            resultList.add(testSuiteBuilder.newInstance());
        }

        return resultList;
    }
}
