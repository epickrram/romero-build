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

import com.epickrram.freewheel.messaging.MessagingContext;
import com.epickrram.freewheel.messaging.MessagingContextFactory;
import com.epickrram.freewheel.messaging.ptp.EndPoint;
import com.epickrram.freewheel.messaging.ptp.EndPointProvider;
import com.epickrram.freewheel.protocol.CodeBookRegistry;
import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.common.proxy.PropertiesTranslator;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.epickrram.romero.core.JobRepository;
import com.epickrram.romero.core.JobRepositoryImpl;
import com.epickrram.romero.core.LoggingJobEventListener;
import com.epickrram.romero.server.PropertiesServerConfig;
import com.epickrram.romero.server.Server;
import com.epickrram.romero.server.ServerImpl;
import com.epickrram.romero.testing.common.TestExecutionResult;
import com.epickrram.romero.testing.common.TestStatus;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.testing.server.JarUrlTestCaseJobDefinitionLoader;
import com.epickrram.romero.testing.server.JobIdentifierUrlBuilder;
import com.epickrram.romero.testing.server.TestCaseJobFactory;
import com.epickrram.romero.testing.server.TestSuiteKeyFactory;
import com.epickrram.romero.util.LoggingUtil;
import com.epickrram.romero.util.UrlLoaderImpl;

import javax.servlet.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.testing.server.JarUrlTestCaseJobDefinitionLoader.URL_PATTERN_PROPERTY;

public final class BootstrapServlet extends GenericServlet
{
    private static final Logger LOGGER = LoggingUtil.getLogger(BootstrapServlet.class.getSimpleName());

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
        final JobIdentifierUrlBuilder urlBuilder = new JobIdentifierUrlBuilder(loaderUrlPattern);
        final JarUrlTestCaseJobDefinitionLoader definitionLoader = new JarUrlTestCaseJobDefinitionLoader(urlBuilder, urlLoader);
        final TestCaseJobFactory jobFactory = new TestCaseJobFactory();
        final JobRepository<TestSuiteIdentifier, Properties, TestSuiteJobResult> jobRepository =
                new JobRepositoryImpl<>(definitionLoader, jobFactory, new LoggingJobEventListener());
        final ServerImpl<TestSuiteIdentifier, Properties, TestSuiteJobResult> server = new ServerImpl<>(jobRepository, new TestSuiteKeyFactory());
        final int serverAppPort = Integer.parseInt(serverConfig.getStringProperty("server.application.listen.port"));

        startServerListener(serverAppPort, server);

        ServerReference.set(server);
    }

    private void startServerListener(final int serverAppPort, final ServerImpl<TestSuiteIdentifier, Properties, TestSuiteJobResult> server)
    {
        final MessagingContextFactory contextFactory = new MessagingContextFactory();
        final CodeBookRegistry codeBookRegistry = contextFactory.getCodeBookRegistry();
        codeBookRegistry.registerTranslatable(TestExecutionResult.class);
        codeBookRegistry.registerTranslatable(TestStatus.class);
        codeBookRegistry.registerTranslatable(TestSuiteIdentifier.class);
        codeBookRegistry.registerTranslatable(TestSuiteJobResult.class);
        codeBookRegistry.registerTranslatable(BuildStatus.class);
        codeBookRegistry.registerTranslatable(JobDefinitionImpl.class);
        codeBookRegistry.registerTranslator(6000, new PropertiesTranslator(), Properties.class);
        final MessagingContext messagingContext =
                contextFactory.createDirectBlockingPointToPointMessagingContext(new EndPointProvider()
                {
                    @Override
                    public EndPoint resolveEndPoint(final Class descriptor)
                    {
                        return new EndPoint(InetAddress.getLoopbackAddress(), serverAppPort);
                    }
                });
        messagingContext.createSubscriber(Server.class, server);
        messagingContext.start();
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