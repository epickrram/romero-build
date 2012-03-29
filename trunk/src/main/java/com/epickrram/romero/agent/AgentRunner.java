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

import com.epickrram.romero.agent.junit.JUnitTestExecutor;
import com.epickrram.romero.common.proxy.BlockingMethodInvocationSender;
import com.epickrram.romero.common.proxy.Deserialiser;
import com.epickrram.romero.common.proxy.PublisherInvocationHandler;
import com.epickrram.romero.common.proxy.Serialiser;
import com.epickrram.romero.server.Server;
import org.junit.runner.JUnitCore;

import javax.net.SocketFactory;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class AgentRunner
{
    private final String serverHost;
    private final int serverPort;
    private final String agentId;

    public static void main(String[] args)
    {
        final AgentRunner agentRunner = new AgentRunner("localhost", 9001, "agent-1");
        agentRunner.start();
    }

    private AgentRunner(final String serverHost, final int serverPort, final String agentId)
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.agentId = agentId;
    }

    private void start()
    {
        final BlockingMethodInvocationSender sender = new BlockingMethodInvocationSender(serverHost, serverPort, new Serialiser(), new Deserialiser(), SocketFactory.getDefault());
        final PublisherInvocationHandler invocationHandler = new PublisherInvocationHandler(sender);
        final Server server = (Server) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Server.class}, invocationHandler);

        final Agent agent = new Agent(server, new JUnitTestExecutor(new JUnitCore(), new TestCaseJobResultHandlerImpl(server)), new SleeperImpl(), agentId);

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(agent, 0, 1, TimeUnit.MILLISECONDS);
    }
}
