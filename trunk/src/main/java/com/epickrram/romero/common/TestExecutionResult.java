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

import com.epickrram.freewheel.io.DecoderStream;
import com.epickrram.freewheel.io.EncoderStream;
import com.epickrram.freewheel.protocol.AbstractTranslator;
import com.epickrram.freewheel.protocol.Translatable;

import java.io.IOException;

@Translatable(codeBookId = 2000)
public final class TestExecutionResult
{
    private final String testClass;
    private final String testMethod;
    private final TestStatus testStatus;
    private final long durationMillis;
    private final String stdout;
    private final String stderr;
    private final String throwable;

    private TestExecutionResult(final String testClass, final String testMethod, final TestStatus testStatus,
                                final long durationMillis, final String stdout, final String stderr,
                                final String throwable)
    {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.testStatus = testStatus;
        this.durationMillis = durationMillis;
        this.stdout = stdout;
        this.stderr = stderr;
        this.throwable = throwable;
    }

    public String getTestClass()
    {
        return testClass;
    }

    public String getTestMethod()
    {
        return testMethod;
    }

    public TestStatus getTestStatus()
    {
        return testStatus;
    }

    public long getDurationMillis()
    {
        return durationMillis;
    }

    public String getStdout()
    {
        return stdout;
    }

    public String getStderr()
    {
        return stderr;
    }

    public String getThrowable()
    {
        return throwable;
    }

    @Override
    public String toString()
    {
        return "TestExecutionResult{" +
                "testClass='" + testClass + '\'' +
                ", testMethod='" + testMethod + '\'' +
                ", testStatus=" + testStatus +
                ", durationMillis=" + durationMillis +
                ", stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                ", throwable=" + throwable +
                '}';
    }

    public static final class Translator extends AbstractTranslator<TestExecutionResult>
    {
        @Override
        protected void doEncode(final TestExecutionResult encodable, final EncoderStream encoderStream) throws IOException
        {
            encoderStream.writeString(encodable.testClass);
            encoderStream.writeString(encodable.testMethod);
            encoderStream.writeObject(encodable.testStatus);
            encoderStream.writeLong(encodable.durationMillis);
            encoderStream.writeString(encodable.stdout);
            encoderStream.writeString(encodable.stderr);
            encoderStream.writeString(encodable.throwable);
        }

        @Override
        protected TestExecutionResult doDecode(final DecoderStream decoderStream) throws IOException
        {
            final String testClass = decoderStream.readString();
            final String testMethod = decoderStream.readString();
            final TestStatus testStatus = decoderStream.readObject();
            final long durationMillis = decoderStream.readLong();
            final String stdout = decoderStream.readString();
            final String stderr = decoderStream.readString();
            final String throwable = decoderStream.readString();

            return new TestExecutionResult(testClass, testMethod, testStatus, durationMillis, stdout, stderr, throwable);
        }
    }

    public static final class Builder
    {
        private String testClass;
        private String testMethod;
        private TestStatus testStatus;
        private long durationMillis;
        private String stdout;
        private String stderr;
        private String throwable;

        public TestExecutionResult newInstance()
        {
            return new TestExecutionResult(testClass, testMethod, testStatus, durationMillis, stdout, stderr, throwable);
        }

        public Builder testClass(final String testClass)
        {
            this.testClass = testClass;
            return this;
        }

        public Builder testMethod(final String testMethod)
        {
            this.testMethod = testMethod;
            return this;
        }

        public Builder testStatus(final TestStatus testStatus)
        {
            this.testStatus = testStatus;
            return this;
        }

        public Builder durationMillis(final long durationMillis)
        {
            this.durationMillis = durationMillis;
            return this;
        }

        public Builder stdout(final String stdout)
        {
            this.stdout = stdout;
            return this;
        }

        public Builder stderr(final String stderr)
        {
            this.stderr = stderr;
            return this;
        }

        public Builder throwable(final String throwable)
        {
            this.throwable = throwable;
            return this;
        }
    }
}