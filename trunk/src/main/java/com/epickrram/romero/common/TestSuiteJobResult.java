//////////////////////////////////////////////////////////////////////////////////
//   Copyright 2011   Mark Price     mark at epickrram.com                      //
//                                                                              //
//   Licensed under the Apache License, Version 2.0 (the "License");            //
//   you may not use this file except in compliance with the License.           //
//   You may obtain a copy of the License at                                    //
//                                                                              //
//       http://www.apache.org/licenses/LICENSE-2.0                             //
//                                                                              //
//   Unless required by applicable law or agreed to in writing, software        //
//   distributed under the License is distributed on an "AS IS" BASIS,          //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   //
//   See the License for the specific language governing permissions and        //
//   limitations under the License.                                             //
//////////////////////////////////////////////////////////////////////////////////

package com.epickrram.romero.common;

import java.util.ArrayList;
import java.util.Collection;

public final class TestSuiteJobResult
{
    private final String testClass;
    private final long durationMillis;
    private final Collection<TestExecutionResult> testExecutionResults;

    public TestSuiteJobResult(final String testClass, final long durationMillis,
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

        public TestSuiteJobResult newInstance()
        {
            return new TestSuiteJobResult(testClass, testRunFinish - testRunStart, testExecutionResults);
        }
    }
}