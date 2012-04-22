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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DriverManagerConnectionManager implements ConnectionManager
{
    private final String databaseConnectionUrl;

    public DriverManagerConnectionManager(final String driverClassname, final String databaseConnectionUrl)
    {
        this.databaseConnectionUrl = databaseConnectionUrl;
        try
        {
            Class.forName(driverClassname);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Cannot load driver class " + driverClassname);
        }
    }

    @Override
    public Connection getConnection()
    {
        try
        {
            return DriverManager.getConnection(databaseConnectionUrl);
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Cannot connect to database", e);
        }
    }
}