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

package com.epickrram.romero.testing.server.web;

import com.epickrram.romero.server.CompletedJobRunIdentifier;
import com.epickrram.romero.server.web.RequestHandler;
import com.epickrram.romero.testing.common.TestSuiteJobResult;
import com.google.gson.GsonBuilder;

import java.util.Collections;
import java.util.List;

public final class TestSuiteJobResultListHandler extends RequestHandler<CompletedJobRunIdentifier, List<TestSuiteJobResult>>
{
    public TestSuiteJobResultListHandler()
    {
        super(CompletedJobRunIdentifier.class, "/testing/testResults.json");
    }

    @Override
    public List<TestSuiteJobResult> handleRequest(final CompletedJobRunIdentifier input)
    {
        return Collections.emptyList();
    }

    @Override
    protected void registerTypeAdapters(final GsonBuilder gsonBuilder)
    {
        gsonBuilder.registerTypeAdapter(CompletedJobRunIdentifier.class, new CompletedJobRunIdentifierTypeAdapter());
    }

}
