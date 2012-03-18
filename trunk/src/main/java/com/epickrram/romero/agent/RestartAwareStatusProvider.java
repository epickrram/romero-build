package com.epickrram.romero.agent;

public final class RestartAwareStatusProvider implements StatusProvider
{
    private final StatusProvider delegate;
    private final AgentRestartMonitor monitor;

    public RestartAwareStatusProvider(final StatusProvider delegate,
                                      final AgentRestartMonitor monitor)
    {
        this.delegate = delegate;
        this.monitor = monitor;
    }

    @Override
    public Status getStatus()
    {
        final Status status = delegate.getStatus();
        if(status == Status.IDLE && monitor.isRestartRequired(System.currentTimeMillis()))
        {
            return Status.WAITING_FOR_RESTART;
        }
        
        return status;
    }
}
