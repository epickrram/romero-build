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
import com.epickrram.romero.agent.ExecutionWrapper;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;

import java.util.Properties;

import static com.epickrram.romero.testing.common.TestPropertyKeys.SYSTEM_PROPERTY_PREFIX;

public final class SystemPropertySetterExecutionWrapper implements ExecutionWrapper<TestSuiteIdentifier, Properties>
{
    @Override
    public void beforeExecution(final JobDefinition<TestSuiteIdentifier, Properties> jobDefinition, final ExecutionContext executionContext)
    {
        final Properties data = jobDefinition.getData();
        for (String propertyKey : data.stringPropertyNames())
        {
            if(propertyKey.startsWith(SYSTEM_PROPERTY_PREFIX))
            {
                System.setProperty(propertyKey.substring(SYSTEM_PROPERTY_PREFIX.length()), data.getProperty(propertyKey));
            }
        }
    }

    @Override
    public void afterExecution(final JobDefinition<TestSuiteIdentifier, Properties> jobDefinition, final ExecutionContext executionContext)
    {
    }

    @Override
    public Priority getPriority()
    {
        return Priority.HIGHEST;
    }
}
