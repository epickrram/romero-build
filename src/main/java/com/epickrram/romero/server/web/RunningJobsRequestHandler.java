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

import com.epickrram.romero.common.RunningJob;
import com.epickrram.romero.testing.common.TestSuiteIdentifier;
import com.epickrram.romero.server.Server;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.epickrram.romero.testing.common.TestSuiteIdentifier.toMapKey;

final class RunningJobsRequestHandler extends VoidInputRequestHandler<Collection<RunningJob<TestSuiteIdentifier>>>
{
    private final Server server;
    private List<RunningJob<TestSuiteIdentifier>> runningJobs;

    public RunningJobsRequestHandler(final Server server)
    {
        this.server = server;
        runningJobs = Arrays.asList(RunningJob.<TestSuiteIdentifier>create("agent-1", toMapKey("com.bar")),
                RunningJob.<TestSuiteIdentifier>create("agent-2", toMapKey("com.foo")));
    }

    @Override
    Collection<RunningJob<TestSuiteIdentifier>> handleRequest()
    {
        return runningJobs;
//        return server.getRunningJobs();
    }
}
