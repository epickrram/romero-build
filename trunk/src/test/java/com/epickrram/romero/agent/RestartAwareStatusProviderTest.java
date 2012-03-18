package com.epickrram.romero.agent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class RestartAwareStatusProviderTest
{
    @Mock
    private StatusProvider delegate;
    @Mock
    private AgentRestartMonitor agentRestartMonitor;

    private RestartAwareStatusProvider statusProvider;

    @Test
    public void shouldReturnDelegateStatusCodeWhenRestartIsNotRequired() throws Exception
    {
        when(delegate.getStatus()).thenReturn(Status.TESTING);

        assertThat(statusProvider.getStatus(), is(Status.TESTING));

        verify(delegate).getStatus();
    }

    @Test
    public void shouldIndicateRestartRequiredIfDelegateReturnsIdleStatus() throws Exception
    {
        when(delegate.getStatus()).thenReturn(Status.IDLE);
        when(agentRestartMonitor.isRestartRequired(anyLong())).thenReturn(true);

        assertThat(statusProvider.getStatus(), is(Status.WAITING_FOR_RESTART));

        verify(delegate).getStatus();
        verify(agentRestartMonitor).isRestartRequired(anyLong());
    }

    @Before
    public void setup() throws Exception
    {
        statusProvider = new RestartAwareStatusProvider(delegate, agentRestartMonitor);
    }
}
