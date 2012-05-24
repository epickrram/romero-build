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

package com.epickrram.romero.testing.agent.junit;

import com.epickrram.romero.agent.ExecutionContext;
import com.epickrram.romero.agent.JobResultHandler;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.stub.StubJUnitTestData;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public final class JUnitTestExecutorTest
{
    @Mock
    private JobResultHandler<TestSuiteJobResult> resultHandler;
    private JUnitClassExecutor unitTestExecutor;
    private JobDefinition<TestSuiteIdentifier, Properties> jobDefinition;

    @Before
    public void setUp() throws Exception
    {
        final JUnitCore jUnitCore = new JUnitCore();
        unitTestExecutor = new JUnitClassExecutor(jUnitCore, resultHandler);
        jobDefinition = new JobDefinitionImpl<>(toMapKey(StubJUnitTestData.class.getName()), null);
    }

    @Test
    public void shouldNotifyHandlerOfTestCaseJobResult() throws Exception
    {
        unitTestExecutor.execute(jobDefinition, new ExecutionContext());

        verify(resultHandler).onJobResult(any(TestSuiteJobResult.class));
    }
}
