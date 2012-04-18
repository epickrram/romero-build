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

package com.epickrram.romero.agent;

import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.server.Server;
import com.epickrram.romero.testing.agent.TestCaseJobResultHandlerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.epickrram.romero.server.StubTestResultBuilder.getTestCaseJobResult;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public final class TestCaseJobResultHandlerImplTest
{
    @Mock
    private Server server;
    private TestCaseJobResultHandlerImpl resultHandler;
    private TestSuiteJobResult result;

    @Test
    public void shouldNotifyServerOfTestCaseJobResult() throws Exception
    {
        resultHandler.onTestCaseJobResult(result);

        verify(server).onTestCaseJobResult(result);
    }

    @Before
    public void setup() throws Exception
    {
        resultHandler = new TestCaseJobResultHandlerImpl(server);
        result = getTestCaseJobResult("test.class");
    }
}
