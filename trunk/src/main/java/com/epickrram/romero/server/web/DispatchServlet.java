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
import com.epickrram.romero.server.dao.JobRunDao;
import com.epickrram.romero.server.dao.JobRunDaoImpl;
import com.epickrram.romero.util.LoggingUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DispatchServlet extends HttpServlet
{
    private static final Logger LOGGER = LoggingUtil.getLogger(DispatchServlet.class.getSimpleName());
    private final Map<String, RequestHandler<?, ?>> requestHandlerMap = new ConcurrentHashMap<>();

    @Override
    public void init(final ServletConfig config) throws ServletException
    {
        super.init(config);
        final Server server = ServerReference.get();
        registerRequestHandler(new BuildStatusRequestHandler(server));
        registerRequestHandler(new RunningJobsRequestHandler(server));
        final JobRunDao jobRunDao = new JobRunDaoImpl(ServerReference.getQueryUtil());
        registerRequestHandler(new JobRunHistoryRequestHandler(jobRunDao));
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        final RequestHandler requestHandler = requestHandlerMap.get(req.getRequestURI());
        if(requestHandler == null)
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try
        {
            resp.setContentType("application/json");
            requestHandler.handleRequest(req.getReader(), resp.getWriter());
        }
        catch(RuntimeException e)
        {
            LOGGER.log(Level.WARNING, "Failed to handle request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void registerRequestHandler(final RequestHandler<?, ?> requestHandler)
    {
        final String uri = requestHandler.getUri();
        if(requestHandlerMap.containsKey(uri))
        {
            throw new IllegalArgumentException("RequestHandler already registered for " + uri);
        }
        requestHandlerMap.put(uri, requestHandler);
    }
}