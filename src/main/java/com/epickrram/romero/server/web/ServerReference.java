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

import com.epickrram.romero.server.Server;
import com.epickrram.romero.server.dao.QueryUtil;

import java.util.concurrent.atomic.AtomicReference;

public final class ServerReference
{
    private static final AtomicReference<Server> serverReference = new AtomicReference<>();
    private static final AtomicReference<QueryUtil> queryUtil = new AtomicReference<>();

    private ServerReference() {}

    static void set(final Server server)
    {
        serverReference.set(server);
    }

    public static Server get()
    {
        return serverReference.get();
    }

    static void setQueryUtil(final QueryUtil queryUtilInstance)
    {
        queryUtil.set(queryUtilInstance);
    }

    public static QueryUtil getQueryUtil()
    {
        return queryUtil.get();
    }
}
