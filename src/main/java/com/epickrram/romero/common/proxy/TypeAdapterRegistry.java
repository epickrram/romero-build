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

import com.epickrram.romero.common.BuildStatus;
import com.epickrram.romero.core.JobDefinitionImpl;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class TypeAdapterRegistry
{
    private final Map<Class, TypeAdapter> typeAdapterMap = new IdentityHashMap<>();
    private final Map<String, TypeAdapter> typeAdapterByClassnameMap = new IdentityHashMap<>();

    public TypeAdapterRegistry()
    {
        registerTypeAdapter(BuildStatus.class, new BuildStatusTypeAdapter());
        registerTypeAdapter(JobDefinitionImpl.class, new TestDefinitionImplTypeAdapter());
    }

    private void registerTypeAdapter(final Class cls, final TypeAdapter typeAdapter)
    {
        final TypeAdapter nullSafeAdapter = typeAdapter.nullSafe();
        typeAdapterMap.put(cls, nullSafeAdapter);
        typeAdapterByClassnameMap.put(cls.getName(), nullSafeAdapter);
    }

    @SuppressWarnings({"unchecked"})
    public <T> TypeAdapter<T> getTypeAdapter(final Class<T> cls)
    {
        return (TypeAdapter<T>) typeAdapterMap.get(cls);
    }

    public Set<Class> getRegisteredTypes()
    {
        return new HashSet<>(typeAdapterMap.keySet());
    }

    public boolean containsEntryFor(final Class<?> cls)
    {
        return typeAdapterMap.containsKey(cls);
    }

    private final class TestDefinitionImplTypeAdapter extends TypeAdapter<JobDefinitionImpl>
    {
        @Override
        public void write(final JsonWriter jsonWriter, final JobDefinitionImpl testDefinition) throws IOException
        {
            jsonWriter.setLenient(true);
            final Object key = testDefinition.getKey();
            final Object data = testDefinition.getData();
            jsonWriter.beginObject();
            jsonWriter.value(key.getClass().getName());
            jsonWriter.value(data.getClass().getName());
            typeAdapterMap.get(key.getClass()).write(jsonWriter, key);
            typeAdapterMap.get(data.getClass()).write(jsonWriter, data);
            jsonWriter.endObject();
        }

        @Override
        public JobDefinitionImpl read(final JsonReader jsonReader) throws IOException
        {
            jsonReader.beginObject();
            final String keyClassname = jsonReader.nextString();
            final String dataClassname = jsonReader.nextString();
            final Object key = typeAdapterByClassnameMap.get(keyClassname).read(jsonReader);
            final Object data = typeAdapterByClassnameMap.get(dataClassname).read(jsonReader);
            jsonReader.endObject();

            return new JobDefinitionImpl(key, data);
        }
    }

    private static final class BuildStatusTypeAdapter extends TypeAdapter<BuildStatus>
    {
        @Override
        public void write(final JsonWriter jsonWriter, final BuildStatus buildStatus) throws IOException
        {
            jsonWriter.value(buildStatus.name());
        }

        @Override
        public BuildStatus read(final JsonReader jsonReader) throws IOException
        {
            return BuildStatus.valueOf(jsonReader.nextString());
        }
    }
}
