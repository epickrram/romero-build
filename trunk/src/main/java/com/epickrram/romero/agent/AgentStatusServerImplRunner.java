package com.epickrram.romero.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static com.epickrram.romero.util.DaemonThreadFactory.DAEMON_THREAD_FACTORY;

public final class AgentStatusServerImplRunner
{
    public static void main(String[] args) throws Exception
    {
        final ExecutorService executor = Executors.newSingleThreadExecutor(DAEMON_THREAD_FACTORY);
        final AgentStatusServerImpl agentStatusServer = new AgentStatusServerImpl(57000, new StatusProvider()
        {
            private int counter = 0;

            @Override
            public Status getStatus()
            {
                return Status.values()[counter++ % Status.values().length];
            }
        }, executor);

        agentStatusServer.start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));

        executor.shutdown();
        executor.awaitTermination(30L, TimeUnit.SECONDS);
    }
}
