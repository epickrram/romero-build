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

import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.common.TestSuiteIdentifier;
import com.epickrram.romero.common.proxy.*;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobDefinitionImpl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.epickrram.romero.common.TestSuiteIdentifier.toMapKey;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class ServerImplProxyIntegrationTest
{
    private static final int PORT = 17899;
    private static final String HOST = "localhost";
    private static final BuildStatus STATUS = BuildStatus.BUILDING;
    private static final String IDENTIFIER = "123456";
    private static final String AGENT_ID = "agent-id";

    @Mock
    private Server server;
    private Server serverProxy;
    private BlockingMethodInvocationReceiver<Server> invocationReceiver;
    private ExecutorService executor;

    @Before
    public void setup()
    {
        serverProxy = new ProxyFactory().createProxy(Server.class, HOST, PORT);
        executor = Executors.newCachedThreadPool();
        invocationReceiver = new BlockingMethodInvocationReceiver<>(PORT, server, Server.class,
                new Serialiser(), new Deserialiser(), ServerSocketFactory.getDefault(), executor);
        invocationReceiver.start();
    }

    @After
    public void teardown()
    {
        invocationReceiver.stop();
        executor.shutdown();
    }

    @Test
    public void shouldReturnStatus() throws Exception
    {
        when(server.getStatus()).thenReturn(STATUS);

        assertThat(serverProxy.getStatus(), is(STATUS));

        verify(server).getStatus();
    }

    @Test
    public void shouldStartTestRun() throws Exception
    {
        serverProxy.startTestRun(IDENTIFIER);

        verify(server).startTestRun(IDENTIFIER);
    }

    @Test
    public void shouldGetCurrentBuildId() throws Exception
    {
        when(server.getCurrentBuildId()).thenReturn(IDENTIFIER);

        assertThat(serverProxy.getCurrentBuildId(), is(IDENTIFIER));

        verify(server).getCurrentBuildId();
    }

    @Test
    public void shouldReturnNextTestToRun() throws Exception
    {
        final JobDefinition<TestSuiteIdentifier, Properties> definition =
                new JobDefinitionImpl<>(toMapKey(getClass().getName()), new Properties());
        when(server.getNextTestToRun(IDENTIFIER)).thenReturn(definition);

        assertThat(serverProxy.getNextTestToRun(IDENTIFIER), equalTo(definition));
    }
}
