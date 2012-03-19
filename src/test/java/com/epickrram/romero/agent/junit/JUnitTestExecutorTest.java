package com.epickrram.romero.agent.junit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public final class JUnitTestExecutorTest
{
    @Mock
    private JUnitCore jUnitCore;
    private JUnitTestExecutor unitTestExecutor;

    @Before
    public void setUp() throws Exception
    {
        unitTestExecutor = new JUnitTestExecutor(jUnitCore);
    }

    @Test
    public void shouldAddTestListenerToJUnitAndExecuteTest() throws Exception
    {
        unitTestExecutor.runTest(StubJUnitTestData.class.getName());

        verify(jUnitCore).addListener(any(TestExecutionResultRunListener.class));
        verify(jUnitCore).run(StubJUnitTestData.class);
    }
}
