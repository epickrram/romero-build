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
import com.epickrram.romero.testing.agent.TestCaseJobResultHandler;
import org.junit.runner.JUnitCore;

import static com.epickrram.romero.agent.ClassLoaderUtil.loadClass;

public final class JUnitClassExecutor implements ClassExecutor
{
    private final JUnitCore jUnitCore;
    private final TestCaseJobResultHandler resultHandler;

    public JUnitClassExecutor(final JUnitCore jUnitCore,
                              final TestCaseJobResultHandler resultHandler)
    {
        this.jUnitCore = jUnitCore;
        this.resultHandler = resultHandler;
    }

    @Override
    public void execute(final String className)
    {
        final TestExecutionResultRunListener listener = new TestExecutionResultRunListener();
        jUnitCore.addListener(listener);
        final Class<?> testClass = loadClass(className);
        jUnitCore.run(testClass);

        resultHandler.onTestCaseJobResult(listener.getTestSuiteJobResult());
    }

}
