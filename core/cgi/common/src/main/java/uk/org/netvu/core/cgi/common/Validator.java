package uk.org.netvu.core.cgi.common;

import java.util.List;

/**
 * An object that can identify whether a ParameterMap is valid.
 */
public abstract class Validator
{
    /**
     * A Validator that accepts any ParameterMap.
     */
    static final Validator TRUE = new Validator()
    {
        @Override
        public boolean isValid( final ParameterMap parameterMap )
        {
            Checks.notNull( parameterMap );

            return true;
        }
    };

    /**
     * A convenience method that produces a Validator that ensures that only one
     * of the specified exclusive parameters has been set to a value.
     * 
     * @param exclusiveParameters
     *        the parameters that are mutually exclusive.
     * @return a Validator that ensures that only one of the specified mutually
     *         exclusive Parameters has been set to a value.
     */
    public static Validator mutuallyExclusive(
            final List<Parameter<?, ?>> exclusiveParameters )
    {
        Checks.notNull( exclusiveParameters );

        return new Validator()
        {
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
                Checks.notNull( parameterMap );

                int count = 0;
                for ( final Parameter<?, ?> exclusiveParameter : exclusiveParameters )
                {
                    count += parameterMap.isDefault( exclusiveParameter ) ? 0
                            : 1;
                }

                return count < 2;
            }
        };
    }

    /**
     * @param parameterMap
     *        the ParameterMap to check.
     * @return true if the ParameterMap is valid, false otherwise.
     */
    public abstract boolean isValid( ParameterMap parameterMap );
}
