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

package com.epickrram.romero.testing.server;

import com.epickrram.romero.core.Job;
import com.epickrram.romero.core.JobEventListener;
import com.epickrram.romero.server.JobRunListener;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.testing.server.dao.TestSuiteJobDao;
import com.epickrram.romero.testing.server.dao.TestSuiteJobDaoImpl;

public final class TestSuiteJobEventListener implements
        JobEventListener<TestSuiteIdentifier, TestSuiteJobResult>,
        JobRunListener
{
    private final TestSuiteJobDao testSuiteJobDao;

    private volatile long startTimestamp = 0;
    private volatile String currentJobRun = null;

    public TestSuiteJobEventListener(final TestSuiteJobDao testSuiteJobDao)
    {
        this.testSuiteJobDao = testSuiteJobDao;
    }

    @Override
    public void onJobUpdate(final Job<TestSuiteIdentifier, TestSuiteJobResult> updatedJob)
    {
        if(startTimestamp != 0 && updatedJob.getState().isComplete())
        {
            if(updatedJob.failedToComplete())
            {
                testSuiteJobDao.onTestSuiteFailureToComplete(currentJobRun, updatedJob.getKey());
            }
            else
            {
                testSuiteJobDao.onTestSuiteJobResult(currentJobRun, updatedJob.getResult());
            }
        }
    }

    @Override
    public void jobRunStarted(final String jobIdentifier, final long startTimestamp)
    {
        this.currentJobRun = jobIdentifier;
        this.startTimestamp = startTimestamp;
    }

    @Override
    public void jobRunFinished(final String jobIdentifier)
    {
    }
}
