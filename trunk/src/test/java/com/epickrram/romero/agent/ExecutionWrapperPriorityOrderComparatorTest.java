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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.epickrram.romero.agent.ExecutionWrapper.Priority.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class ExecutionWrapperPriorityOrderComparatorTest
{
    @Mock
    private ExecutionWrapper<String, String> highestPriority;
    @Mock
    private ExecutionWrapper<String, String> defaultPriority;
    @Mock
    private ExecutionWrapper<String, String> lowestPriority;
    private List<ExecutionWrapper<String, String>> wrapperList;

    @Before
    public void setup()
    {
        wrapperList = new ArrayList<>();
        wrapperList.add(defaultPriority);
        wrapperList.add(lowestPriority);
        wrapperList.add(highestPriority);
    }

    @Test
    public void shouldOrderByPriority() throws Exception
    {
        when(highestPriority.getPriority()).thenReturn(HIGHEST);
        when(defaultPriority.getPriority()).thenReturn(DEFAULT);
        when(lowestPriority.getPriority()).thenReturn(LOWEST);
        Collections.sort(wrapperList, ExecutionWrapperPriorityOrderComparator.INSTANCE);

        assertThat(wrapperList.get(0), is(highestPriority));
        assertThat(wrapperList.get(1), is(defaultPriority));
        assertThat(wrapperList.get(2), is(lowestPriority));
    }
}
