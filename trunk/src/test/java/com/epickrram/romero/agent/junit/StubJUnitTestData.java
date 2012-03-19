package com.epickrram.romero.agent.junit;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class StubJUnitTestData
{
    @Ignore
    @Test
    public void shouldBeIgnored() throws Exception
    {
        fail();
    }

    @Test
    public void shouldPass() throws Exception
    {
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