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

import com.epickrram.freewheel.messaging.MessagingContext;
import com.epickrram.freewheel.messaging.MessagingContextFactory;
import com.epickrram.freewheel.messaging.ptp.EndPointProvider;
import com.epickrram.freewheel.protocol.CodeBookRegistry;
import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.common.proxy.PropertiesTranslator;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.testing.common.TestExecutionResult;
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.server.Server;
import com.epickrram.romero.testing.common.TestSuiteJobResult;

import java.util.Properties;

public final class ServerConnectionFactory
{
    private final EndPointProvider endPointProvider;

    public ServerConnectionFactory(final EndPointProvider endPointProvider)
    {
        this.endPointProvider = endPointProvider;
    }

    @SuppressWarnings({"unchecked"})
    public <K, D, R> Server<K, D, R> getServer()
    {
        final MessagingContextFactory contextFactory = new MessagingContextFactory();
        final CodeBookRegistry codeBookRegistry = contextFactory.getCodeBookRegistry();
        codeBookRegistry.registerTranslatable(TestExecutionResult.class);
        codeBookRegistry.registerTranslatable(TestStatus.class);
        codeBookRegistry.registerTranslatable(TestSuiteIdentifier.class);
        codeBookRegistry.registerTranslatable(TestSuiteJobResult.class);
        codeBookRegistry.registerTranslatable(BuildStatus.class);
        codeBookRegistry.registerTranslatable(JobDefinitionImpl.class);
        codeBookRegistry.registerTranslator(6000, new PropertiesTranslator(), Properties.class);
        final MessagingContext messagingContext =
                contextFactory.createDirectBlockingPointToPointMessagingContext(endPointProvider);
        return messagingContext.createPublisher(Server.class);
    }
}