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

import com.epickrram.romero.agent.ClassExecutor;
import com.epickrram.romero.agent.ExecutionContext;
import com.epickrram.romero.agent.JobResultHandler;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import org.junit.runner.JUnitCore;

import java.util.Properties;

import static com.epickrram.romero.agent.ClassLoaderUtil.loadClass;

public final class JUnitClassExecutor implements ClassExecutor<TestSuiteIdentifier, Properties>
{
    private final JUnitCore jUnitCore;
    private final JobResultHandler<TestSuiteJobResult> resultHandler;

    public JUnitClassExecutor(final JUnitCore jUnitCore,
                              final JobResultHandler<TestSuiteJobResult> resultHandler)
    {
        this.jUnitCore = jUnitCore;
        this.resultHandler = resultHandler;
    }

    @Override
    public void execute(final JobDefinition<TestSuiteIdentifier, Properties> jobDefinition, final ExecutionContext executionContext)
    {
        final String testClassName = jobDefinition.getKey().getTestClass();
        final TestExecutionResultRunListener listener = new TestExecutionResultRunListener();
        jUnitCore.addListener(listener);
        final Class<?> testClass = loadClass(testClassName);
        jUnitCore.run(testClass);

        resultHandler.onJobResult(listener.getTestSuiteJobResult());
    }
}