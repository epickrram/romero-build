package com.epickrram.romero.agent.junit;

import com.epickrram.romero.agent.TestExecutor;
import org.junit.runner.JUnitCore;

public final class JUnitTestExecutor implements TestExecutor
{
    private final JUnitCore jUnitCore;

    public JUnitTestExecutor(final JUnitCore jUnitCore)
    {
        this.jUnitCore = jUnitCore;
    }

    @Override
    public void runTest(final String className)
    {
        final TestExecutionResultRunListener listener = new TestExecutionResultRunListener();
        jUnitCore.addListener(listener);
        jUnitCore.run(loadClass(className));
    }

    private Class<?> loadClass(final String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException();
        }
    }
}
