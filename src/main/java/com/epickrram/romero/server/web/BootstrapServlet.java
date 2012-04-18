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

package com.epickrram.romero.server.web;

import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.core.JobRepository;
import com.epickrram.romero.core.JobRepositoryImpl;
import com.epickrram.romero.core.LoggingJobEventListener;
import com.epickrram.romero.testing.server.JarUrlTestCaseJobDefinitionLoader;
import com.epickrram.romero.server.PropertiesServerConfig;
import com.epickrram.romero.server.ServerImpl;
import com.epickrram.romero.testing.server.TestCaseJobFactory;
import com.epickrram.romero.testing.server.TestSuiteKeyFactory;
import com.epickrram.romero.util.UrlLoaderImpl;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.testing.server.JarUrlTestCaseJobDefinitionLoader.URL_PATTERN_PROPERTY;

public final class BootstrapServlet extends GenericServlet
{
    private static final Logger LOGGER = Logger.getLogger(BootstrapServlet.class.getSimpleName());

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        initialise();
    }

    private void initialise() throws ServletException
    {
        final UrlLoaderImpl urlLoader = new UrlLoaderImpl();
        final PropertiesServerConfig serverConfig =
                new PropertiesServerConfig("http://localhost:8090/server.properties", urlLoader);
        try
        {
            serverConfig.init();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Loading config failed", e);
            throw new ServletException("Unable to load configured properties", e);
        }
        final String loaderUrlPattern = serverConfig.getStringProperty(URL_PATTERN_PROPERTY);
        final JarUrlTestCaseJobDefinitionLoader definitionLoader = new JarUrlTestCaseJobDefinitionLoader(loaderUrlPattern, urlLoader);
        final TestCaseJobFactory jobFactory = new TestCaseJobFactory();
        final JobRepository<TestSuiteIdentifier, Properties, TestSuiteJobResult> jobRepository =
                new JobRepositoryImpl<>(definitionLoader, jobFactory, new LoggingJobEventListener());
        final ServerImpl<TestSuiteIdentifier, Properties, TestSuiteJobResult> server = new ServerImpl<>(jobRepository, new TestSuiteKeyFactory());
        final int serverAppPort = Integer.parseInt(serverConfig.getStringProperty("server.application.listen.port"));
        ServerReference.set(server);
    }

    @Override
    public void destroy()
    {
        executor.shutdown();
    }

    @Override
    public void service(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws ServletException, IOException
    {
    }
}