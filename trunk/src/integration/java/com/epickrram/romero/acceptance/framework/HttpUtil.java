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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;

import static java.lang.String.format;

public final class HttpUtil
{
    private static final ConnectionHandler OK_GET_REQUEST_HANDLER = new ExpectOkGetRequestConnectionHandler();

    private HttpUtil()
    {
    }

    public static void getIgnoringResponse(final String url)
    {
        executeGetRequest(url, OK_GET_REQUEST_HANDLER);
    }

    public static String post(final String url)
    {
        return executePostRequest(url, OK_GET_REQUEST_HANDLER);
    }

    private static final class ExpectOkGetRequestConnectionHandler implements ConnectionHandler
    {
        @Override
        public String withConnection(final HttpURLConnection connection) throws IOException
        {
            final int responseCode = connection.getResponseCode();
            if (responseCode != 200)
            {
                throw new IllegalStateException(format("Expected response code 200 from [%s], but was %d",
                        connection.getURL().toExternalForm(), responseCode));
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
                response.append(line).append('\n');
            }
            return response.toString();
        }
    }

    private static String executeGetRequest(final String url, final ConnectionHandler connectionHandler)
    {
        return executeRequest(url, connectionHandler, "GET");
    }

    private static String executePostRequest(final String url, final ConnectionHandler connectionHandler)
    {
        return executeRequest(url, connectionHandler, "POST");
    }

    private static String executeRequest(final String url, final ConnectionHandler connectionHandler, final String requestType)
    {
        final String response;
        HttpURLConnection connection = null;
        try
        {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(requestType);
            connection.connect();
            response = connectionHandler.withConnection(connection);
        }
        catch (IOException e)
        {
            throw new RuntimeException(format("Failed to execute HTTP GET [%s]: %s", url, e.getMessage()));
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
        return response;
    }

    interface ConnectionHandler
    {
        String withConnection(final HttpURLConnection connection) throws IOException;
    }
}
