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

import com.epickrram.romero.core.CompositeJobEventListener;
import com.epickrram.romero.core.JobRepository;
import com.epickrram.romero.core.JobRepositoryImpl;
import com.epickrram.romero.core.LoggingJobEventListener;
import com.epickrram.romero.server.*;
import com.epickrram.romero.server.dao.Bootstrap;
import com.epickrram.romero.server.dao.DriverManagerConnectionManager;
import com.epickrram.romero.server.dao.QueryUtil;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.testing.server.JarUrlTestSuiteJobDefinitionLoader;
import com.epickrram.romero.testing.server.JobIdentifierUrlBuilder;
import com.epickrram.romero.testing.server.TestCaseJobFactory;
import com.epickrram.romero.testing.server.TestSuiteKeyFactory;
import com.epickrram.romero.testing.server.TestingRomeroServerModule;
import com.epickrram.romero.testing.server.UrlBuilder;
import com.epickrram.romero.util.LoggingUtil;
import com.epickrram.romero.util.UrlLoaderImpl;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.testing.server.JarUrlTestSuiteJobDefinitionLoader.URL_PATTERN_PROPERTY;

public final class BootstrapServlet extends GenericServlet
{
    private static final Logger LOGGER = LoggingUtil.getLogger(BootstrapServlet.class.getSimpleName());

    private volatile TestingRomeroServerModule serverModule;

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        final String serverConfigPropertiesUrl = config.getInitParameter("romero.server.config.url");
        LOGGER.fine("Using server config from URL: " + serverConfigPropertiesUrl);
        initialise(serverConfigPropertiesUrl);
    }

    private void initialise(final String serverConfigPropertiesUrl) throws ServletException
    {
        final UrlLoaderImpl urlLoader = new UrlLoaderImpl();
        final PropertiesServerConfig serverConfig = new PropertiesServerConfig(serverConfigPropertiesUrl, urlLoader);
        try
        {
            serverConfig.init();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Loading config failed", e);
            throw new ServletException("Unable to load configured properties", e);
        }
        final int serverAppPort = Integer.parseInt(serverConfig.getStringProperty("server.application.listen.port"));
        final String databaseConnectionUrl = serverConfig.getStringProperty("server.database.url");
        final String databaseDriverClass = serverConfig.getStringProperty("server.database.driver.class.name");

        final DriverManagerConnectionManager connectionManager = new DriverManagerConnectionManager(databaseDriverClass, databaseConnectionUrl);
        final QueryUtil queryUtil = new QueryUtil(connectionManager);
        final CompositeJobRunListener jobRunListener = new CompositeJobRunListener();
        jobRunListener.addDelegate(new StatsRecordingJobRunListener(queryUtil));
        final CompositeJobEventListener<TestSuiteIdentifier, TestSuiteJobResult> jobEventListener =
                new CompositeJobEventListener<>();
        jobEventListener.addDelegate(new LoggingJobEventListener());

        initialiseDatabase(queryUtil);

        final String loaderUrlPattern = serverConfig.getStringProperty(URL_PATTERN_PROPERTY);
        final JobIdentifierUrlBuilder jobResourceUrlBuilder = new JobIdentifierUrlBuilder(loaderUrlPattern);
        final UrlBuilder testConfigPropertiesResourceUrlBuilder = new UrlBuilder(serverConfig.getStringProperty(JarUrlTestSuiteJobDefinitionLoader.TEST_CONFIG_RESOURCE_PATTERN_PROPERTY));
        // TODO this should all come from the module
        final JarUrlTestSuiteJobDefinitionLoader definitionLoader = new JarUrlTestSuiteJobDefinitionLoader(jobResourceUrlBuilder, urlLoader, testConfigPropertiesResourceUrlBuilder);
        final TestCaseJobFactory jobFactory = new TestCaseJobFactory();
        final JobRepository<TestSuiteIdentifier, Properties, TestSuiteJobResult> jobRepository =
                new JobRepositoryImpl<>(definitionLoader, jobFactory, jobEventListener);
        final ServerImpl<TestSuiteIdentifier, Properties, TestSuiteJobResult> server = new ServerImpl<>(jobRepository, new TestSuiteKeyFactory(), jobRunListener);

        serverModule = new TestingRomeroServerModule(serverAppPort, server);
        serverModule.initialise();
        serverModule.initialise(serverConfig);
        jobEventListener.addDelegate(serverModule.<TestSuiteIdentifier, TestSuiteJobResult>getJobEventListener());
        jobRunListener.addDelegate(serverModule.getJobRunListener());

        ServerReference.set(server);
        ServerReference.setQueryUtil(queryUtil);
        ServerReference.setModule(serverModule);
    }

    @Override
    public void destroy()
    {
        serverModule.shutdown();
    }

    @Override
    public void service(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws ServletException, IOException
    {
    }

    private void initialiseDatabase(final QueryUtil queryUtil) throws ServletException
    {
        try
        {
            Bootstrap.setupDatabase(queryUtil, "romero.schema.sql");
        }
        catch (SQLException | IOException e)
        {
            throw new ServletException(e);
        }
    }
}