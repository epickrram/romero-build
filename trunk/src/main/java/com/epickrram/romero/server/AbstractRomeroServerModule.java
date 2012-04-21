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

import com.epickrram.freewheel.messaging.ptp.EndPoint;
import com.epickrram.freewheel.messaging.ptp.EndPointProvider;
import com.epickrram.romero.common.AbstractRomeroModule;

import java.net.InetAddress;

public abstract class AbstractRomeroServerModule extends AbstractRomeroModule
{
    public AbstractRomeroServerModule(final int serverAppPort)
    {
        super(new LocalEndPointProvider(serverAppPort));
    }

    private static class LocalEndPointProvider implements EndPointProvider
    {
        private final int serverAppPort;

        public LocalEndPointProvider(final int serverAppPort)
        {
            this.serverAppPort = serverAppPort;
        }

        @Override
        public EndPoint resolveEndPoint(final Class descriptor)
        {
            return new EndPoint(InetAddress.getLoopbackAddress(), serverAppPort);
        }
    }
}
