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

import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.server.Server;

import java.util.HashMap;
import java.util.Map;

final class BuildStatusRequestHandler extends VoidInputRequestHandler<Map<String, String>>
{
    private final Server server;

    public BuildStatusRequestHandler(final Server server)
    {
        this.server = server;
    }

    @Override
    Map<String, String> handleRequest()
    {
        final BuildStatus status = server.getStatus();
        final Map<String, String> map = new HashMap<>();
        map.put("status", status.name());
        map.put("totalJobs", Integer.toString(server.getTotalJobs()));
        map.put("remainingJobs", Integer.toString(server.getRemainingJobs()));
        return map;
    }
}
