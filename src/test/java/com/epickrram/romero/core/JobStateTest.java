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

import org.junit.Test;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class JobStateTest
{
    @Test
    public void shouldTransitionFromPendingToRunningOrCancelled() throws Exception
    {
        assertValidTransition(JobState.PENDING, JobState.RUNNING, JobState.CANCELLED);
    }

    @Test
    public void shouldTransitionFromRunningToTimeoutOrFinished() throws Exception
    {
        assertValidTransition(JobState.RUNNING, JobState.TIMEOUT_SERVER, JobState.TIMEOUT_CLIENT,
                                                JobState.FINISHED);
    }

    @Test
    public void shouldNotTransitionFromCancelled() throws Exception
    {
        assertValidTransition(JobState.CANCELLED);
    }

    @Test
    public void shouldNotTransitionFromFinished() throws Exception
    {
        assertValidTransition(JobState.FINISHED);
    }

    @Test
    public void shouldNotTransitionFromTimeoutClient() throws Exception
    {
        assertValidTransition(JobState.TIMEOUT_CLIENT);
    }

    @Test
    public void shouldNotTransitionFromTimeoutServer() throws Exception
    {
        assertValidTransition(JobState.TIMEOUT_SERVER);
    }

    private void assertValidTransition(final JobState startState, final JobState... nextStates)
    {
        sort(nextStates);
        for (JobState nextState : nextStates)
        {
            assertThat(startState.canTransitionTo(nextState), is(true));
        }
        for(JobState possibleState : JobState.values())
        {
            if(binarySearch(nextStates, possibleState) < 0)
            {
                assertThat(startState.canTransitionTo(possibleState), is(false));
            }
        }
    }
}