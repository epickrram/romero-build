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

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.epickrram.romero.common.proxy.SerialisationUtil.coerceValue;

public final class PublisherInvocationHandler implements InvocationHandler
{
    private final MethodInvocationSender methodInvocationSender;

    public PublisherInvocationHandler(final MethodInvocationSender methodInvocationSender)
    {
        this.methodInvocationSender = methodInvocationSender;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        final MethodRequest methodRequest = new MethodRequest(method.getDeclaringClass().getName(),
                method.getName(), args);
        final MethodResponse methodResponse;
        try
        {
            methodResponse = methodInvocationSender.invoke(methodRequest);
            if(methodResponse.containsException())
            {
                throw new RuntimeException("Remote invocation caused exception: " + methodResponse.getExceptionMessage());
            }
            return coerceValue(methodResponse.getResult(), method.getReturnType());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to complete remote invocation", e);
        }
    }

}
