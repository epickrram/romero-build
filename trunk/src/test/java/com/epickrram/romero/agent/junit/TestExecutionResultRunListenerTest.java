package com.epickrram.romero.agent.junit;

import com.epickrram.romero.common.TestCaseJobResult;
import com.epickrram.romero.common.TestExecutionResult;
import com.epickrram.romero.common.TestStatus;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.JUnitCore;

import java.util.Collection;

import static com.epickrram.romero.common.TestStatus.*;
import static java.lang.String.valueOf;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public final class TestExecutionResultRunListenerTest
{
    private static final int EXPECTED_NUMBER_OF_TEST_METHODS = 4;
    private static final Class<StubJUnitTestData> TEST_CLASS = StubJUnitTestData.class;

    @Test
    public void shouldGenerateTestExecutionResultForEachTestMethod() throws Exception
    {
        final TestExecutionResultRunListener listener = new TestExecutionResultRunListener();
        final JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(listener);
        jUnitCore.run(TEST_CLASS);

        final TestCaseJobResult result = listener.getTestCaseJobResult();
        final Collection<TestExecutionResult> testMethodResults = result.getTestExecutionResults();

        assertThat(result.getTestClass(), is(TEST_CLASS.getName()));
        assertThat(result.getDurationMillis(), is(not(0L)));
        assertThat(testMethodResults.size(), is(EXPECTED_NUMBER_OF_TEST_METHODS));

        assertContains(testExecutionResult(TEST_CLASS, "shouldBeIgnored", IGNORED), testMethodResults);
        assertContains(testExecutionResult(TEST_CLASS, "shouldPass", SUCCESS), testMethodResults);
        assertContains(testExecutionResult(TEST_CLASS, "shouldFailAssumption", FAILURE), testMethodResults);
        assertContains(testExecutionResult(TEST_CLASS, "shouldThrowException", ERROR, "ExpectedException"), testMethodResults);
    }

    private static void assertContains(final Matcher<TestExecutionResult> matcher, final Collection<TestExecutionResult> results)
    {
        for (TestExecutionResult result : results)
        {
            if(matcher.matches(result))
            {
                return;
            }
        }
        final StringDescription description = new StringDescription();
        matcher.describeTo(description);
        fail("No result matching: " + description.toString() + " in\n" + results);
    }

    private static Matcher<TestExecutionResult> testExecutionResult(final Class<?> testClass,
                                                                    final String methodName,
                                                                    final TestStatus testStatus,
                                                                    final String traceSubstring)
    {
        return new TypeSafeMatcher<TestExecutionResult>()
        {
            @Override
            public boolean matchesSafely(final TestExecutionResult testExecutionResult)
            {
                return testExecutionResult.getTestClass().equals(testClass.getName()) &&
                       testExecutionResult.getTestMethod().equals(methodName) &&
                       testExecutionResult.getTestStatus().equals(testStatus) &&
                       (traceSubstring == null || valueOf(testExecutionResult.getThrowable()).contains(traceSubstring));
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("class: ").appendText(testClass.getSimpleName()).appendText("\nmethod: ").
                        appendText(methodName).appendText("\nstatus: ").appendText(testStatus.name());
            }
        };
    }

    private static Matcher<TestExecutionResult> testExecutionResult(final Class<?> testClass,
                                                                    final String methodName,
                                                                    final TestStatus testStatus)
    {
        return testExecutionResult(testClass, methodName, testStatus, null);
    }
}