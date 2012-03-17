package com.epickrram.romero.util;

import java.util.concurrent.ThreadFactory;

public final class DaemonThreadFactory implements ThreadFactory
{
    public static final ThreadFactory DAEMON_THREAD_FACTORY = new DaemonThreadFactory();

    @Override
    public Thread newThread(final Runnable runnable)
    {
        final Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    }
}
