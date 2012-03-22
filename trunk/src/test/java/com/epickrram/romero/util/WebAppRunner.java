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

package com.epickrram.romero.util;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public final class WebAppRunner
{
    public static void main(String[] args) throws Exception
    {
        final Server server = new Server(8080);
        WebAppContext context = new WebAppContext();

        final File webAppRootDir = new File("src/main/web");
        final String webXml = new File(webAppRootDir, "WEB-INF/web.xml").getAbsolutePath();
        context.setDescriptor(webXml);
        context.setResourceBase(webAppRootDir.getAbsolutePath());
        context.setParentLoaderPriority(true);
        context.setContextPath("/");
        
        server.setHandler(context);

        server.start();
        server.join();
    }
}
