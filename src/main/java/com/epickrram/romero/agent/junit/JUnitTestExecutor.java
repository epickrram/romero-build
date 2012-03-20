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
import com.epickrram.romero.agent.TestExecutor;
import org.junit.runner.JUnitCore;

public final class JUnitTestExecutor implements TestExecutor
{
    private final JUnitCore jUnitCore;
    private final TestCaseJobResultHandler resultHandler;

    public JUnitTestExecutor(final JUnitCore jUnitCore,
                             final TestCaseJobResultHandler resultHandler)
    {
        this.jUnitCore = jUnitCore;
        this.resultHandler = resultHandler;
    }

    @Override
    public void runTest(final String className)
    {
        final TestExecutionResultRunListener listener = new TestExecutionResultRunListener();
        jUnitCore.addListener(listener);
        final Class<?> testClass = loadClass(className);
        jUnitCore.run(testClass);

        resultHandler.onTestCaseJobResult(listener.getTestCaseJobResult());
    }

    private Class<?> loadClass(final String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("Unable to load class " + className);
        }
    }
}
