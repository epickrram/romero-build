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

package com.epickrram.romero.testing.agent;

import com.epickrram.romero.agent.ExecutionContext;

public final class CompositeTestCaseWrapper implements TestCaseWrapper
{
    private final TestCaseWrapper[] delegates;

    public CompositeTestCaseWrapper(final TestCaseWrapper... delegates)
    {
        this.delegates = delegates;
    }

    @Override
    public void beforeTestCase(final ExecutionContext executionContext)
    {
        for (TestCaseWrapper delegate : delegates)
        {
            delegate.beforeTestCase(executionContext);
        }
    }

    @Override
    public void afterTestCase(final ExecutionContext executionContext)
    {
        for (TestCaseWrapper delegate : delegates)
        {
            delegate.afterTestCase(executionContext);
        }
    }
}
