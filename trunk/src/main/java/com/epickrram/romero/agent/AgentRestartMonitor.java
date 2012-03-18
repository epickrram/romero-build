package com.epickrram.romero.agent;

public interface AgentRestartMonitor
{
    void onAgentStart(final long timestamp);
    boolean isRestartRequired(final long timestamp);
}
