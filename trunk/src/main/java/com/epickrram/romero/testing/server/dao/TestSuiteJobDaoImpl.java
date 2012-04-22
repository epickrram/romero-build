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
import com.epickrram.romero.server.dao.UpdateOnlyQueryHandler;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.util.LoggingUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TestSuiteJobDaoImpl implements TestSuiteJobDao
{
    private static final Logger LOGGER = LoggingUtil.getLogger(TestSuiteJobDaoImpl.class);
    private final QueryUtil queryUtil;

    public TestSuiteJobDaoImpl(final QueryUtil queryUtil)
    {
        this.queryUtil = queryUtil;
    }

    @Override
    public void onTestSuiteJobResult(final String jobIdentifier, final TestSuiteJobResult jobResult)
    {
        try
        {
            queryUtil.update(new UpdateOnlyQueryHandler("")
            {
                @Override
                public void prepareStatement(final PreparedStatement statement) throws SQLException
                {
                    statement.setString(1, jobIdentifier);
                }
            });
        }
        catch (SQLException e)
        {
            LOGGER.log(Level.WARNING, "Failed to record job result for " + jobResult.getTestClass(), e);
        }
    }
}