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

package com.epickrram.romero.common.proxy;

public final class SerialisationTestFixture
{
    static final Object[] METHOD_ARGS = new Object[]
            {
                    "foo",
                    null,
                    3.147d
            };

    static final String CLASS_NAME = "com.epickrram.romero.TestClass";
    static final String METHOD_NAME = "someMethod";
    static final MethodRequest METHOD_REQUEST = new MethodRequest(CLASS_NAME, METHOD_NAME, METHOD_ARGS);
    static final MethodResponse METHOD_RESPONSE = new MethodResponse(null, METHOD_ARGS[0]);
    static final String SERIALISED_METHOD_REQUEST = "{\"className\":\"com.epickrram.romero.TestClass\",\"methodName\":\"someMethod\",\"arguments\":[\"foo\",null,3.147]}";
    static final String SERIALISED_METHOD_RESPONSE = "{\"result\":\"foo\"}";
}
