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

import com.epickrram.freewheel.messaging.MessagingContext;
import com.epickrram.freewheel.protocol.CodeBookRegistry;
import com.epickrram.romero.common.proxy.PropertiesTranslator;
import com.epickrram.romero.core.JobEventListener;
import com.epickrram.romero.server.AbstractRomeroServerModule;
import com.epickrram.romero.server.JobRunListener;
import com.epickrram.romero.server.Server;
import com.epickrram.romero.server.ServerConfig;
import com.epickrram.romero.server.dao.Bootstrap;
import com.epickrram.romero.server.dao.DriverManagerConnectionManager;
import com.epickrram.romero.server.dao.QueryUtil;
import com.epickrram.romero.server.web.RequestHandler;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.epickrram.romero.testing.server.dao.TestSuiteJobDaoImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public final class TestingRomeroServerModule extends AbstractRomeroServerModule
{
    private final Server server;
    private final Collection<RequestHandler<?, ?>> requestHandlers = new ArrayList<>();
    private volatile TestSuiteJobEventListener eventListener;


    public TestingRomeroServerModule(final int serverAppPort, final Server server)
    {
        super(serverAppPort);
        this.server = server;
    }

    @Override
    protected void registerTranslatables(final CodeBookRegistry codeBookRegistry)
    {
        codeBookRegistry.registerTranslator(6000, new PropertiesTranslator(), Properties.class);
    }

    @Override
    protected void beforeMessagingStart(final MessagingContext messagingContext)
    {
        messagingContext.createSubscriber(Server.class, server);
    }

    @Override
    public void initialise(final ServerConfig serverConfig)
    {
        final String moduleDbDriverClass = serverConfig.getStringProperty("romero.module.testing.database.driver.class.name");
        final String moduleDbUrl = serverConfig.getStringProperty("romero.module.testing.database.url");
        final String schemaResource = serverConfig.getStringProperty("romero.module.testing.database.schema.resource");
        final QueryUtil queryUtil = new QueryUtil(new DriverManagerConnectionManager(moduleDbDriverClass, moduleDbUrl));

        try
        {
            Bootstrap.setupDatabase(queryUtil, schemaResource);
        }
        catch(SQLException | IOException e)
        {
            throw new IllegalStateException("Failed to bootstrap database", e);
        }

        final TestSuiteJobDaoImpl testSuiteJobDao = new TestSuiteJobDaoImpl(queryUtil);
        eventListener = new TestSuiteJobEventListener(testSuiteJobDao);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public JobEventListener<TestSuiteIdentifier, TestSuiteJobResult> getJobEventListener()
    {
        return eventListener;
    }

    @Override
    public JobRunListener getJobRunListener()
    {
        return eventListener;
    }

    @Override
    public Collection<RequestHandler<?, ?>> getRequestHandlers()
    {
        return requestHandlers;
    }
}
