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
import java.util.logging.Logger;

public final class JobRepositoryImpl<K, D, R> implements JobRepository<K, D, R>
{
    private static final Logger LOGGER = Logger.getLogger(JobRepositoryImpl.class.getSimpleName());
    private final JobDefinitionLoader<K, D> jobDefinitionLoader;
    private final Map<K, Job<K, R>> jobMap;
    private final Map<K, JobDefinition<K, D>> jobDefinitionMap;
    private final JobFactory<K, D, R> jobFactory;

    public JobRepositoryImpl(final JobDefinitionLoader<K, D> jobDefinitionLoader,
                             final JobFactory<K, D, R> jobFactory)
    {
        this.jobDefinitionLoader = jobDefinitionLoader;
        this.jobFactory = jobFactory;
        jobMap = new ConcurrentSkipListMap<>();
        jobDefinitionMap = new ConcurrentSkipListMap<>();
    }

    @Override
    public void init(final String identifier)
    {
        jobMap.clear();
        jobDefinitionMap.clear();
        final List<JobDefinition<K,D>> jobList = jobDefinitionLoader.loadJobDefinitions(identifier);
        for (JobDefinition<K, D> jobDefinition : jobList)
        {
            jobDefinitionMap.put(jobDefinition.getKey(), jobDefinition);
            jobMap.put(jobDefinition.getKey(), jobFactory.createJob(jobDefinition));
        }
    }

    @Override
    public JobDefinition<K, D> getJobToRun()
    {
        for (Job<K, R> job : jobMap.values())
        {
            if(job.transitionTo(JobState.RUNNING))
            {
                return jobDefinitionMap.get(job.getKey());
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

    @Override
    public boolean areJobsComplete()
    {
        for (Job<K, R> job : jobMap.values())
        {
            if(!job.getState().isComplete())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onJobResult(final K key, final R result)
    {
        final Job<K, R> job = jobMap.get(key);
        if(job != null)
        {
            job.addResult(result);
        }
        else
        {
            LOGGER.warning("Received result for unknown job: " + key);
        }
    }
}