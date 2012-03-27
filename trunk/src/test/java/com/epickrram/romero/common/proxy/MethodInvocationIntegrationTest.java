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

package com.epickrram.romero.common.proxy;

import org.junit.Test;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class MethodInvocationIntegrationTest
{
    private static final int INT_VALUE = 42;
    private static final double DOUBLE_VALUE = Math.PI;
    private static final int PORT = 17899;
    private static final String HOST = "localhost";
    private static final String STRING_VALUE = "STRING_VALUE";

    @Test
    public void shouldSendRequestAndReceiveResponse() throws Exception
    {
        final RemoteInterfaceImpl implementation = new RemoteInterfaceImpl();
        final MethodInvocationSender invocationSender =
                new BlockingMethodInvocationSender(HOST, PORT, new Serialiser(), new Deserialiser(), SocketFactory.getDefault());
        final ExecutorService executor = Executors.newCachedThreadPool();
        final BlockingMethodInvocationReceiver<RemoteInterface> invocationReceiver = new BlockingMethodInvocationReceiver<>(PORT, implementation, RemoteInterface.class, new Serialiser(), new Deserialiser(), ServerSocketFactory.getDefault(), executor);
        invocationReceiver.start();
        final RemoteInterface remoteInterface = (RemoteInterface) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{RemoteInterface.class}, new PublisherInvocationHandler(invocationSender));

        remoteInterface.noReturnValueNoArgs();
        remoteInterface.noReturnValueSomeArgs(INT_VALUE, STRING_VALUE);

        assertThat(remoteInterface.returnValueNoArgs(), is(DOUBLE_VALUE));
        assertThat(remoteInterface.returnValueSomeArgs(INT_VALUE, STRING_VALUE), is(INT_VALUE));
        assertThat(implementation.noReturnValueNoArgsInvocationCount, is(1));
        assertThat(implementation.noReturnValueSomeArgsInvocationCount, is(1));

        invocationReceiver.stop();
        executor.shutdownNow();
        executor.awaitTermination(30L, TimeUnit.SECONDS);
    }

    interface RemoteInterface
    {
        void noReturnValueNoArgs();
        void noReturnValueSomeArgs(final int foo, final String bar);
        double returnValueNoArgs();
        int returnValueSomeArgs(final int foo, final String bar);
    }

    private static final class RemoteInterfaceImpl implements RemoteInterface
    {
        private int noReturnValueNoArgsInvocationCount = 0;
        private int noReturnValueSomeArgsInvocationCount = 0;
        
        @Override
        public void noReturnValueNoArgs()
        {
            noReturnValueNoArgsInvocationCount++;
        }

        @Override
        public void noReturnValueSomeArgs(final int foo, final String bar)
        {
            noReturnValueSomeArgsInvocationCount++;
        }

        @Override
        public double returnValueNoArgs()
        {
            return DOUBLE_VALUE;
        }

        @Override
        public int returnValueSomeArgs(final int foo, final String bar)
        {
            return INT_VALUE;
        }
    }
}