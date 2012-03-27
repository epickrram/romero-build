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

import com.epickrram.romero.common.TestSuiteIdentifier;
import com.epickrram.romero.common.TestSuiteJobResult;
import com.epickrram.romero.common.proxy.BlockingMethodInvocationReceiver;
import com.epickrram.romero.common.proxy.Deserialiser;
import com.epickrram.romero.common.proxy.Serialiser;
import com.epickrram.romero.core.JobRepository;
import com.epickrram.romero.core.JobRepositoryImpl;
import com.epickrram.romero.core.LoggingJobEventListener;
import com.epickrram.romero.server.*;
import com.epickrram.romero.util.UrlLoaderImpl;

import javax.net.ServerSocketFactory;
import javax.servlet.*;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.server.JarUrlTestCaseJobDefinitionLoader.URL_PATTERN_PROPERTY;

public final class BootstrapServlet extends GenericServlet
{
    private static final Logger LOGGER = Logger.getLogger(BootstrapServlet.class.getSimpleName());

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private volatile BlockingMethodInvocationReceiver<Server> invocationReceiver;

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
        final ServerImpl server = new ServerImpl(jobRepository);
        final int serverAppPort = Integer.parseInt(serverConfig.getStringProperty("server.application.listen.port"));
        invocationReceiver = new BlockingMethodInvocationReceiver<>(serverAppPort, server, Server.class, new Serialiser(), new Deserialiser(), ServerSocketFactory.getDefault(), executor);
        ServerReference.set(server);
        invocationReceiver.start();
    }

    @Override
    public void destroy()
    {
        executor.shutdown();
        invocationReceiver.stop();
    }

    @Override
    public void service(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws ServletException, IOException
    {
    }
}