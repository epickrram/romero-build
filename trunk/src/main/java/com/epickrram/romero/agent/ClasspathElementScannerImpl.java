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

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

public final class ClasspathElementScannerImpl implements ClasspathElementScanner
{
    @Override
    public Set<String> findClassNamesMatching(final String regex, final Collection<URL> classpathElements)
    {
        final Pattern pattern = Pattern.compile(regex);
        final Set<String> executionWrapperClassNames = new HashSet<>();
        for (URL classpathElement : classpathElements)
        {
            if(classpathElement.toExternalForm().endsWith(".jar"))
            {
                try
                {
                    final JarInputStream jarInputStream = new JarInputStream(classpathElement.openStream());
                    JarEntry entry;
                    while((entry = jarInputStream.getNextJarEntry()) != null)
                    {
                        final String entryName = entry.getName();
                        if(pattern.matcher(entryName).matches())
                        {
                            executionWrapperClassNames.add(jarEntryToClassName(entryName));
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return executionWrapperClassNames;
    }

    private String jarEntryToClassName(final String entryName)
    {
        return entryName.replace('/', '.').substring(0, entryName.lastIndexOf('.'));
    }
}
