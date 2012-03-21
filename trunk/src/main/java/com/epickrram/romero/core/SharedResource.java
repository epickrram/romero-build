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

import java.util.concurrent.atomic.AtomicInteger;

public final class SharedResource<T> implements Resource<T>
{
    private final int maxConcurrentJobs;
    private final T value;
    private final AtomicInteger currentRunningJobs = new AtomicInteger(0);

    public SharedResource(final int maxConcurrentJobs, final T value)
    {
        this.maxConcurrentJobs = maxConcurrentJobs;
        this.value = value;
    }

    @Override
    public boolean acquire()
    {
        final int currentJobs = currentRunningJobs.get();
        return currentJobs < maxConcurrentJobs && currentRunningJobs.compareAndSet(currentJobs, currentJobs + 1);
    }

    @Override
    public T getValue()
    {
        return value;
    }

    @Override
    public void release()
    {
        currentRunningJobs.decrementAndGet();
    }

    @Override
    public int getMaxConcurrentJobs()
    {
        return maxConcurrentJobs;
    }

    @Override
    public int getCurrentUsageCount()
    {
        return currentRunningJobs.get();
    }
}
