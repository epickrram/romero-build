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

package com.epickrram.romero.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public final class JobRepositoryImpl<K, R> implements JobRepository<K, R>
{
    private final JobLoader<K, R> jobLoader;
    private final Map<K, Job<K, R>> jobMap;

    public JobRepositoryImpl(final JobLoader<K, R> jobLoader)
    {
        this.jobLoader = jobLoader;
        jobMap = new ConcurrentSkipListMap<>();
    }

    @Override
    public void init(final String identifier)
    {
        jobMap.clear();
        final List<Job<K,R>> jobList = jobLoader.loadJobs(identifier);
        for (Job<K, R> job : jobList)
        {
            jobMap.put(job.getKey(), job);
        }
    }

    @Override
    public Job<K, R> getJobToRun()
    {
        for (Job<K, R> job : jobMap.values())
        {
            if(job.transitionTo(JobState.RUNNING))
            {
                return job;
            }
        }
        return null;
    }

    @Override
    public boolean isJobAvailable()
    {
        for (Job<K, R> job : jobMap.values())
        {
            if(job.getState() == JobState.PENDING)
            {
                return true;
            }
        }
        return false;
    }
}
