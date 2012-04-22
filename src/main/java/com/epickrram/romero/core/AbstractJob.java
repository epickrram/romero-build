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

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractJob<K, R> implements Job<K, R>
{
    private final AtomicReference<JobState> jobState = new AtomicReference<>(JobState.PENDING);
    private final K key;
    private volatile R result;
    private volatile boolean failedToComplete;

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
    public R getResult()
    {
        return result;
    }

    @Override
    public K getKey()
    {
        return key;
    }

    @Override
    public void setResult(final R result, final JobEventListener<K, R> jobEventListener)
    {
        this.result = result;
        final JobState newJobState = getNewJobState(result);
        if(newJobState != null)
        {
            jobState.set(newJobState);
        }
        jobEventListener.onJobUpdate(this);
    }

    @Override
    public void setFailure(final String stackTrace, final JobEventListener<K, R> jobEventListener)
    {
        failedToComplete = true;
        jobState.set(JobState.FINISHED);
        jobEventListener.onJobUpdate(this);
    }

    @Override
    public boolean failedToComplete()
    {
        return failedToComplete;
    }

    protected abstract JobState getNewJobState(final R result);

    @Override
    public String toString()
    {
        return "AbstractJob{" +
                "jobState=" + jobState +
                ", key=" + key +
                ", result=" + result +
                '}';
    }
}