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

public final class CompletedJobRunIdentifier
{
    private final String jobRunIdentifier;
    private final long startTimestamp;

    public CompletedJobRunIdentifier(final String jobRunIdentifier, final long startTimestamp)
    {
        this.jobRunIdentifier = jobRunIdentifier;
        this.startTimestamp = startTimestamp;
    }

    public String getJobRunIdentifier()
    {
        return jobRunIdentifier;
    }

    public long getStartTimestamp()
    {
        return startTimestamp;
    }

    @Override
    public String toString()
    {
        return "CompletedJobRunIdentifier{" +
                "jobRunIdentifier='" + jobRunIdentifier + '\'' +
                ", startTimestamp=" + startTimestamp +
                '}';
    }

    public static final class Builder
    {
        private String jobRunIdentifier;
        private long startTimestamp;

        public void jobRunIdentifier(final String jobRunIdentifier)
        {
            this.jobRunIdentifier = jobRunIdentifier;
        }

        public void startTimestamp(final long startTimestamp)
        {
            this.startTimestamp = startTimestamp;
        }

        public CompletedJobRunIdentifier create()
        {
            validate();

            return new CompletedJobRunIdentifier(jobRunIdentifier, startTimestamp);
        }

        private void validate()
        {
            if(jobRunIdentifier == null || startTimestamp == 0)
            {
                throw new IllegalStateException(String.format("Cannot create object, jobRunId = %s, startTimestamp = %d",
                                    jobRunIdentifier, startTimestamp));
            }
        }
    }
}
