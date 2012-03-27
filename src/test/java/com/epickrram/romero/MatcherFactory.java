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

import com.epickrram.romero.common.TestSuiteJob;
import com.epickrram.romero.core.JobState;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

import java.util.Arrays;

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
}
