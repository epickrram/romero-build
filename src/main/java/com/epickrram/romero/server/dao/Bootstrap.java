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

import com.epickrram.romero.util.IoUtil;
import com.epickrram.romero.util.LoggingUtil;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class Bootstrap
{
    private static final Logger LOGGER = LoggingUtil.getLogger(Bootstrap.class);

    public static void setupDatabase(final QueryUtil queryUtil, final String schemaDefinitionResource) throws SQLException, IOException
    {
        final Boolean tablesExist = queryUtil.query(new QueryHandler<Boolean>("SHOW TABLES")
        {
            @Override
            public void prepareStatement(final PreparedStatement statement) throws SQLException
            {
            }

            @Override
            public Boolean handleResult(final ResultSet resultSet) throws SQLException
            {
                return resultSet.next();
            }
        });

        LOGGER.info("Tables exist: " + tablesExist);

        if(!tablesExist)
        {
            final String createDatabaseSql = IoUtil.readClasspathResource(schemaDefinitionResource);
            final String[] statements = createDatabaseSql.split(";");

            for (final String statement : statements)
            {
                LOGGER.info("Executing: \n" + statement);
                queryUtil.update(new UpdateOnlyQueryHandler(statement)
                {
                    @Override
                    public void prepareStatement(final PreparedStatement statement) throws SQLException
                    {
                    }
                });
            }
        }
    }
}