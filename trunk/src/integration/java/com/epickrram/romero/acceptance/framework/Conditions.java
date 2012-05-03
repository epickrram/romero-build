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

package com.epickrram.romero.acceptance.framework;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.epickrram.romero.acceptance.framework.HttpUtil.post;

public final class Conditions
{
    private static final Logger LOGGER = Logger.getLogger(Conditions.class.getSimpleName());

    private Conditions() {}

    static Waiter.Condition postRequestJsonResponseContainsCondition(final String url, final int arrayIndex,
                                                                     final String key, final String expectedValue)
    {
        return new Waiter.Condition()
        {
            @SuppressWarnings({"unchecked"})
            @Override
            public boolean isMet()
            {
                final String response = post(url);
                LOGGER.log(Level.WARNING, response);
                final List<Map<String, String>> array = new Gson().fromJson(response, List.class);
                if(arrayIndex < array.size())
                {
                    final Map<String, String> map = array.get(arrayIndex);
                    return map.containsKey(key) && map.get(key).equals(expectedValue);
                }
                return false;
            }

            @Override
            public String getFailureMessage()
            {
                return String.format("Response from [%s] did not contain text '%s' at array index %d",
                        url, expectedValue, arrayIndex);
            }
        };
    }

    static Waiter.Condition postRequestJsonResponseContainsCondition(final String url, final String key,
                                                                     final String expectedValue)
    {
        return new Waiter.Condition()
        {
            @SuppressWarnings({"unchecked"})
            @Override
            public boolean isMet()
            {
                final String response = post(url);
                LOGGER.log(Level.WARNING, response);
                final Map<String, String> map = new Gson().fromJson(response, Map.class);
                
                return map.containsKey(key) && map.get(key).equals(expectedValue);
            }

            @Override
            public String getFailureMessage()
            {
                return String.format("Response from [%s] did not contain text '%s'", url, expectedValue);
            }
        };
    }
}
