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

package com.epickrram.romero.acceptance.test;

import com.epickrram.romero.acceptance.framework.Romero;
import com.epickrram.romero.testing.common.TestStatus;
import org.junit.Before;
import org.junit.Test;

public final class ExecuteUnitTestRunAcceptanceTest
{
    private final Romero romero = new Romero("localhost", 8080);

    @Before
    public void beforeTest()
    {
        romero.waitForTestRunFinished();
    }

    @Test
    public void shouldExecuteTestRun() throws Exception
    {
        romero.startTestRun("124");
        romero.waitForTestRunStarted("124");
        romero.waitForTestRunFinished();

        romero.waitForTestRunHistoryLatest("124");

        romero.waitForTestCaseResultSummary("124", 7, 4, 1, 1, 1);

        romero.waitForTestCaseResult("MultipleTestMethodTestSuite", "shouldPassInFiveSeconds", TestStatus.SUCCESS);
        romero.waitForTestCaseResult("MultipleTestMethodTestSuite", "shouldFailAssertion", TestStatus.FAILURE);
        romero.waitForTestCaseResult("MultipleTestMethodTestSuite", "shouldThrowException", TestStatus.ERROR);
        romero.waitForTestCaseResult("MultipleTestMethodTestSuite", "shouldBeIgnored", TestStatus.IGNORED);
        romero.waitForTestCaseResult("SingleTestMethodTestSuite", "shouldPassInFileSeconds", TestStatus.SUCCESS);
        romero.waitForTestCaseResult("VersionedTestSuite", "shouldBeVersioned", TestStatus.SUCCESS);
    }
}