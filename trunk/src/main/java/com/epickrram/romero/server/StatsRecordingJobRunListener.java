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

package com.epickrram.romero.server;

import com.epickrram.romero.server.dao.QueryUtil;
import com.epickrram.romero.server.dao.UpdateOnlyQueryHandler;
import com.epickrram.romero.util.LoggingUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class StatsRecordingJobRunListener implements JobRunListener
{
    private static final Logger LOGGER = LoggingUtil.getLogger(StatsRecordingJobRunListener.class);
    private static final String INSERT_JOB_RUN_SQL = "INSERT INTO job_run " +
            "(identifier, start_timestamp, end_timestamp) VALUES (?,?,?)";
    private static final String UPDATE_JOB_RUN_SQL = "UPDATE job_run " +
            "SET end_timestamp = ? WHERE identifier = ? AND end_timestamp = ?";

    private final QueryUtil queryUtil;

    public StatsRecordingJobRunListener(final QueryUtil queryUtil)
    {
        this.queryUtil = queryUtil;
    }

    @Override
    public void jobRunStarted(final String jobIdentifier, final long startTimestamp)
    {
        try
        {
            queryUtil.update(new UpdateOnlyQueryHandler(INSERT_JOB_RUN_SQL)
            {
                @Override
                public void prepareStatement(final PreparedStatement statement) throws SQLException
                {
                    statement.setString(1, jobIdentifier);
                    statement.setLong(2, startTimestamp);
                    statement.setLong(3, -1);
                }
            });
        }
        catch (SQLException e)
        {
            LOGGER.warning("Failed to record job run start: " + e.getMessage());
        }
    }

    @Override
    public void jobRunFinished(final String jobIdentifier)
    {
        try
        {
            queryUtil.update(new UpdateOnlyQueryHandler(UPDATE_JOB_RUN_SQL)
            {
                @Override
                public void prepareStatement(final PreparedStatement statement) throws SQLException
                {
                    statement.setLong(1, System.currentTimeMillis());
                    statement.setString(2, jobIdentifier);
                    statement.setLong(3, -1);
                }
            });
        }
        catch (SQLException e)
        {
            LOGGER.warning("Failed to record job run start: " + e.getMessage());
        }
    }
}