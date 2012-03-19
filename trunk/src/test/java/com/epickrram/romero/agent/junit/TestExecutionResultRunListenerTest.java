package com.epickrram.romero.agent.junit;

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
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class TestExecutionResultRunListenerTest
{
    private static final int EXPECTED_NUMBER_OF_TEST_METHODS = 4;

    @Test
    public void shouldGenerateTestExecutionResultForEachTestMethod() throws Exception
    {
        final TestExecutionResultRunListener listener = new TestExecutionResultRunListener();
        final JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(listener);
        jUnitCore.run(StubJUnitTestData.class);

        final Collection<TestExecutionResult> results = listener.getResults();

        assertThat(results.size(), is(EXPECTED_NUMBER_OF_TEST_METHODS));

        assertContains(testExecutionResult(StubJUnitTestData.class, "shouldBeIgnored", IGNORED), results);
        assertContains(testExecutionResult(StubJUnitTestData.class, "shouldPass", SUCCESS), results);
        assertContains(testExecutionResult(StubJUnitTestData.class, "shouldFailAssumption", FAILURE), results);
        assertContains(testExecutionResult(StubJUnitTestData.class, "shouldThrowException", ERROR), results);
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
                                                                            final TestStatus testStatus)
    {
        return new TypeSafeMatcher<TestExecutionResult>()
        {
            @Override
            public boolean matchesSafely(final TestExecutionResult testExecutionResult)
            {
                return testExecutionResult.getTestClass().equals(testClass.getName()) &&
                       testExecutionResult.getTestMethod().equals(methodName) &&
                       testExecutionResult.getTestStatus().equals(testStatus);
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("class: ").appendText(testClass.getSimpleName()).appendText("\nmethod: ").
                        appendText(methodName).appendText("\nstatus: ").appendText(testStatus.name());
            }
        };
    }
}