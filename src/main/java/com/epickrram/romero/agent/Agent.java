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
import com.epickrram.romero.util.LoggingUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.common.BuildStatus.BUILDING;

public final class Agent<K, D, R> implements Runnable
{
    private static final Logger LOGGER = LoggingUtil.getLogger(Agent.class.getSimpleName());
    private static final long WAIT_FOR_BUILDING_INTERVAL_SECONDS = 10L;
    private static final long WAIT_FOR_AVAILABLE_TEST_INTERVAL_SECONDS = 2L;
    private static final String EXECUTION_WRAPPER_CLASS_REGEX = "^.*ExecutionWrapper\\.class$";

    private final Server<K, D, R> server;
    private final ClassExecutor<K, D> classExecutor;
    private final Sleeper sleeper;
    private final String agentId;
    private final List<ExecutionWrapper<K, D>> executionWrappers;
    private final ClasspathBuilder<K, D> classpathBuilder;
    private final ClasspathElementScanner classpathElementScanner;

    public Agent(final Server<K, D, R> server,
                 final ClassExecutor<K, D> classExecutor,
                 final Sleeper sleeper, final String agentId,
                 final List<ExecutionWrapper<K, D>> executionWrappers,
                 final ClasspathBuilder<K, D> classpathBuilder,
                 final ClasspathElementScanner classpathElementScanner)
    {
        this.server = server;
        this.classExecutor = classExecutor;
        this.sleeper = sleeper;
        this.agentId = agentId;
        this.executionWrappers = executionWrappers;
        this.classpathBuilder = classpathBuilder;
        this.classpathElementScanner = classpathElementScanner;
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
        final JobDefinition<K, D> jobDefinition = server.getNextTestToRun(agentId);
        if(jobDefinition != null)
        {
            final Properties currentSystemProperties = (Properties) System.getProperties().clone();
            final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try
            {
                final ExecutionContext executionContext = new ExecutionContext();
                final List<URL> classpathElements = classpathBuilder.getAdditionalClasspathElements(jobDefinition);

                prepareClasspath(currentClassLoader, classpathElements);

                final Set<String> executionWrapperClassNames = classpathElementScanner.findClassNamesMatching(EXECUTION_WRAPPER_CLASS_REGEX, classpathElements);
                final List<ExecutionWrapper<K, D>> additionalExecutionWrappers = new ArrayList<>(executionWrapperClassNames.size());
                for (String className : executionWrapperClassNames)
                {
                    addExecutionWrapper(additionalExecutionWrappers, className);
                }

                additionalExecutionWrappers.addAll(executionWrappers);
                beforeExecute(jobDefinition, executionContext, additionalExecutionWrappers);

                classExecutor.execute(jobDefinition, executionContext);

                afterExecute(jobDefinition, executionContext, additionalExecutionWrappers);
            }
            catch(Throwable e)
            {
                final StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                server.onJobFailure(jobDefinition, writer.toString());
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

    private void addExecutionWrapper(final List<ExecutionWrapper<K, D>> executionWrappers, final String className)
    {
        try
        {
            executionWrappers.add(ClassLoaderUtil.<ExecutionWrapper<K, D>>loadClass(className).newInstance());
        }
        catch (InstantiationException e)
        {
            LOGGER.log(Level.SEVERE, String.format("Unable to load execution wrapper %s", className), e);
        }
        catch (IllegalAccessException e)
        {
            LOGGER.log(Level.SEVERE, String.format("Unable to load execution wrapper %s", className), e);
        }
    }

    private void prepareClasspath(final ClassLoader currentClassLoader, final List<URL> urlList)
    {
        if(!urlList.isEmpty())
        {
            final URLClassLoader additional = new URLClassLoader(urlList.toArray(new URL[urlList.size()]), currentClassLoader);
            Thread.currentThread().setContextClassLoader(additional);
        }
    }

    private void beforeExecute(final JobDefinition<K, D> jobDefinition,
                               final ExecutionContext executionContext,
                               final List<ExecutionWrapper<K, D>> executionWrappers)
    {
        for (ExecutionWrapper<K, D> executionWrapper : executionWrappers)
        {
            executionWrapper.beforeExecution(jobDefinition, executionContext);
        }
    }

    private void afterExecute(final JobDefinition<K, D> jobDefinition,
                              final ExecutionContext executionContext,
                              final List<ExecutionWrapper<K, D>> executionWrappers)
    {
        for (ExecutionWrapper<K, D> executionWrapper : executionWrappers)
        {
            executionWrapper.afterExecution(jobDefinition, executionContext);
        }
    }

    public interface Sleeper
    {
        void sleep(final long seconds);
    }
}