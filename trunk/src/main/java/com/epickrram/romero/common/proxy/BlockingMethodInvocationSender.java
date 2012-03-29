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

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Logger;

public final class BlockingMethodInvocationSender implements MethodInvocationSender
{
    private static final Logger LOGGER = Logger.getLogger(BlockingMethodInvocationSender.class.getSimpleName());
    private final String remoteHost;
    private final int remotePort;
    private final Serialiser serialiser;
    private final Deserialiser deserialiser;
    private final SocketFactory socketFactory;
    private Socket connectedSocket = null;

    public BlockingMethodInvocationSender(final String remoteHost, final int remotePort,
                                          final Serialiser serialiser, final Deserialiser deserialiser,
                                          final SocketFactory socketFactory)
    {
        this.serialiser = serialiser;
        this.deserialiser = deserialiser;
        this.socketFactory = socketFactory;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public MethodResponse invoke(final MethodRequest methodRequest) throws IOException
    {
        final Socket socket = getConnectedSocket();
        final OutputStream outputStream = socket.getOutputStream();
        final PrintWriter writer = new PrintWriter(outputStream, true);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        serialiser.writeInto(writer, methodRequest);
        writer.write("\n");
        writer.flush();
        final String responseLine = reader.readLine();
        final MethodResponse methodResponse = deserialiser.readMethodResponse(new StringReader(responseLine));
        destroyConnectedSocket();
        return methodResponse;
    }

    private void destroyConnectedSocket()
    {
        if(connectedSocket != null)
        {
            try
            {
                connectedSocket.close();
            }
            catch (IOException e)
            {
                LOGGER.warning("Unable to close connectedSocket");
            }
            connectedSocket = null;
        }
    }

    private Socket getConnectedSocket() throws IOException
    {
        if(connectedSocket == null)
        {
            int retriesLeft = 3;
            for (int i = retriesLeft; i != 0; --i)
            {
                try
                {
                    connectedSocket = socketFactory.createSocket(remoteHost, remotePort);
                    break;
                }
                catch (IOException e)
                {
                    LOGGER.warning("Failed to connect to server, retries remaining: " + i);
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            }
            if(connectedSocket == null)
            {
                throw new IOException("Unable to connect to " + remoteHost + ":" + remotePort);
            }
        }
        return connectedSocket;
    }
}
