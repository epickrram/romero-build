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

package com.epickrram.romero.agent;

public final class RestartAwareStatusProvider implements StatusProvider
{
    private final StatusProvider delegate;
    private final AgentRestartMonitor monitor;

    public RestartAwareStatusProvider(final StatusProvider delegate,
                                      final AgentRestartMonitor monitor)
    {
        this.delegate = delegate;
        this.monitor = monitor;
    }

    @Override
    public Status getStatus()
    {
        final Status status = delegate.getStatus();
        if(status == Status.IDLE && monitor.isRestartRequired(System.currentTimeMillis()))
        {
            return Status.WAITING_FOR_RESTART;
        }
        
        return status;
    }
}
