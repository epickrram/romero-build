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

import com.epickrram.romero.server.web.VoidInputRequestHandler;
import com.epickrram.romero.testing.server.dao.TestRunSummaryDao;

import java.util.Collection;

public final class TestRunHistorySummaryRequestHandler extends VoidInputRequestHandler<Collection<TestRunSummary>>
{
    private static final int HISTORY_LIMIT = 20;
    
    private final TestRunSummaryDao testRunSummaryDao;

    public TestRunHistorySummaryRequestHandler(final TestRunSummaryDao testRunSummaryDao)
    {
        super("/testing/summary.json");
        this.testRunSummaryDao = testRunSummaryDao;
    }
    @Override
    public Collection<TestRunSummary> handleRequest()
    {
        return testRunSummaryDao.getTestRunHistory(HISTORY_LIMIT);
    }
}