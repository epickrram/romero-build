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

package com.epickrram.romero.agent.junit;

import com.epickrram.romero.agent.TestCaseJobResultHandler;
import com.epickrram.romero.common.TestCaseJobResult;
import com.epickrram.romero.stub.StubJUnitTestData;
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
    private TestCaseJobResultHandler resultHandler;
    private JUnitTestExecutor unitTestExecutor;

    @Before
    public void setUp() throws Exception
    {
        final JUnitCore jUnitCore = new JUnitCore();
        unitTestExecutor = new JUnitTestExecutor(jUnitCore, resultHandler);
    }

    @Test
    public void shouldNotifyHandlerOfTestCaseJobResult() throws Exception
    {
        unitTestExecutor.runTest(StubJUnitTestData.class.getName());

        verify(resultHandler).onTestCaseJobResult(any(TestCaseJobResult.class));
    }
}
