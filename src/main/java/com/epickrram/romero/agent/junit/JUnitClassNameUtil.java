package com.epickrram.romero.agent.junit;

public final class JUnitClassNameUtil
{
    static final int TEST_CLASS_NAME = 1;
    static final int TEST_METHOD_NAME = 0;

    private JUnitClassNameUtil()
    {
    }

    static String[] fromDisplayName(final String displayName)
    {
        final int openingBracket = displayName.indexOf('(');
        return new String[] {
                displayName.substring(0, openingBracket),
                displayName.substring(openingBracket + 1, displayName.length() - 1)
        };
    }
}
