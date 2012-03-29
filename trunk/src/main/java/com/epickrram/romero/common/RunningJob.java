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

package com.epickrram.romero.common;

public final class RunningJob<K>
{
    private final String agentId;
    private final K jobKey;
    private final long startedTimestamp;

    public RunningJob(final String agentId, final K jobKey, final long startedTimestamp)
    {
        this.agentId = agentId;
        this.jobKey = jobKey;
        this.startedTimestamp = startedTimestamp;
    }

    public static <K> RunningJob<K> create(final String agentId, final K jobKey)
    {
        return new RunningJob<>(agentId, jobKey, System.currentTimeMillis());
    }

    public K getJobKey()
    {
        return jobKey;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public long getStartedTimestamp()
    {
        return startedTimestamp;
    }

    @Override
    public String toString()
    {
        return "RunningJob{" +
                "agentId='" + agentId + '\'' +
                ", jobKey=" + jobKey +
                ", startedTimestamp=" + startedTimestamp +
                '}';
    }
}