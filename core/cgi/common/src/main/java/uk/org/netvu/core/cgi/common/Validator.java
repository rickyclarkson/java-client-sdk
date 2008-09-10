package uk.org.netvu.core.cgi.common;

/**
 * An object that can identify whether a GenericBuilder is valid.
 */
public abstract class Validator
{
    /**
     * @param builder
     *        the GenericBuilder to check.
     * @return true if the GenericBuilder is valid, false otherwise.
     */
    public abstract boolean isValid( GenericBuilder builder );

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
            final Iterable<Parameter<?, ?>> exclusiveParameters )
    {
        return new Validator()
        {
            @Override
            public boolean isValid( final GenericBuilder builder )
            {
                int count = 0;
                for ( final Parameter<?, ?> exclusiveParameter : exclusiveParameters )
                {
                    count += builder.isDefault( exclusiveParameter ) ? 0 : 1;
                }

                return count < 2;
            }
        };
    }
}
