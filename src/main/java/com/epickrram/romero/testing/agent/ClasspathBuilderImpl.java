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

import com.epickrram.romero.agent.ClasspathBuilder;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.testing.common.TestPropertyKeys;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class ClasspathBuilderImpl implements ClasspathBuilder<TestSuiteIdentifier, Properties>
{
    @Override
    public List<URL> getAdditionalClasspathElements(final JobDefinition<TestSuiteIdentifier, Properties> jobDefinition)
    {
        final List<URL> urlList = new ArrayList<>();
        final Properties data = jobDefinition.getData();
        final Set<String> propertyKeys = data.stringPropertyNames();
        for (String propertyKey : propertyKeys)
        {
            if(propertyKey.startsWith(TestPropertyKeys.CLASSPATH_URL_PREFIX))
            {
                urlList.add(getUrl(data, propertyKey));
            }
        }

        return urlList;
    }

    private static URL getUrl(final Properties data, final String propertyKey)
    {
        final String urlSpec = data.getProperty(propertyKey);
        try
        {
            return new URL(urlSpec);
        }
        catch (MalformedURLException e)
        {
            throw new IllegalArgumentException("Could not parse URL: " + urlSpec, e);
        }
    }
}
