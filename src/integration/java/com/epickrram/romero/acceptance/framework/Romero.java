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

package com.epickrram.romero.acceptance.framework;

import static com.epickrram.romero.acceptance.framework.Conditions.postRequestJsonResponseContainsCondition;
import static com.epickrram.romero.acceptance.framework.HttpUtil.getIgnoringResponse;
import static com.epickrram.romero.acceptance.framework.Waiter.waitFor;

public final class Romero
{
    private final String host;
    private final int port;

    public Romero(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }

    public void startTestRun(final String testRunIdentifier)
    {
        getIgnoringResponse(toRomeroUrl("/start?jobIdentifier=" + testRunIdentifier));
    }

    public void waitForTestRunStarted(final String testRunIdentifier)
    {
        waitFor(postRequestJsonResponseContainsCondition(toRomeroUrl("/build/status.json"),
                "jobRunIdentifier", testRunIdentifier));
        waitFor(postRequestJsonResponseContainsCondition(toRomeroUrl("/build/status.json"),
                "status", "BUILDING"));
    }

    public void waitForTestRunFinished()
    {
        waitFor(postRequestJsonResponseContainsCondition(toRomeroUrl("/build/status.json"),
                "status", "WAITING_FOR_NEXT_BUILD"));
    }

    private String toRomeroUrl(final String uri)
    {
        return new StringBuilder("http://").append(host).append(":").append(port).append(uri).toString();
    }
}
