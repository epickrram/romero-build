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

package com.epickrram.romero.core;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class SharedResourceTest
{
    private static final int MAX_CONCURRENT_JOBS = 3;
    private static final String RESOURCE = "resource";
    private SharedResource<String> sharedResource;

    @Test
    public void shouldBeAllowAcquisitionOfMaxConcurrentJobs() throws Exception
    {
        assertThat(0, is(sharedResource.getCurrentUsageCount()));

        for(int i = 0; i < MAX_CONCURRENT_JOBS; i++)
        {
            assertThat(sharedResource.acquire(), is(true));
        }

        assertThat(MAX_CONCURRENT_JOBS, is(sharedResource.getCurrentUsageCount()));
    }

    @Test
    public void shouldNotAllowAcquisitionOfMoreThanMaxConcurrentJobs() throws Exception
    {
        for(int i = 0; i < MAX_CONCURRENT_JOBS; i++)
        {
            assertThat(sharedResource.acquire(), is(true));
        }

        assertThat(sharedResource.acquire(), is(false));
    }

    @Test
    public void shouldAllowFurtherAcquisitionOnceReleased() throws Exception
    {
        for(int i = 0; i < MAX_CONCURRENT_JOBS; i++)
        {
            assertThat(sharedResource.acquire(), is(true));
        }

        for(int i = 0; i < MAX_CONCURRENT_JOBS; i++)
        {
            sharedResource.release();
        }

        assertThat(0, is(sharedResource.getCurrentUsageCount()));

        for(int i = 0; i < MAX_CONCURRENT_JOBS; i++)
        {
            assertThat(sharedResource.acquire(), is(true));
        }
    }

    @Before
    public void setup() throws Exception
    {
        sharedResource = new SharedResource<>(MAX_CONCURRENT_JOBS, RESOURCE);
    }
}
