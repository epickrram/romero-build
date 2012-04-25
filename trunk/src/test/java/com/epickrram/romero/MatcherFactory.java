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

package com.epickrram.romero;

import com.epickrram.romero.common.RunningJob;
import com.epickrram.romero.core.JobState;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJob;
import com.epickrram.romero.testing.server.web.TestRunSummary;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.util.Arrays;
import java.util.Collection;

public final class MatcherFactory
{
    private MatcherFactory() {}

    public static Matcher<TestSuiteJob> testCaseJobsWithStates(final String expectedTestClassName,
                                                              final JobState... expectedJobStates)
    {
        return new TypeSafeMatcher<TestSuiteJob>()
        {
            @Override
            public boolean matchesSafely(final TestSuiteJob testSuiteJob)
            {
                for(int i = 0, n = expectedJobStates.length; i < n; i++)
                {
                    if(testSuiteJob.getKey().getTestClass().equals(expectedTestClassName) &&
                       testSuiteJob.getState() == expectedJobStates[i])
                    {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("class: " + expectedTestClassName);
                description.appendText("\nstates: " + Arrays.toString(expectedJobStates));
            }
        };
    }

    public static Matcher<Collection<RunningJob<TestSuiteIdentifier>>> runningJobs(final RunningJob... expected)
    {
        return new TypeSafeMatcher<Collection<RunningJob<TestSuiteIdentifier>>>()
        {
            @Override
            public boolean matchesSafely(final Collection<RunningJob<TestSuiteIdentifier>> runningJobs)
            {
                if((expected == null || expected.length == 0) && runningJobs.isEmpty())
                {
                    return true;
                }
                if(expected.length != runningJobs.size())
                {
                    return false;
                }
                int matchCount = 0;
                for (int i = 0; i < expected.length; i++)
                {
                    final RunningJob runningJob = expected[i];
                    for (RunningJob<TestSuiteIdentifier> actual : runningJobs)
                    {
                        if(runningJob.getJobKey().equals(actual.getJobKey()) &&
                           runningJob.getAgentId().equals(actual.getAgentId()))
                        {
                            matchCount++;
                        }
                    }
                }
                return matchCount == runningJobs.size();
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("running jobs matching:\n" + Arrays.toString(expected));
            }
        };
    }

    public static Matcher<TestRunSummary> summary(final String jobId, final long startTimestamp)
    {
        return new TypeSafeMatcher<TestRunSummary>()
        {
            @Override
            public boolean matchesSafely(final TestRunSummary testRunSummary)
            {
                return jobId.equals(testRunSummary.getJobRunIdentifier()) &&
                       startTimestamp == testRunSummary.getStartTimestamp();
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("jobId = ").appendText(jobId).
                        appendText(", startTimestamp = ").appendText(Long.toString(startTimestamp));
            }
        };
    }
}
