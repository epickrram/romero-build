package com.epickrram.romero.agent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public final class SleeperImpl implements Agent.Sleeper
{
    @Override
    public void sleep(final long seconds)
    {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }
}
