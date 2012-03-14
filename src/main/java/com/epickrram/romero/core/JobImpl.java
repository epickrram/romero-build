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

public final class JobImpl<K, R> implements Job<K, R>
{
    private final K key;

    public JobImpl(final K key)
    {
        this.key = key;
    }

    @Override
    public JobState getState()
    {
        return null;
    }

    @Override
    public boolean transitionTo(final JobState newState)
    {
        return false;
    }

    @Override
    public R getResult()
    {
        return null;
    }

    @Override
    public void setResult(final R result)
    {
    }

    @Override
    public K getKey()
    {
        return key;
    }
}
