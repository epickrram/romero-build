package com.epickrram.romero.common;

import java.util.ArrayList;
import java.util.Collection;

public final class TestCaseJobResult
{
    private final String testClass;
    private final long durationMillis;
    private final Collection<TestExecutionResult> testExecutionResults;

    public TestCaseJobResult(final String testClass, final long durationMillis,
                             final Collection<TestExecutionResult> testExecutionResults)
    {
        this.testClass = testClass;
        this.durationMillis = durationMillis;
        this.testExecutionResults = testExecutionResults;
    }

    public String getTestClass()
    {
        return testClass;
    }

    public long getDurationMillis()
    {
        return durationMillis;
    }

    public Collection<TestExecutionResult> getTestExecutionResults()
    {
        return testExecutionResults;
    }

    public static final class Builder
    {
        private String testClass;
        private long testRunStart;
        private long testRunFinish;
        private Collection<TestExecutionResult> testExecutionResults = new ArrayList<>();

        public Builder testClass(final String testClass)
        {
            this.testClass = testClass;
            return this;
        }

        public Builder testRunStart(final long testRunStart)
        {
            this.testRunStart = testRunStart;
            return this;
        }

        public Builder testRunFinish(final long testRunFinish)
        {
            this.testRunFinish = testRunFinish;
            return this;
        }

        public Builder testExecutionResult(final TestExecutionResult testExecutionResult)
        {
            this.testExecutionResults.add(testExecutionResult);
            return this;
        }

        public TestCaseJobResult newInstance()
        {
            return new TestCaseJobResult(testClass, testRunFinish - testRunStart, testExecutionResults);
        }
    }
}