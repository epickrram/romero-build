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

package com.epickrram.romero.agent;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class ExecutionContext
{
    private final Map<String, Object> valueMap = new HashMap<>();

    @SuppressWarnings({"unchecked"})
    public <T> T getValue(final String key)
    {
        return (T) valueMap.get(key);
    }

    public <T> void setValue(final String key, final T value)
    {
        valueMap.put(key, value);
    }
}