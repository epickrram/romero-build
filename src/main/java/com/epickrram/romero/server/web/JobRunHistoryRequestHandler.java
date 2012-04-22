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

package com.epickrram.romero.server.web;

import com.epickrram.romero.server.JobRun;
import com.epickrram.romero.server.dao.JobRunDao;

import java.util.List;

public final class JobRunHistoryRequestHandler extends VoidInputRequestHandler<List<JobRun>>
{
    private static final int MAX_RESULTS = 20;
    private final JobRunDao jobRunDao;

    public JobRunHistoryRequestHandler(final JobRunDao jobRunDao)
    {
        this.jobRunDao = jobRunDao;
    }

    @Override
    List<JobRun> handleRequest()
    {
        return jobRunDao.getHistory(MAX_RESULTS);
    }
}
