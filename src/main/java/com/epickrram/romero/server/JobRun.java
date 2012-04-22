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

package com.epickrram.romero.server;

public final class JobRun
{
    private final int id;
    private final String identifier;
    private final long startTimestamp;
    private final long endTimestamp;

    public JobRun(final int id, final String identifier, final long startTimestamp, final long endTimestamp)
    {
        this.id = id;
        this.identifier = identifier;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public int getId()
    {
        return id;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public long getStartTimestamp()
    {
        return startTimestamp;
    }

    public long getEndTimestamp()
    {
        return endTimestamp;
    }
}
