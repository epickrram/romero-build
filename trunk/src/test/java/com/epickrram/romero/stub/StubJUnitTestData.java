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

package com.epickrram.romero.stub;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class StubJUnitTestData
{
    public static final String PROP_KEY_1 = "property.1";
    public static final String PROP_KEY_2 = "property.2";
    public static String PROP_VALUE_1;
    public static String PROP_VALUE_2;

    @Ignore
    @Test
    public void shouldBeIgnored() throws Exception
    {
        fail();
    }

    @Test
    public void shouldPass() throws Exception
    {
        PROP_VALUE_1 = System.getProperty(PROP_KEY_1);
        PROP_VALUE_2 = System.getProperty(PROP_KEY_2);
        assertThat(true, is(true));
    }

    @Test
    public void shouldFailAssumption() throws Exception
    {
        assertThat(false, is(true));
    }

    @Test
    public void shouldThrowException() throws Exception
    {
        throw new RuntimeException("ExpectedException");
    }
}