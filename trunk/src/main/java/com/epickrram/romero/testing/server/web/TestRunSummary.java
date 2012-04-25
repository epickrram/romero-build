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

package com.epickrram.romero.testing.server.web;

import com.epickrram.romero.testing.common.TestStatus;

import java.util.Map;

public final class TestRunSummary
{
    private final String jobRunIdentifier;
    private final long startTimestamp;
    private final long durationMillis;
    private final int testSuiteCount;
    private final int testCaseCount;
    private final Map<TestStatus, Integer> statusCountMap;

    public TestRunSummary(final String jobRunIdentifier, final long startTimestamp,
                          final long durationMillis, final int testSuiteCount,
                          final int testCaseCount, final Map<TestStatus, Integer> statusCountMap)
    {
        this.jobRunIdentifier = jobRunIdentifier;
        this.startTimestamp = startTimestamp;
        this.durationMillis = durationMillis;
        this.testSuiteCount = testSuiteCount;
        this.testCaseCount = testCaseCount;
        this.statusCountMap = statusCountMap;
    }

    public long getStartTimestamp()
    {
        return startTimestamp;
    }

    public String getJobRunIdentifier()
    {
        return jobRunIdentifier;
    }

    public long getDurationMillis()
    {
        return durationMillis;
    }

    public int getTestSuiteCount()
    {
        return testSuiteCount;
    }

    public int getTestCaseCount()
    {
        return testCaseCount;
    }

    public Map<TestStatus, Integer> getStatusCountMap()
    {
        return statusCountMap;
    }

    @Override
    public String toString()
    {
        return "TestRunSummary{" +
                "jobRunIdentifier='" + jobRunIdentifier + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", durationMillis=" + durationMillis +
                ", testSuiteCount=" + testSuiteCount +
                ", testCaseCount=" + testCaseCount +
                ", statusCountMap=" + statusCountMap +
                '}';
    }
}
