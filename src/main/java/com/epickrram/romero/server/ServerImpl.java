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

import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.common.RunningJob;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobRepository;
import com.epickrram.romero.util.LoggingUtil;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public final class ServerImpl<K, D, R> implements Server<K, D, R>
{
    private static final Logger LOGGER = LoggingUtil.getLogger(ServerImpl.class.getSimpleName());

    private final JobRepository<K, D, R> jobRepository;
    private final KeyFactory<K, R> keyFactory;
    private final JobRunListener jobRunListener;
    private final AtomicReference<BuildStatus> buildStatus = new AtomicReference<>(BuildStatus.WAITING_FOR_NEXT_BUILD);
    private final Map<K, RunningJob<K>> runningJobMap = new ConcurrentHashMap<>();
    private volatile String currentJobRunIdentifier;

    public ServerImpl(final JobRepository<K, D, R> jobRepository, final KeyFactory<K, R> keyFactory,
                      final JobRunListener jobRunListener)
    {
        this.jobRepository = jobRepository;
        this.keyFactory = keyFactory;
        this.jobRunListener = jobRunListener;

        // TODO race condition - can transition straight to waiting for jobs to complete

    }

    @Override
    public void startJobRun(final String identifier)
    {
        if(buildStatus.compareAndSet(BuildStatus.WAITING_FOR_NEXT_BUILD, BuildStatus.BUILDING))
        {
            LOGGER.info("Starting build " + identifier);
            jobRepository.init(identifier);
            jobRunListener.jobRunStarted(identifier, System.currentTimeMillis());
            currentJobRunIdentifier = identifier;
        }
    }

    @Override
    public BuildStatus getStatus()
    {
        determineStatus();
        return buildStatus.get();
    }

    @Override
    public String getCurrentJobRunIdentifier()
    {
        final BuildStatus status = buildStatus.get();
        if(status == BuildStatus.BUILDING || status == BuildStatus.WAITING_FOR_JOBS_TO_COMPLETE)
        {
            return currentJobRunIdentifier;
        }
        return null;
    }

    @Override
    public JobDefinition<K, D> getNextTestToRun(final String agentId)
    {
        final JobDefinition<K, D> job = jobRepository.getJobToRun();
        if(job != null)
        {
            recordRunningJob(agentId, job);
        }
        return job;
    }

    @Override
    public void onJobResult(final R result)
    {
        final K key = keyFactory.getKey(result);
        jobRepository.onJobResult(key, result);
        onJobComplete(key);
    }

    @Override
    public void onJobFailure(final JobDefinition<K, D> testDefinition, final String stackTrace)
    {
        final K key = testDefinition.getKey();
        jobRepository.onJobFailure(key, stackTrace);
        onJobComplete(key);
    }

    @Override
    public Collection<RunningJob<K>> getRunningJobs()
    {
        return runningJobMap.values();
    }

    @Override
    public Integer getJobsRemainingToBeRun()
    {
        return jobRepository.getJobsRemainingToBeRun();
    }

    @Override
    public Integer getTotalJobs()
    {
        return jobRepository.size();
    }

    @Override
    public Integer getNumberOfRunningJobs()
    {
        return runningJobMap.size();
    }

    private void onJobComplete(final K key)
    {
        runningJobMap.remove(key);
        if(jobRepository.areJobsComplete())
        {
            jobRunListener.jobRunFinished(currentJobRunIdentifier);
        }
    }

    private void recordRunningJob(final String agentId, final JobDefinition<K, D> job)
    {
        runningJobMap.put(job.getKey(), RunningJob.<K>create(agentId, job.getKey()));
    }

    private void determineStatus()
    {
        if(!jobRepository.isJobAvailable() &&
                buildStatus.compareAndSet(BuildStatus.BUILDING, BuildStatus.WAITING_FOR_JOBS_TO_COMPLETE))
        {
            LOGGER.info("Waiting for tests to complete");
        }
        else if(jobRepository.areJobsComplete() &&
                buildStatus.compareAndSet(BuildStatus.WAITING_FOR_JOBS_TO_COMPLETE, BuildStatus.WAITING_FOR_NEXT_BUILD))
        {
            LOGGER.info("Waiting for next build");
        }
    }
}