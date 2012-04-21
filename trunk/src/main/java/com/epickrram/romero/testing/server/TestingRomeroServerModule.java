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

package com.epickrram.romero.testing.server;

import com.epickrram.freewheel.messaging.MessagingContext;
import com.epickrram.freewheel.protocol.CodeBookRegistry;
import com.epickrram.romero.common.proxy.PropertiesTranslator;
import com.epickrram.romero.server.AbstractRomeroServerModule;
import com.epickrram.romero.server.Server;

import java.util.Properties;

public final class TestingRomeroServerModule extends AbstractRomeroServerModule
{
    private final Server server;

    public TestingRomeroServerModule(final int serverAppPort, final Server server)
    {
        super(serverAppPort);
        this.server = server;
    }

    @Override
    protected void registerTranslatables(final CodeBookRegistry codeBookRegistry)
    {
        codeBookRegistry.registerTranslator(6000, new PropertiesTranslator(), Properties.class);
    }

    @Override
    protected void beforeMessagingStart(final MessagingContext messagingContext)
    {
        messagingContext.createSubscriber(Server.class, server);
    }
}
