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
    private transient final int numberOfTestMethods;
    private transient final long lastRunDurationMillis;
    private transient final Comparator<TestCaseIdentifier> comparator;

    public TestCaseIdentifier(final String testClass, final int numberOfTestMethods,
                              final long lastRunDurationMillis)
    {
        this(testClass, numberOfTestMethods, lastRunDurationMillis, DEFAULT_COMPARATOR);
    }

    public TestCaseIdentifier(final String testClass, final int numberOfTestMethods,
                              final long lastRunDurationMillis, final Comparator<TestCaseIdentifier> comparator)
    {
        this.testClass = testClass;
        this.numberOfTestMethods = numberOfTestMethods;
        this.lastRunDurationMillis = lastRunDurationMillis;
        this.comparator = comparator;
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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TestCaseIdentifier that = (TestCaseIdentifier) o;

        return !(testClass != null ? !testClass.equals(that.testClass) : that.testClass != null);

    }

    @Override
    public int hashCode()
    {
        return testClass != null ? testClass.hashCode() : 0;
    }

    public static TestCaseIdentifier toMapKey(final String testClass)
    {
        return new TestCaseIdentifier(testClass, 0, 0L);
    }

    @Override
    public String toString()
    {
        return "TestCaseIdentifier{" +
                "testClass='" + testClass + '\'' +
                '}';
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