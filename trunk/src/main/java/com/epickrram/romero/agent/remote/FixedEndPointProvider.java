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

package com.epickrram.romero.agent.remote;

import com.epickrram.freewheel.messaging.ptp.EndPoint;
import com.epickrram.freewheel.messaging.ptp.EndPointProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class FixedEndPointProvider implements EndPointProvider
{
    private final String remoteHost;
    private final int remotePort;
    private EndPoint endPoint;

    public FixedEndPointProvider(final String remoteHost, final int remotePort)
    {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public EndPoint resolveEndPoint(final Class aClass)
    {
        if(endPoint == null)
        {
            try
            {
                endPoint = new EndPoint(InetAddress.getByName(remoteHost), remotePort);
            }
            catch (UnknownHostException e)
            {
                throw new IllegalArgumentException("Cannot find host: " + remoteHost, e);
            }
        }
        return endPoint;
    }
}
