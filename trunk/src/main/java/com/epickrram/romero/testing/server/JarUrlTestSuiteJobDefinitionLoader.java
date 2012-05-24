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

package com.epickrram.romero.testing.server;

import com.epickrram.romero.testing.common.TestPropertyKeys;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.core.JobDefinitionLoader;
import com.epickrram.romero.util.UrlLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;

public final class JarUrlTestSuiteJobDefinitionLoader implements JobDefinitionLoader<TestSuiteIdentifier, Properties>
{
    public static final String URL_PATTERN_PROPERTY = "server.loader.definition.jar.url.pattern";

    private final UrlLoader urlLoader;
    private final JobIdentifierUrlBuilder urlBuilder;

    public JarUrlTestSuiteJobDefinitionLoader(final JobIdentifierUrlBuilder urlBuilder, final UrlLoader urlLoader)
    {
        this.urlBuilder = urlBuilder;
        this.urlLoader = urlLoader;
    }

    @Override
    public List<JobDefinition<TestSuiteIdentifier, Properties>> loadJobDefinitions(final String identifier)
    {
        final List<JobDefinition<TestSuiteIdentifier, Properties>> definitionList = new ArrayList<>();
        final String url = urlBuilder.getUrl(identifier);
        try
        {
            final File download = urlLoader.downloadUrl(url, true);
            if(download == null)
            {
                throw new RuntimeException("Unable to open url: " + url + ", not found");
            }
            final JarFile jarFile = new JarFile(download);
            final Enumeration<JarEntry> jarEntries = jarFile.entries();
            while(jarEntries.hasMoreElements())
            {
                final JarEntry jarEntry = jarEntries.nextElement();
                final String name = jarEntry.getName();
                if(name.contains(".class"))
                {
                    final Properties properties = new Properties();
                    properties.setProperty(TestPropertyKeys.CLASSPATH_URL_PREFIX + ".tests", url);
                    final JobDefinition<TestSuiteIdentifier, Properties> definition =
                            new JobDefinitionImpl<>(toMapKey(toClassName(name)), properties);
                    definitionList.add(definition);
                }
            }

            jarFile.close();
            download.delete();
            return definitionList;
        }
        catch(IOException e)
        {
            throw new RuntimeException("Unable to open url: " + url, e);
        }
    }

    private static String toClassName(final String fileName)
    {
        return fileName.substring(0, fileName.lastIndexOf('.')).replace('/', '.');
    }
}