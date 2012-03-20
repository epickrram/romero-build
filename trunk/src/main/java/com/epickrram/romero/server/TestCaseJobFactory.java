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

package com.epickrram.romero.server;

import com.epickrram.romero.common.TestCaseIdentifier;
import com.epickrram.romero.common.TestCaseJob;
import com.epickrram.romero.common.TestCaseJobResult;
import com.epickrram.romero.core.Job;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobFactory;

import java.util.Properties;

public final class TestCaseJobFactory implements JobFactory<TestCaseIdentifier, Properties, TestCaseJobResult>
{
    @Override
    public Job<TestCaseIdentifier, TestCaseJobResult> createJob(final JobDefinition<TestCaseIdentifier, Properties> jobDefinition)
    {
        return new TestCaseJob(jobDefinition.getKey());
    }
}
