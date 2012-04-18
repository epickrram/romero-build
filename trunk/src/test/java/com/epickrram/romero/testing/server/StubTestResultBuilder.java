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

package com.epickrram.romero.testing.server;

import com.epickrram.romero.testing.common.TestExecutionResult;
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.common.TestSuiteJobResult;

import static java.util.Arrays.asList;

public final class StubTestResultBuilder
{
    private static final String TEST_METHOD = "testMethod";
    private static final TestStatus TEST_STATUS = TestStatus.SUCCESS;
    private static final long DURATION_MILLIS = 1500L;
    private static final String STDOUT = "STDOUT";
    private static final String STDERR = "STDERR";

    public static TestExecutionResult getTestExecutionResult(final String testClass)
    {
        return getTestExecutionResultBuilder(testClass).
                newInstance();
    }

    public static TestExecutionResult.Builder getTestExecutionResultBuilder(final String testClass)
    {
        return new TestExecutionResult.Builder().
                testClass(testClass).
                testMethod(TEST_METHOD).
                testStatus(TEST_STATUS).
                durationMillis(DURATION_MILLIS).
                stdout(STDOUT).
                stderr(STDERR);
    }

    public static TestSuiteJobResult getTestCaseJobResult(final String testClass)
    {
        return new TestSuiteJobResult(testClass, 1L, asList(getTestExecutionResult(testClass)));
    }
}