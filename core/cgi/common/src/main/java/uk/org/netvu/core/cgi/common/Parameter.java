package uk.org.netvu.core.cgi.common;

public final class Parameter<T>
{
    public final String name;
    public final String description;
    public final Option<T> defaultValue;
    public final Constraint<T> constraint;
    public final Conversion<String, T> fromString;

    private Parameter( final String name, final String description,
            final Option<T> defaultValue, final Constraint<T> constraint,
            final Conversion<String, T> fromString )
    {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.constraint = constraint;
        this.fromString = fromString;
    }

    public static <T> Parameter<T> param( final String name,
            final String description, final Conversion<String, T> fromString )
    {
        return paramImpl( name, description, new Option.None<T>(), fromString );
    }

    public static <T> Parameter<T> param( final String name,
            final String description, final T defaultValue,
            final Conversion<String, T> fromString )
    {
        return paramImpl( name, description,
                new Option.Some<T>( defaultValue ), fromString );
    }

    private static <T> Parameter<T> paramImpl( final String name,
            final String description, final Option<T> defaultValue,
            final Conversion<String, T> fromString )
    {
        return new Parameter<T>( name, description, defaultValue,
                new Constraint<T>()
                {
                    public boolean isValid( final GenericBuilder builder,
                            final Parameter<T> parameter, final T newValue )
                    {
                        return !builder.isSet( parameter );
                    }
                }, fromString );
    }

    @Override
    public String toString()
    {
        return "Parameter(name=" + name + ", description=" + description
                + ", defaultValue=" + defaultValue + ')';
    }

    public static Parameter<Integer> bound( final int lowerInclusive,
            final int higherInclusive, final Parameter<Integer> param )
    {
        return new Parameter<Integer>( param.name, param.description,
                param.defaultValue, new Constraint<Integer>()
                {

                    public boolean isValid( final GenericBuilder builder,
                            final Parameter<Integer> parameter,
                            final Integer newValue )
                    {
                        return param.constraint.isValid( builder, parameter,
                                newValue )
                                && newValue >= lowerInclusive
                                && newValue <= higherInclusive;
                    }
                }, param.fromString );
    }

    public static <T> Parameter<T> not( final T t, final Parameter<T> param )
    {
        return new Parameter<T>( param.name, param.description,
                param.defaultValue, new Constraint<T>()
                {
                    public boolean isValid( final GenericBuilder builder,
                            final Parameter<T> parameter, final T newValue )
                    {
                        return param.constraint.isValid( builder, parameter,
                                newValue )
                                && !newValue.equals( t );
                    }
                }, param.fromString );
    }
}
