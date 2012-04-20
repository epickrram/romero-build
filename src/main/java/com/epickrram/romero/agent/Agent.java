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

package com.epickrram.romero.agent;

import com.epickrram.romero.core.JobDefinition;
import com.epickrram.romero.server.Server;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.util.LoggingUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.common.BuildStatus.BUILDING;
import static com.epickrram.romero.testing.common.TestPropertyKeys.*;

public final class Agent implements Runnable
{
    private static final Logger LOGGER = LoggingUtil.getLogger(Agent.class.getSimpleName());
    private static final long WAIT_FOR_BUILDING_INTERVAL_SECONDS = 10L;
    private static final long WAIT_FOR_AVAILABLE_TEST_INTERVAL_SECONDS = 2L;

    private final Server<TestSuiteIdentifier, Properties, TestSuiteJobResult> server;
    private final ClassExecutor classExecutor;
    private final Sleeper sleeper;
    private final String agentId;

    public Agent(final Server<TestSuiteIdentifier, Properties, TestSuiteJobResult> server,
                 final ClassExecutor classExecutor,
                 final Sleeper sleeper, final String agentId)
    {
        this.server = server;
        this.classExecutor = classExecutor;
        this.sleeper = sleeper;
        this.agentId = agentId;
    }

    @Override
    public void run()
    {
        try
        {
            LOGGER.fine("Retrieving server status");
            if(server.getStatus() == BUILDING)
            {
                handleBuildingStatus();
            }
            else
            {
                sleeper.sleep(WAIT_FOR_BUILDING_INTERVAL_SECONDS);
            }
        }
        catch(RuntimeException e)
        {
            LOGGER.log(Level.WARNING, "Failed to retrieve server status", e);
        }
    }

    private void handleBuildingStatus()
    {
        LOGGER.fine("Retrieving next job from server");
        final JobDefinition<TestSuiteIdentifier,Properties> testDefinition = server.getNextTestToRun(agentId);
        if(testDefinition != null)
        {
            final Properties currentSystemProperties = (Properties) System.getProperties().clone();
            final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                final List<String>testCaseWrappersClassNames = new ArrayList<>();
                handleTestProperties(testDefinition.getData(), currentClassLoader, testCaseWrappersClassNames);
                final List<ExecutionWrapper> executionWrappers = new ArrayList<>(testCaseWrappersClassNames.size());
                instantiateExecutionWrappers(testCaseWrappersClassNames, executionWrappers);

                beforeExecute(executionWrappers);

                classExecutor.execute(testDefinition.getKey().getTestClass());

                afterExecute(executionWrappers);
            }
            catch(Throwable e)
            {
                final StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                server.onJobFailure(testDefinition, writer.toString());
            }
            finally
            {
                System.setProperties(currentSystemProperties);
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        }
        else
        {
            sleeper.sleep(WAIT_FOR_AVAILABLE_TEST_INTERVAL_SECONDS);
        }
    }

    private void afterExecute(final List<ExecutionWrapper> executionWrappers)
    {
        for (ExecutionWrapper executionWrapper : executionWrappers)
        {
            executionWrapper.afterExecution(null);
        }
    }

    private void beforeExecute(final List<ExecutionWrapper> executionWrappers)
    {
        for (ExecutionWrapper executionWrapper : executionWrappers)
        {
            executionWrapper.beforeExecution(null);
        }
    }

    private void handleTestProperties(final Properties data, final ClassLoader currentClassLoader,
                                      final List<String> testCaseWrappers)
    {
        final List<URL> urlList = new ArrayList<>();
        for (String propertyKey : data.stringPropertyNames())
        {
            if(propertyKey.startsWith(CLASSPATH_URL_PREFIX))
            {
                urlList.add(getUrl(data, propertyKey));
            }
            else if(propertyKey.startsWith(SYSTEM_PROPERTY_PREFIX))
            {
                System.setProperty(propertyKey.substring(SYSTEM_PROPERTY_PREFIX.length()), data.getProperty(propertyKey));
            }
            else if(propertyKey.startsWith(TEST_CASE_WRAPPER_PREFIX))
            {
                testCaseWrappers.add(data.getProperty(propertyKey));
            }
        }

        if(!urlList.isEmpty())
        {
            final URLClassLoader additional = new URLClassLoader(urlList.toArray(new URL[urlList.size()]), currentClassLoader);
            Thread.currentThread().setContextClassLoader(additional);
        }
    }

    private void instantiateExecutionWrappers(final List<String> testCaseWrappersClassNames, final List<ExecutionWrapper> executionWrappers)
    {
        for (String className : testCaseWrappersClassNames)
        {
            addTestCaseWrapper(executionWrappers, className);
        }
    }

    private void addTestCaseWrapper(final List<ExecutionWrapper> executionWrappers, final String className)
    {
        try
        {
            executionWrappers.add(ClassLoaderUtil.<ExecutionWrapper>loadClass(className).newInstance());
        }
        catch (InstantiationException e)
        {
            throw new IllegalArgumentException("Cannot instantiate ExecutionWrapper for " + className +
                    " does it have a no-arg constructor?");
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException("Cannot instantiate ExecutionWrapper for " + className);
        }
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

    public interface Sleeper
    {
        void sleep(final long seconds);
    }
}