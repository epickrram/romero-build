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

import com.epickrram.romero.util.LoggingUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public final class ResourceRepository<T, K, D>
{
    private static final Logger LOGGER = LoggingUtil.getLogger(ResourceRepository.class.getSimpleName());
    
    private final Map<K, Resource<T>> allocatedResourceMap = new ConcurrentHashMap<>();
    private final List<Resource<T>> resourceList = new CopyOnWriteArrayList<>();

    void addResource(final Resource<T> resource)
    {
        resourceList.add(resource);
    }

    public Resource<T> acquire(final JobDefinition<K, D> jobDefinition)
    {
        if(allocatedResourceMap.containsKey(jobDefinition.getKey()))
        {
            LOGGER.warning("job [" + jobDefinition.getKey() + "] is attempting to acquire more than one resource");
            return null;
        }
        for (Resource<T> resource : resourceList)
        {
            if(resource.acquire())
            {
                allocatedResourceMap.put(jobDefinition.getKey(), resource);
                return resource;
            }
        }
        return null;
    }
    
    public void release(final JobDefinition<K, D> jobDefinition)
    {
        final K key = jobDefinition.getKey();
        if(allocatedResourceMap.containsKey(key))
        {
            allocatedResourceMap.remove(key).release();
        }
    }
}