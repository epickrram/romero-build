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
import com.epickrram.romero.common.TestSuiteIdentifier;
import com.epickrram.romero.common.TestSuiteJobResult;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static com.epickrram.romero.common.TestSuiteIdentifier.toMapKey;

public final class ServerImpl implements Server
{
    private static final Logger LOGGER = Logger.getLogger(ServerImpl.class.getSimpleName());

    private final JobRepository<TestSuiteIdentifier, Properties, TestSuiteJobResult> jobRepository;
    private final AtomicReference<BuildStatus> buildStatus = new AtomicReference<>(BuildStatus.WAITING_FOR_NEXT_BUILD);
    private final Map<TestSuiteIdentifier, RunningJob<TestSuiteIdentifier>> runningJobMap = new ConcurrentHashMap<>();
    private volatile String currentBuildId;

    public ServerImpl(final JobRepository<TestSuiteIdentifier, Properties, TestSuiteJobResult> jobRepository)
    {
        this.jobRepository = jobRepository;
    }

    @Override
    public void startTestRun(final String identifier)
    {
        if(buildStatus.compareAndSet(BuildStatus.WAITING_FOR_NEXT_BUILD, BuildStatus.BUILDING))
        {
            LOGGER.info("Starting build " + identifier);
            jobRepository.init(identifier);
            currentBuildId = identifier;
        }
    }

    @Override
    public BuildStatus getStatus()
    {
        determineStatus();
        return buildStatus.get();
    }

    @Override
    public String getCurrentBuildId()
    {
        final BuildStatus status = buildStatus.get();
        if(status == BuildStatus.BUILDING || status == BuildStatus.WAITING_FOR_TESTS_TO_COMPLETE)
        {
            return currentBuildId;
        }
        return null;
    }

    @Override
    public JobDefinition<TestSuiteIdentifier, Properties> getNextTestToRun(final String agentId)
    {
        final JobDefinition<TestSuiteIdentifier, Properties> job = jobRepository.getJobToRun();
        if(job != null)
        {
            recordRunningJob(agentId, job);
        }
        return job;
    }

    @Override
    public void onTestCaseJobResult(final TestSuiteJobResult testSuiteJobResult)
    {
        recordFinishedJob(testSuiteJobResult.getTestClass());
        final TestSuiteIdentifier testSuiteIdentifier = toMapKey(testSuiteJobResult.getTestClass());
        jobRepository.onJobResult(testSuiteIdentifier, testSuiteJobResult);
    }

    @Override
    public Collection<RunningJob<TestSuiteIdentifier>> getRunningJobs()
    {
        return runningJobMap.values();
    }

    @Override
    public int getRemainingJobs()
    {
        return 0;
    }

    @Override
    public int getTotalJobs()
    {
        return jobRepository.size();
    }

    private void recordRunningJob(final String agentId, final JobDefinition<TestSuiteIdentifier, Properties> job)
    {
        runningJobMap.put(job.getKey(), RunningJob.<TestSuiteIdentifier>create(agentId, job.getKey()));
    }

    private void recordFinishedJob(final String testSuite)
    {
        runningJobMap.remove(TestSuiteIdentifier.toMapKey(testSuite));
    }

    private void determineStatus()
    {
        if(!jobRepository.isJobAvailable() &&
                buildStatus.compareAndSet(BuildStatus.BUILDING, BuildStatus.WAITING_FOR_TESTS_TO_COMPLETE))
        {
            LOGGER.info("Waiting for tests to complete");
        }
        else if(jobRepository.areJobsComplete() &&
                buildStatus.compareAndSet(BuildStatus.WAITING_FOR_TESTS_TO_COMPLETE, BuildStatus.WAITING_FOR_NEXT_BUILD))
        {
            LOGGER.info("Waiting for next build");
        }
    }
}