package com.epickrram.romero.agent.junit;

import com.epickrram.romero.common.TestExecutionResult;
import com.epickrram.romero.common.TestStatus;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.Collection;

import static com.epickrram.romero.agent.junit.JUnitClassNameUtil.*;
import static com.epickrram.romero.common.TestStatus.*;

public final class TestExecutionResultRunListener extends RunListener
{
    private final Collection<TestExecutionResult> results = new ArrayList<>();
    private TestExecutionResult.Builder resultBuilder;

    @Override
    public void testRunStarted(final Description description) throws Exception
    {
    }

    @Override
    public void testRunFinished(final Result result) throws Exception
    {
    }

    @Override
    public void testStarted(final Description description) throws Exception
    {
        initialiseWithStatus(description, SUCCESS);
    }

    @Override
    public void testFinished(final Description description) throws Exception
    {
        onResult();
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Override
    public void testFailure(final Failure failure) throws Exception
    {
        resultBuilder.testStatus(failure.getException() instanceof AssertionError ? FAILURE : ERROR);
    }

    @Override
    public void testAssumptionFailure(final Failure failure)
    {
        resultBuilder.testStatus(FAILURE);
    }

    @Override
    public void testIgnored(final Description description) throws Exception
    {
        initialiseWithStatus(description, IGNORED);
        onResult();
    }

    public Collection<TestExecutionResult> getResults()
    {
        return results;
    }

    private void onResult()
    {
        results.add(resultBuilder.newInstance());
    }

    private void initialiseWithStatus(final Description description, final TestStatus initialStatus)
    {
        resultBuilder = new TestExecutionResult.Builder();
        setTestDetails(description);
        resultBuilder.testStatus(initialStatus);
    }

    private void setTestDetails(final Description description)
    {
        final String[] classNameAndMethod = fromDisplayName(description.getDisplayName());
        resultBuilder.testClass(classNameAndMethod[TEST_CLASS_NAME]);
        resultBuilder.testMethod(classNameAndMethod[TEST_METHOD_NAME]);
    }
}