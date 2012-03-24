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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public final class CompositeTestCaseWrapperTest
{
    private static final TestingContext TESTING_CONTEXT = new TestingContext();
    @Mock
    private TestCaseWrapper delegateOne;
    @Mock
    private TestCaseWrapper delegateTwo;
    private CompositeTestCaseWrapper composite;

    @Test
    public void shouldDelegateBeforeTest() throws Exception
    {
        composite.beforeTestCase(TESTING_CONTEXT);

        verify(delegateOne).beforeTestCase(TESTING_CONTEXT);
        verify(delegateTwo).beforeTestCase(TESTING_CONTEXT);
    }

    @Test
    public void shouldDelegateAfterTest() throws Exception
    {
        composite.afterTestCase(TESTING_CONTEXT);

        verify(delegateOne).afterTestCase(TESTING_CONTEXT);
        verify(delegateTwo).afterTestCase(TESTING_CONTEXT);
    }

    @Before
    public void setup() throws Exception
    {
        composite = new CompositeTestCaseWrapper(delegateOne, delegateTwo);
    }
}