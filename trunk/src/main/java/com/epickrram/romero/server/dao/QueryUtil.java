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

package com.epickrram.romero.server.dao;

import com.epickrram.romero.util.LoggingUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class QueryUtil
{
    private static final Logger LOGGER = LoggingUtil.getLogger(QueryUtil.class);
    private final ConnectionManager connectionManager;

    public QueryUtil(final ConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    public <T> T query(final QueryHandler<T> queryHandler) throws SQLException
    {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(queryHandler.getQuery());
            queryHandler.prepareStatement(statement);
            resultSet = statement.executeQuery();
            return queryHandler.handleResult(resultSet);
        }
        finally
        {
            closeSafely(connection, statement, resultSet);
        }
    }

    public void update(final UpdateOnlyQueryHandler queryHandler) throws SQLException
    {
        Connection connection = null;
        PreparedStatement statement = null;
        try
        {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(queryHandler.getQuery());
            queryHandler.prepareStatement(statement);
            statement.executeUpdate();
        }
        finally
        {
            //noinspection NullableProblems
            closeSafely(connection, statement, null);
        }
    }

    private static void closeSafely(final Connection connection, final PreparedStatement statement, final ResultSet resultSet)
    {
        if(resultSet != null)
        {
            try
            {
                resultSet.close();
            }
            catch (SQLException e)
            {
                LOGGER.warning("Failed to close ResultSet: " + e.getMessage());
            }
        }
        if(statement != null)
        {
            try
            {
                statement.close();
            }
            catch (SQLException e)
            {
                LOGGER.warning("Failed to close PreparedStatement: " + e.getMessage());
            }
        }
        if(connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException e)
            {
                LOGGER.warning("Failed to close Connection: " + e.getMessage());
            }
        }
    }
}