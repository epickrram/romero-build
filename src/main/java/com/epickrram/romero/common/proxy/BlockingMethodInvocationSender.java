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

public final class BlockingMethodInvocationSender implements MethodInvocationSender
{
    private final String remoteHost;
    private final int remotePort;
    private final Serialiser serialiser;
    private final Deserialiser deserialiser;
    private final SocketFactory socketFactory;
    private Socket socket = null;

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
        final OutputStream outputStream = getSocket().getOutputStream();
        final PrintWriter writer = new PrintWriter(outputStream, true);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        serialiser.writeInto(writer, methodRequest);
        writer.write("\n");
        writer.flush();
        final String responseLine = reader.readLine();
        final MethodResponse methodResponse = deserialiser.readMethodResponse(new StringReader(responseLine));
        socket = null;
        return methodResponse;
    }

    private Socket getSocket() throws IOException
    {
        if(socket == null)
        {
            socket = socketFactory.createSocket(remoteHost, remotePort);
        }
        return socket;
    }
}
