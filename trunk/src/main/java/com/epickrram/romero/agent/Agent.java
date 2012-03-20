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

import com.epickrram.romero.server.Server;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.common.BuildStatus.BUILDING;

public final class Agent implements Runnable
{
    private static final Logger LOGGER = Logger.getLogger(Agent.class.getSimpleName());
    private static final long WAIT_FOR_BUILDING_INTERVAL_SECONDS = 10L;
    private static final long WAIT_FOR_AVAILABLE_TEST_INTERVAL_SECONDS = 2L;

    private final Server server;
    private final TestExecutor testExecutor;
    private final Sleeper sleeper;
    private final String agentId;

    public Agent(final Server server, final TestExecutor testExecutor,
                 final Sleeper sleeper, final String agentId)
    {
        this.server = server;
        this.testExecutor = testExecutor;
        this.sleeper = sleeper;
        this.agentId = agentId;
    }

    @Override
    public void run()
    {
        try
        {
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
        final String testClass = server.getNextTestClassToRun(agentId);
        if(testClass != null)
        {
            testExecutor.runTest(testClass);
        }
        else
        {
            sleeper.sleep(WAIT_FOR_AVAILABLE_TEST_INTERVAL_SECONDS);
        }
    }

    public interface Sleeper
    {
        void sleep(final long seconds);
    }
}