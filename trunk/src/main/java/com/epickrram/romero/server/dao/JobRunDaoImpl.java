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

package com.epickrram.romero.server.dao;

import com.epickrram.romero.server.JobRun;
import com.epickrram.romero.util.LoggingUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public final class JobRunDaoImpl implements JobRunDao
{
    private static final Logger LOGGER = LoggingUtil.getLogger(JobRunDaoImpl.class);
    private static final StartTimeAscendingJobRunComparator COMPARATOR = new StartTimeAscendingJobRunComparator();
    private final QueryUtil queryUtil;

    public JobRunDaoImpl(final QueryUtil queryUtil)
    {
        this.queryUtil = queryUtil;
    }

    @Override
    public List<JobRun> getHistory(final int maxResults)
    {
        try
        {
            return queryUtil.query(new QueryHandler<List<JobRun>>("SELECT * FROM job_run WHERE end_timestamp > 0")
            {
                @Override
                public void prepareStatement(final PreparedStatement statement) throws SQLException
                {
                }

                @Override
                public List<JobRun> handleResult(final ResultSet resultSet) throws SQLException
                {
                    final List<JobRun> jobRunList = new ArrayList<>();

                    while(resultSet.next())
                    {
                        final int id = resultSet.getInt("id");
                        final String jobRunIdentifier = resultSet.getString("identifier");
                        final long startTimestamp = resultSet.getLong("start_timestamp");
                        final long endTimestamp = resultSet.getLong("end_timestamp");

                        jobRunList.add(new JobRun(id, jobRunIdentifier, startTimestamp, endTimestamp));
                    }

                    Collections.sort(jobRunList, COMPARATOR);
                    return jobRunList;
                }
            });
        }
        catch (SQLException e)
        {
            LOGGER.warning("Failed to load job run history: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private static class StartTimeAscendingJobRunComparator implements Comparator<JobRun>
    {
        @Override
        public int compare(final JobRun o1, final JobRun o2)
        {
            return Long.compare(o1.getStartTimestamp(), o2.getStartTimestamp());
        }
    }
}
