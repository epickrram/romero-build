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

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.messaging.MessagingContext;
import com.epickrram.freewheel.messaging.MessagingContextFactory;
import com.epickrram.freewheel.messaging.ptp.EndPointProvider;
import com.epickrram.freewheel.protocol.CodeBookRegistry;
import com.epickrram.freewheel.protocol.Translator;
import com.epickrram.romero.agent.remote.FixedEndPointProvider;
import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class ServerImplProxyIntegrationTest
{
    private static final int PORT = 17899;
    private static final String HOST = "localhost";
    private static final BuildStatus STATUS = BuildStatus.BUILDING;
    private static final String IDENTIFIER = "123456";
    private static final String AGENT_ID = "agent-id";

    @Mock
    private Server<TestSuiteIdentifier, Properties, TestSuiteJobResult> server;
    private Server<TestSuiteIdentifier, Properties, TestSuiteJobResult> serverProxy;
    private MessagingContext messagingContext;

    @SuppressWarnings({"unchecked"})
    @Before
    public void setup()
    {
        final EndPointProvider endPointProvider = new FixedEndPointProvider(HOST, PORT);
        final MessagingContextFactory contextFactory = new MessagingContextFactory();
        registerCodeBookEntries(contextFactory);
        messagingContext = contextFactory.createDirectBlockingPointToPointMessagingContext(endPointProvider);
        messagingContext.createSubscriber(Server.class, server);
        serverProxy = messagingContext.createPublisher(Server.class);
        messagingContext.start();
    }

    @After
    public void teardown()
    {
        messagingContext.stop();
    }
    
    @Test
    public void shouldReturnStatus() throws Exception
    {
        when(server.getStatus()).thenReturn(STATUS);

        final BuildStatus status = serverProxy.getStatus();
        assertThat(status, is(STATUS));

        verify(server).getStatus();
    }

    @Test
    public void shouldStartTestRun() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);
        doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable
            {
                latch.countDown();
                return null;
            }
        }).when(server).startJobRun(IDENTIFIER);
        
        serverProxy.startJobRun(IDENTIFIER);

        latch.await();

        verify(server).startJobRun(IDENTIFIER);
    }

    @Test
    public void shouldGetCurrentBuildId() throws Exception
    {
        when(server.getCurrentJobRunIdentifier()).thenReturn(IDENTIFIER);

        assertThat(serverProxy.getCurrentJobRunIdentifier(), is(IDENTIFIER));

        verify(server).getCurrentJobRunIdentifier();
    }

    @Test
    public void shouldReturnNextTestToRun() throws Exception
    {
        final JobDefinition<TestSuiteIdentifier, Properties> definition =
                new JobDefinitionImpl<>(toMapKey(getClass().getName()), new Properties());
        when(server.getNextTestToRun(AGENT_ID)).thenReturn(definition);

        assertThat(serverProxy.getNextTestToRun(AGENT_ID), equalTo(definition));
    }

    private void registerCodeBookEntries(final MessagingContextFactory contextFactory)
    {
        final CodeBookRegistry codeBookRegistry = contextFactory.getCodeBookRegistry();
        codeBookRegistry.registerTranslatable(BuildStatus.class);
        codeBookRegistry.registerTranslatable(JobDefinitionImpl.class);
        codeBookRegistry.registerTranslatable(TestSuiteIdentifier.class);
        codeBookRegistry.registerTranslator(5010, new Translator<Properties>()
        {
            @Override
            public void encode(final Properties encodable, final EncoderStream encoderStream)
            {
            }

            @Override
            public Properties decode(final DecoderStream decoderStream)
            {

                return new Properties();
            }
        }, Properties.class);
    }
}
