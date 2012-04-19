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

package com.epickrram.romero.acceptance.framework;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public final class Waiter
{
    private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 30L;
    private static final long DEFAULT_WAIT_INTERVAL_SECONDS = 1L;

    static void waitFor(final Condition condition)
    {
        final long timeoutAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(DEFAULT_WAIT_TIMEOUT_SECONDS);
        while(System.currentTimeMillis() < timeoutAt)
        {
            if(!condition.isMet())
            {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(DEFAULT_WAIT_INTERVAL_SECONDS));
            }
            else
            {
                return;
            }
        }

        throw new IllegalStateException("Condition was not met: " + condition.getFailureMessage());
    }

    public interface Condition
    {
        boolean isMet();
        String getFailureMessage();
    }
}
