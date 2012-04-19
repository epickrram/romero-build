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

import com.epickrram.romero.util.LoggingUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public final class StartBuildServlet extends HttpServlet
{
    private static final Logger LOGGER = LoggingUtil.getLogger(StartBuildServlet.class.getSimpleName());
    private static final String JOB_IDENTIFIER_KEY = "jobIdentifier";

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        startBuild(req.getParameter(JOB_IDENTIFIER_KEY));
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        startBuild(req.getParameter(JOB_IDENTIFIER_KEY));
    }

    private void startBuild(final String jobIdentifier) throws ServletException
    {
        if(jobIdentifier != null)
        {
            LOGGER.info("Starting build for jobIdentifier: " + jobIdentifier);
            ServerReference.get().startTestRun(jobIdentifier);
            return;
        }

        throw new ServletException("Unable to initialise build");
    }
}