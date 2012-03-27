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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class PublisherInvocationHandlerTest
{
    private static final int INT_VALUE = 47;
    private static final String STR_VALUE = "strValue";
    private static final double DOUBLE_VALUE = 3.147d;
    @Mock
    private MethodInvocationSender sender;

    @Test
    public void shouldSerialiseArgumentsAndInvokeSender() throws Exception
    {
        when(sender.invoke(Matchers.<MethodRequest>any())).thenReturn(new MethodResponse(null, null));

        createProxy().sendNoReturnValue(INT_VALUE, STR_VALUE);

        verify(sender).invoke(argThat(matchesMethodRequest(RemoteObject.class.getName(), "sendNoReturnValue", INT_VALUE, STR_VALUE)));
    }

    @Test
    public void shouldInvokeSenderAndReturnValue() throws Exception
    {
        when(sender.invoke(Matchers.<MethodRequest>any())).thenReturn(new MethodResponse(null, DOUBLE_VALUE));

        final Double value = createProxy().shouldReturnValue();

        assertThat(value, is(DOUBLE_VALUE));

        verify(sender).invoke(argThat(matchesMethodRequest(RemoteObject.class.getName(), "shouldReturnValue")));
    }

    private static Matcher<MethodRequest> matchesMethodRequest(final String className,
                                                               final String methodName,
                                                               final Object... expectedArgs)
    {
        return new TypeSafeMatcher<MethodRequest>()
        {
            @Override
            public boolean matchesSafely(final MethodRequest methodRequest)
            {
                final Object[] serialisedArguments = methodRequest.getArguments();
                if(!(methodRequest.getClassName().equals(className) &&
                     methodRequest.getMethodName().equals(methodName)))
                {
                    return false;
                }
                if((serialisedArguments == null || serialisedArguments.length == 0) &&
                        (expectedArgs == null || expectedArgs.length == 0))
                {
                    return true;
                }
                if(serialisedArguments.length != expectedArgs.length)
                {
                    return false;
                }
                for (int i = 0, n = serialisedArguments.length; i < n; i++)
                {
                    final Object serialisedArgument = serialisedArguments[i];
                    if (!((serialisedArgument == null && expectedArgs[i] == null) ||
                       (serialisedArgument != null && expectedArgs[i] != null &&
                        serialisedArgument.equals(expectedArgs[i]))))
                    {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void describeTo(final Description description)
            {
                for (Object expectedArg : expectedArgs)
                {
                    description.appendText(String.valueOf(expectedArg)).appendText("\n");
                }
            }
        };
    }

    private RemoteObject createProxy()
    {
        final PublisherInvocationHandler handler = new PublisherInvocationHandler(sender);
        return (RemoteObject) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{RemoteObject.class}, handler);
    }

    interface RemoteObject
    {
        void sendNoReturnValue(final int foo, final String bar);
        Double shouldReturnValue();
    }
}
