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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractJob<K, R> implements Job<K, R>
{
    private final AtomicReference<JobState> jobState = new AtomicReference<>(JobState.PENDING);
    private final Collection<R> resultList = new CopyOnWriteArrayList<>();
    private final K key;

    public AbstractJob(final K key)
    {
        this.key = key;
    }

    @Override
    public JobState getState()
    {
        return jobState.get();
    }

    @Override
    public boolean transitionTo(final JobState newState)
    {
        final JobState currentState = jobState.get();
        return currentState.canTransitionTo(newState) && jobState.compareAndSet(currentState, newState);
    }

    @Override
    public Collection<R> getResultList()
    {
        return resultList;
    }

    @Override
    public void addResult(final R result, final JobEventListener<K, R> jobEventListener)
    {
        resultList.add(result);
        final JobState newJobState = getNewJobState(result);
        if(newJobState != null)
        {
            jobState.set(newJobState);
        }
    }

    @Override
    public K getKey()
    {
        return key;
    }

    protected abstract JobState getNewJobState(final R result);
}