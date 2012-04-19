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

import java.util.Map;

import static com.epickrram.romero.acceptance.framework.HttpUtil.post;

public final class Conditions
{
    private Conditions() {}

    static Waiter.Condition postRequestJsonResponseContainsCondition(final String url, final String key, final String expectedValue)
    {
        return new Waiter.Condition()
        {
            @SuppressWarnings({"unchecked"})
            @Override
            public boolean isMet()
            {
                final String response = post(url);
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
