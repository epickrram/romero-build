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

import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.util.UrlLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class JarUrlTestSuiteJobDefinitionLoaderTest
{
    private static final String URL_PATTERN = "/path/${jobIdentifier}/resource";
    private static final String JOB_IDENTIFIER = "12345";
    private static final String EXPECTED_URL = "/path/12345/resource";

    @Mock
    private UrlLoader urlLoader;
    private JarUrlTestSuiteJobDefinitionLoader jobDefinitionLoader;

    @Before
    public void setup() throws Exception
    {
        final JobIdentifierUrlBuilder urlBuilder = new JobIdentifierUrlBuilder(URL_PATTERN);
        jobDefinitionLoader = new JarUrlTestSuiteJobDefinitionLoader(urlBuilder, urlLoader, testConfigPropertiesResourceUrlBuilder);
    }

    @Test
    public void shouldLoadTestDefinitionsFromJar() throws Exception
    {
        when(urlLoader.downloadUrl(anyString(), anyBoolean())).thenReturn(testDefinitionJar());

        final List<JobDefinition<TestSuiteIdentifier, Properties>> jobDefinitions =
                jobDefinitionLoader.loadJobDefinitions(JOB_IDENTIFIER);

        assertThat(jobDefinitions.size(), is(1));
        final TestSuiteIdentifier key = jobDefinitions.get(0).getKey();
        assertThat(key, is(toMapKey("com.epickrram.romero.StubTestCaseFromExternalJar")));

        verify(urlLoader).downloadUrl(EXPECTED_URL, true);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfTestDefinitionsCannotBeLoaded() throws Exception
    {
        when(urlLoader.downloadUrl(anyString(), anyBoolean())).thenReturn(null);

        jobDefinitionLoader.loadJobDefinitions(JOB_IDENTIFIER);
    }

    private static File testDefinitionJar()
    {
        return new File("src/test/resources/external-test-archive.jar");
    }
}