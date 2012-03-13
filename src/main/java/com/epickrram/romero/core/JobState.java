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

import java.util.Arrays;

public enum JobState
{
    CANCELLED(),
    FINISHED_SUCCESS(),
    FINISHED_FAILED(),
    TIMEOUT_CLIENT(),
    TIMEOUT_SERVER(),
    RUNNING(JobState.FINISHED_SUCCESS, JobState.FINISHED_FAILED, JobState.TIMEOUT_CLIENT, JobState.TIMEOUT_SERVER),
    PENDING(JobState.RUNNING, JobState.CANCELLED);

    private final JobState[] validSuccessiveStates;

    JobState(final JobState... validSuccessiveStates)
    {
        this.validSuccessiveStates = validSuccessiveStates;
        Arrays.sort(this.validSuccessiveStates);
    }

    public boolean canTransitionTo(final JobState nextState)
    {
        return Arrays.binarySearch(this.validSuccessiveStates, nextState) >= 0;
    }
}