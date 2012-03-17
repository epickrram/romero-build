package com.epickrram.romero.agent;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AgentStatusServerImpl implements AgentStatusServer
{
    private static final Logger LOGGER = Logger.getLogger(AgentStatusServerImpl.class.getSimpleName());

    private final int port;
    private final StatusProvider statusProvider;
    private final Executor executor;

    public AgentStatusServerImpl(final int port,
                                 final StatusProvider statusProvider,
                                 final Executor executor)
    {
        this.port = port;
        this.statusProvider = statusProvider;
        this.executor = executor;
    }

    @Override
    public void start()
    {
        executor.execute(new SocketListener(port, statusProvider));
    }

    private static final class SocketListener implements Runnable
    {
        private final int port;
        private final StatusProvider statusProvider;

        private SocketListener(final int port, final StatusProvider statusProvider)
        {
            this.port = port;
            this.statusProvider = statusProvider;
        }

        @Override
        public void run()
        {
            while(!Thread.currentThread().isInterrupted())
            {
                try
                {
                    final ServerSocket serverSocket = new ServerSocket(port);
                    while(!Thread.currentThread().isInterrupted())
                    {
                        try
                        {
                            final Socket socket = serverSocket.accept();
                            final OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
                            writer.append(statusProvider.getStatus().name());
                            writer.flush();
                            socket.close();
                        }
                        catch(IOException e)
                        {
                            LOGGER.log(Level.WARNING, "Could not accept or write to socket", e);
                        }
                    }

                }
                catch(IOException e)
                {
                    LOGGER.log(Level.WARNING, "Failed to create server socket, waiting for 10 seconds", e);
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10L));
                }
            }
        }
    }
}