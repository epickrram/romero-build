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

package com.epickrram.romero.common;

import java.util.Comparator;

public final class TestCaseIdentifier implements Comparable<TestCaseIdentifier>
{
    private static final DefaultComparator DEFAULT_COMPARATOR = new DefaultComparator();

    private final String testClass;
    private final int numberOfTestMethods;
    private final long lastRunDurationMillis;
    private final Comparator<TestCaseIdentifier> comparator;

    public TestCaseIdentifier(final String testClass, final int numberOfTestMethods, final long lastRunDurationMillis)
    {
        this.testClass = testClass;
        this.numberOfTestMethods = numberOfTestMethods;
        this.lastRunDurationMillis = lastRunDurationMillis;
        comparator = DEFAULT_COMPARATOR;
    }

    @Override
    public int compareTo(final TestCaseIdentifier other)
    {
        return comparator.compare(this, other);
    }

    public String getTestClass()
    {
        return testClass;
    }

    public int getNumberOfTestMethods()
    {
        return numberOfTestMethods;
    }

    public long getLastRunDurationMillis()
    {
        return lastRunDurationMillis;
    }

    private static final class DefaultComparator implements  Comparator<TestCaseIdentifier>
    {
        @Override
        public int compare(final TestCaseIdentifier o1, final TestCaseIdentifier o2)
        {
            return o1.testClass.compareTo(o2.testClass);
        }
    }
}