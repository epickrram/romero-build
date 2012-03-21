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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public final class ResourceRepositoryTest
{
    private static final JobDefinition<String, String> DEFINITION_1 = new JobDefinitionImpl<>("KEY_1", "DATA");
    private static final JobDefinition<String, String> DEFINITION_2 = new JobDefinitionImpl<>("KEY_2", "DATA");

    private ResourceRepository<String, String, String> resourceRepository;

    @Test
    public void shouldNotAllowMoreThanOneResourcePerJob() throws Exception
    {
        final Resource<String> resource = resourceRepository.acquire(DEFINITION_1);

        assertThat(resource, is(notNullValue()));
        assertThat(resource.getCurrentUsageCount(), is(1));

        assertThat(resourceRepository.acquire(DEFINITION_1), is(nullValue()));
    }

    @Test
    public void shouldReleaseResource() throws Exception
    {
        final Resource<String> resource = resourceRepository.acquire(DEFINITION_1);
        resourceRepository.release(DEFINITION_1);

        assertThat(resource.getCurrentUsageCount(), is(0));
    }

    @Test
    public void shouldNotReleaseResourceIfItIsNotAllocatedToReleasingJob() throws Exception
    {
        final Resource<String> resource = resourceRepository.acquire(DEFINITION_1);
        resourceRepository.release(DEFINITION_2);

        assertThat(resource.getCurrentUsageCount(), is(1));
    }

    @Test
    public void shouldReturnNullIfNoMoreResourcesAreAvailable() throws Exception
    {
        final Resource<String> resourceOne = resourceRepository.acquire(DEFINITION_1);
        final Resource<String> resourceTwo = resourceRepository.acquire(DEFINITION_2);

        assertThat(resourceOne, is(notNullValue()));
        assertThat(resourceTwo, is(notNullValue()));

        final Resource<String> attemptedResource = resourceRepository.acquire(new JobDefinitionImpl<>("KEY_3", "DATA"));
        assertThat(attemptedResource, is(nullValue()));
    }

    @Before
    public void setup() throws Exception
    {
        resourceRepository = new ResourceRepository<>();
        resourceRepository.addResource(new SharedResource<>(1, "RESOURCE_1"));
        resourceRepository.addResource(new SharedResource<>(1, "RESOURCE_2"));
    }
}