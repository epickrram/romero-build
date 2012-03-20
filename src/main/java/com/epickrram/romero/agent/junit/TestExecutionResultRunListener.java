package com.epickrram.romero.agent.junit;

import com.epickrram.romero.common.TestCaseJobResult;
import com.epickrram.romero.common.TestExecutionResult;
import com.epickrram.romero.common.TestStatus;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import static com.epickrram.romero.agent.junit.JUnitClassNameUtil.*;
import static com.epickrram.romero.common.TestStatus.*;

public final class TestExecutionResultRunListener extends RunListener
{
    private TestExecutionResult.Builder testMethodResultBuilder;
    private TestCaseJobResult.Builder testCaseResultBuilder;
    private TestCaseJobResult testCaseJobResult;

    @Override
    public void testRunStarted(final Description description) throws Exception
    {
        testCaseResultBuilder = new TestCaseJobResult.Builder();
        testCaseResultBuilder.testRunStart(System.currentTimeMillis());
    }

    @Override
    public void testRunFinished(final Result result) throws Exception
    {
        testCaseResultBuilder.testRunFinish(System.currentTimeMillis());
        testCaseJobResult = testCaseResultBuilder.newInstance();
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
        final boolean isAssertionError = failure.getException() instanceof AssertionError;
        testMethodResultBuilder.testStatus(isAssertionError ? FAILURE : ERROR);
        testMethodResultBuilder.throwable(failure.getTrace());
    }

    @Override
    public void testAssumptionFailure(final Failure failure)
    {
        testMethodResultBuilder.testStatus(FAILURE);
    }

    @Override
    public void testIgnored(final Description description) throws Exception
    {
        initialiseWithStatus(description, IGNORED);
        onResult();
    }

    public TestCaseJobResult getTestCaseJobResult()
    {
        return testCaseJobResult == null ?
                testCaseResultBuilder.testRunFinish(System.currentTimeMillis()).newInstance() :
                testCaseJobResult;
    }

    private void onResult()
    {
        testCaseResultBuilder.testExecutionResult(testMethodResultBuilder.newInstance());
    }

    private void initialiseWithStatus(final Description description, final TestStatus initialStatus)
    {
        testMethodResultBuilder = new TestExecutionResult.Builder();
        setTestDetails(description);
        testMethodResultBuilder.testStatus(initialStatus);
    }

    private void setTestDetails(final Description description)
    {
        final String[] classNameAndMethod = fromDisplayName(description.getDisplayName());
        final String testClass = classNameAndMethod[TEST_CLASS_NAME];
        testMethodResultBuilder.testClass(testClass);
        testMethodResultBuilder.testMethod(classNameAndMethod[TEST_METHOD_NAME]);
        testCaseResultBuilder.testClass(testClass);
    }
}