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

package com.epickrram.romero.server;

import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.common.TestCaseIdentifier;
import com.epickrram.romero.common.TestCaseJobResult;
import com.epickrram.romero.core.JobDefinition;

import java.util.Properties;

public interface Server
{
    void startTestRun(final String identifier);
    BuildStatus getStatus();
    String getCurrentBuildId();
    JobDefinition<TestCaseIdentifier, Properties> getNextTestToRun(final String agentId);

    void onTestCaseJobResult(final TestCaseJobResult testCaseJobResult);

    int getTotalJobs();
    int getRemainingJobs();
}