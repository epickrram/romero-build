package com.epickrram.romero.agent;

import com.epickrram.romero.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.epickrram.romero.common.BuildStatus.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class AgentTest
{
    private static final String AGENT_ID = "AGENT-ID";
    private static final String TEST_CLASS = "TEST-CLASS";

    @Mock
    private Server server;
    @Mock
    private Agent.Sleeper sleeper;
    @Mock
    private TestExecutor testExecutor;
    private Agent agent;

    @Test
    public void shouldRetrieveServerStatus() throws Exception
    {
        when(server.getStatus()).thenReturn(WAITING_FOR_NEXT_BUILD);

        agent.run();

        verify(server).getStatus();
        verify(sleeper).sleep(anyLong());
    }

    @Test
    public void shouldRetrieveNextJobWhenServerStatusIsBuilding() throws Exception
    {
        when(server.getStatus()).thenReturn(BUILDING);
        when(server.getNextTestClassToRun(AGENT_ID)).thenReturn(TEST_CLASS);

        agent.run();

        verify(server).getStatus();
        verify(testExecutor).runTest(TEST_CLASS);
        verifyZeroInteractions(sleeper);
    }

    @Test
    public void shouldSleepForDurationWhenServerStatusIsWaitingForTestFinish() throws Exception
    {
        when(server.getStatus()).thenReturn(WAITING_FOR_TESTS_TO_COMPLETE);

        agent.run();

        verify(server).getStatus();
        verify(sleeper).sleep(anyLong());
    }

    @Test
    public void shouldSleepForDurationWhenServerStatusIsBuildingButNextTestIsNull() throws Exception
    {
        when(server.getStatus()).thenReturn(BUILDING);
        when(server.getNextTestClassToRun(AGENT_ID)).thenReturn(null);

        agent.run();

        verify(server).getStatus();
        verifyZeroInteractions(testExecutor);
        verify(sleeper).sleep(anyLong());
    }

    @Before
    public void setup() throws Exception
    {
        agent = new Agent(server, testExecutor, sleeper, AGENT_ID);
    }
}