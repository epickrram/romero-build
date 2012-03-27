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

import javax.net.ServerSocketFactory;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import static com.epickrram.romero.common.proxy.SerialisationUtil.coerceValue;

public final class BlockingMethodInvocationReceiver<T>
{
    private static final Logger LOGGER = Logger.getLogger(BlockingMethodInvocationReceiver.class.getSimpleName());

    private final int port;
    private final T implementation;
    private final Class<T> remoteInterfaceClass;
    private final Serialiser serialiser;
    private final Deserialiser deserialiser;
    private final ServerSocketFactory serverSocketFactory;
    private final Executor executor;
    private volatile BlockingMethodInvocationReceiver<T>.MessageReceiver messageReceiver;

    public BlockingMethodInvocationReceiver(final int port, final T implementation, final Class<T> remoteInterfaceClass,
                                            final Serialiser serialiser, final Deserialiser deserialiser,
                                            final ServerSocketFactory serverSocketFactory, final Executor executor)
    {
        this.port = port;
        this.implementation = implementation;
        this.remoteInterfaceClass = remoteInterfaceClass;
        this.serialiser = serialiser;
        this.deserialiser = deserialiser;
        this.serverSocketFactory = serverSocketFactory;
        this.executor = executor;
    }

    public void start()
    {
        final CountDownLatch latch = new CountDownLatch(1);
        messageReceiver = new MessageReceiver(latch);
        executor.execute(messageReceiver);
    }

    public void stop()
    {
        messageReceiver.stop();
    }

    private final class MessageReceiver implements Runnable
    {
        private final CountDownLatch latch;
        private volatile ServerSocket serverSocket;

        public MessageReceiver(final CountDownLatch latch)
        {
            this.latch = latch;
        }

        void stop()
        {
            if(serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                    serverSocket = null;
                }
                catch (IOException e)
                {
                    throw new IllegalStateException("Unable to close socket", e);
                }
            }
        }

        @Override
        public void run()
        {
            try
            {
                LOGGER.info("Creating listener for " + remoteInterfaceClass.getName() + " on port " + port);
                latch.countDown();
                serverSocket = serverSocketFactory.createServerSocket(port);
                while(!Thread.currentThread().isInterrupted())
                {
                    final Socket socket = serverSocket.accept();
                    final InputStream inputStream = socket.getInputStream();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    final PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    final String line = reader.readLine();
                    final MethodRequest methodRequest = deserialiser.readMethodRequest(new StringReader(line));
                    final Method method = getMethod(methodRequest);
                    final Object[] args = toArguments(methodRequest, method.getParameterTypes());
                    final Object result = method.invoke(implementation, args);
                    final MethodResponse response = new MethodResponse(null, result);
                    serialiser.writeInto(writer, response);
                    writer.write("\n");
                    writer.flush();
                    socket.close();
                }
            }
            catch (IOException | InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private Object[] toArguments(final MethodRequest methodRequest, final Class<?>[] parameterTypes)
    {
        if(methodRequest.getArguments() == null)
        {
            return null;
        }
        final Object[] args = new Object[methodRequest.getArguments().length];
        for (int i = 0; i < methodRequest.getArguments().length; i++)
        {
            args[i] = coerceValue(methodRequest.getArguments()[i], parameterTypes[i]);
        }
        return args;
    }

    private Method getMethod(final MethodRequest methodRequest)
    {
        final Method[] methods = remoteInterfaceClass.getMethods();
        for (Method method : methods)
        {
            if(methodRequest.getMethodName().equals(method.getName()))
            {
                return method;
            }
        }
        throw new IllegalArgumentException("No method called " + methodRequest.getMethodName());
    }
}