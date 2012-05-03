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

package com.epickrram.romero.acceptance.framework;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import static com.epickrram.romero.acceptance.framework.Conditions.postRequestJsonResponseContainsCondition;
import static com.epickrram.romero.acceptance.framework.HttpUtil.getIgnoringResponse;
import static com.epickrram.romero.acceptance.framework.HttpUtil.post;
import static com.epickrram.romero.acceptance.framework.Waiter.parseIntFromGsonParsedIntValue;
import static com.epickrram.romero.acceptance.framework.Waiter.waitFor;

public final class Romero
{
    private final String host;
    private final int port;

    public Romero(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }

    public void startTestRun(final String testRunIdentifier)
    {
        getIgnoringResponse(toRomeroUrl("/start?jobIdentifier=" + testRunIdentifier));
    }

    public void waitForTestRunStarted(final String testRunIdentifier)
    {
        waitFor(postRequestJsonResponseContainsCondition(toRomeroUrl("/build/status.json"),
                "jobRunIdentifier", testRunIdentifier));
        waitFor(postRequestJsonResponseContainsCondition(toRomeroUrl("/build/status.json"),
                "status", "BUILDING"));
    }

    public void waitForTestRunFinished()
    {
        waitFor(postRequestJsonResponseContainsCondition(toRomeroUrl("/build/status.json"),
                "status", "WAITING_FOR_NEXT_BUILD"), 70L);
    }

    public void waitForTestRunHistoryLatest(final String testRunIdentifier)
    {
        waitFor(postRequestJsonResponseContainsCondition(toRomeroUrl("/testing/summary.json"),
                0, "jobRunIdentifier", testRunIdentifier));
    }

    public void waitForTestCaseResultSummary(final String testRunIdentifier,
                                             final int expectedTestCaseCount, final int expectedPassCount,
                                             final int expectedFailedCount, final int expectedIgnoredCount,
                                             final int expectedErrorCount)
    {
        waitFor(new Waiter.Condition()
        {
            @SuppressWarnings({"unchecked"})
            @Override
            public boolean isMet()
            {
                final String response = post(toRomeroUrl("/testing/summary.json"));
                final List<Map<String, Object>> array = new Gson().fromJson(response, List.class);
                final Map<String, Object> map = array.get(0);
                final int testCaseCount = parseIntFromGsonParsedIntValue(map, "testCaseCount");
                final Map<String, String> resultCountMap = (Map<String, String>) map.get("statusCountMap");
                final int passCount = getResultCount(resultCountMap, "SUCCESS");
                final int failedCount = getResultCount(resultCountMap, "FAILURE");
                final int ignoredCount = getResultCount(resultCountMap, "IGNORED");
                final int errorCount = getResultCount(resultCountMap, "ERROR");

                return expectedTestCaseCount == testCaseCount &&
                       expectedPassCount == passCount &&
                       expectedFailedCount == failedCount &&
                       expectedIgnoredCount == ignoredCount &&
                       expectedErrorCount == errorCount;
            }

            @Override
            public String getFailureMessage()
            {
                return String.format("Did not find expected test run summary for test run %s", testRunIdentifier);
            }
        });
    }

    private int getResultCount(final Map<String, String> resultCountMap, final String summaryKey)
    {
        return parseIntFromGsonParsedIntValue(resultCountMap, summaryKey);
    }

    private String toRomeroUrl(final String uri)
    {
        return new StringBuilder("http://").append(host).append(":").append(port).append(uri).toString();
    }
}
