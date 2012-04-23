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

import com.epickrram.romero.util.LoggingUtil;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CompositeJobRunListener implements JobRunListener
{
    private static final Logger LOGGER = LoggingUtil.getLogger(CompositeJobRunListener.class);

    private final Collection<JobRunListener> delegates = new CopyOnWriteArrayList<>();

    @Override
    public void jobRunStarted(final String jobIdentifier, final long startTimestamp)
    {
        for (JobRunListener delegate : delegates)
        {
            try
            {
                delegate.jobRunStarted(jobIdentifier, startTimestamp);
            }
            catch(RuntimeException e)
            {
                LOGGER.log(Level.WARNING, "Failed to invoke delegate", e);
            }
        }
    }

    @Override
    public void jobRunFinished(final String jobIdentifier)
    {
        for (JobRunListener delegate : delegates)
        {
            try
            {
                delegate.jobRunFinished(jobIdentifier);
            }
            catch(RuntimeException e)
            {
                LOGGER.log(Level.WARNING, "Failed to invoke delegate", e);
            }
        }
    }

    public void addDelegate(final JobRunListener jobRunListener)
    {
        delegates.add(jobRunListener);
    }
}
