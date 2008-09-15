package uk.org.netvu.core.cgi.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An immutable object that stores the values associated with parameters and
 * provides an interface to retrieving those values. For internal use only.
 */
public final class GenericBuilder
{
    /**
     * The validator that is executed against a GenericBuilder that has new
     * values. An invalid GenericBuilder is never returned to callers.
     */
    public final Validator validator;

    /**
     * The map that stores the value for each parameter.
     */
    public final Map<Parameter<?, ?>, Object> values;

    /**
     * Constructs a GenericBuilder that is always valid.
     */
    public GenericBuilder()
    {
        this( new Validator()
        {
            @Override
            public boolean isValid( final GenericBuilder builder )
            {
                return true;
            }
        } );
    }

    /**
     * Constructs a GenericBuilder with the supplied validator.
     * 
     * @param validator
     *        a test against a GenericBuilder for validity.
     */
    public GenericBuilder( final Validator validator )
    {
        this( new HashMap<Parameter<?, ?>, Object>(), validator );
    }

    GenericBuilder( final Map<Parameter<?, ?>, Object> values,
            final Validator validator )
    {
        this.validator = validator;
        this.values = Collections.unmodifiableMap( values );
    }

    /**
     * Constructs a new GenericBuilder with the specified value for the
     * specified parameter, or throws an IllegalStateException if the new
     * GenericBuilder would be invalid.
     * 
     * @param <T>
     *        the input type of the parameter.
     * @param <R>
     *        the output type of the parameter.
     * @param parameter
     *        the parameter to store a value for.
     * @param value
     *        the value to store.
     * @return a new GenericBuilder with the specified value for the specified
     *         parameter.
     */
    public <T, R> GenericBuilder with( final Parameter<T, R> parameter,
            final T value )
    {
        final Map<Parameter<?, ?>, Object> copy = new HashMap<Parameter<?, ?>, Object>(
                values );

        copy.put( parameter, parameter.reduce( value, get( parameter ) ) );

        final GenericBuilder built = new GenericBuilder( copy, validator );
        if ( !validator.isValid( built ) )
        {
            throw new IllegalStateException( value + " for "
                    + parameter.getName()
                    + " violates the constraints on this builder" );
        }

        return built;
    }

    /**
     * Identifies whether the GenericBuilder only has the parameter's default
     * value.
     * 
     * @param parameter
     *        the parameter of interest.
     * @return true if the GenericBuilder only has the parameter's default
     *         value, false otherwise.
     */
    public boolean isDefault( final Parameter<?, ?> parameter )
    {
        return values.get( parameter ) == null;
    }

    /**
     * Gets the stored value for the specified parameter.
     * 
     * @param <T>
     *        the output type of the parameter.
     * @param parameter
     *        the parameter of interest.
     * @return the stored value for the specified parameter.
     */
    @SuppressWarnings("unchecked")
    public <T> T get( final Parameter<?, T> parameter )
    {
        return values.containsKey( parameter ) ? (T) values.get( parameter )
                : parameter.getDefaultValue();
    }

    private <T, R> GenericBuilder withFromString( final Parameter<T, R> param,
            final URLParameter keyAndValue )
    {
        return with( param, param.fromURLParameter( keyAndValue ) );
    }

    /**
     * Parses the specified URL, with the specified parameters, to return a
     * GenericBuilder holding the values parsed from the URL.
     * 
     * @param url
     *        the URL to parse.
     * @param params
     *        the parameters of interest.
     * @return a GenericBuilder holding the values parsed from the URL.
     */
    public static GenericBuilder fromURL( final String url,
            final Iterable<? extends Parameter<?, ?>> params )
    {
        GenericBuilder builder = new GenericBuilder();
        final List<URLParameter> parts = URLExtractor.nameValuePairs( url );

        for ( final URLParameter part : parts )
        {
            for ( final Parameter<?, ?> param : params )
            {
                if ( part.name.startsWith( param.getName() ) )
                {
                    builder = builder.withFromString( param, part );
                }
            }
        }

        return builder;
    }

    /**
     * Produces the parameters part of a URL with the specified parameters in
     * order.
     * 
     * @param params
     *        the parameters of interest, in order.
     * @return the parameters part of a URL.
     */
    public String toURLParameters( final List<? extends Parameter<?, ?>> params )
    {
        final StringBuilder builder = new StringBuilder();
        for ( final Parameter<?, ?> param : params )
        {
            builder.append( param.toURLParameter( this ) ).append( "&" );
        }

        return builder.toString().replaceAll( "[&]+$", "" ).replaceAll( "[&]+",
                "&" ).replaceAll( "^&", "" );
    }

    /**
     * Parses each of the specified Strings and gives it to the corresponding
     * parameter, returning a GenericBuilder holding the parsed values.
     * 
     * @param params
     *        the parameters of interest, corresponding with the strings
     *        parameter by index.
     * @param strings
     *        the Strings of interest, corresponding with the params parameter
     *        by index.
     * @return a GenericBuilder holding the parsed values.
     */
    public static GenericBuilder fromStrings(
            final List<Parameter<?, ?>> params, final List<String> strings )
    {
        GenericBuilder builder = new GenericBuilder();

        for ( final Pair<Parameter<?, ?>, String> pair : Iterables.zip( params,
                strings ) )
        {
            builder = builder.withFromString( pair.first(), new URLParameter(
                    pair.first().getName(), pair.second() ) );
        }

        return builder;
    }
}
