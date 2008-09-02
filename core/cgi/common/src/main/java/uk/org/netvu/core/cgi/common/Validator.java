package uk.org.netvu.core.cgi.common;

public abstract class Validator
{
    public abstract boolean isValid( GenericBuilder builder );

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
