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

package com.epickrram.romero.common;

import com.epickrram.freewheel.messaging.MessagingContext;
import com.epickrram.freewheel.messaging.MessagingContextFactory;
import com.epickrram.freewheel.messaging.ptp.EndPointProvider;
import com.epickrram.freewheel.protocol.CodeBookRegistry;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.testing.common.TestExecutionResult;
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;

public abstract class AbstractRomeroModule
{
    private final EndPointProvider endPointProvider;
    private volatile MessagingContext messagingContext;

    protected AbstractRomeroModule(final EndPointProvider endPointProvider)
    {
        this.endPointProvider = endPointProvider;
    }

    public final void shutdown()
    {
        if(messagingContext != null)
        {
            messagingContext.stop();
        }
    }
    
    public final void initialise()
    {
        final MessagingContextFactory contextFactory = new MessagingContextFactory();
        final CodeBookRegistry codeBookRegistry = contextFactory.getCodeBookRegistry();
        codeBookRegistry.registerTranslatable(TestExecutionResult.class);
        codeBookRegistry.registerTranslatable(TestStatus.class);
        codeBookRegistry.registerTranslatable(TestSuiteIdentifier.class);
        codeBookRegistry.registerTranslatable(TestSuiteJobResult.class);
        codeBookRegistry.registerTranslatable(BuildStatus.class);
        codeBookRegistry.registerTranslatable(JobDefinitionImpl.class);
        registerTranslatables(codeBookRegistry);
        messagingContext = contextFactory.createDirectBlockingPointToPointMessagingContext(endPointProvider);
        beforeMessagingStart(messagingContext);
        messagingContext.start();
    }

    protected abstract void registerTranslatables(final CodeBookRegistry codeBookRegistry);
    protected abstract void beforeMessagingStart(final MessagingContext messagingContext);
}
