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

import com.epickrram.romero.server.dao.QueryUtil;
import com.epickrram.romero.testing.server.web.TestRunSummary;
import com.epickrram.romero.util.LoggingUtil;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public final class TestRunSummaryDaoImpl implements TestRunSummaryDao
{
    private static final Logger LOGGER = LoggingUtil.getLogger(TestRunSummaryDaoImpl.class);
    private static final String SELECT_SQL = "SELECT * FROM test_case_result";

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
            return queryUtil.query(new TestRunSummaryCollectionQueryHandler(SELECT_SQL));
        }
        catch (SQLException e)
        {
            LOGGER.warning("Failed to retrieve test run history: " + e.getMessage());
        }
        return Collections.emptyList();
    }

}
