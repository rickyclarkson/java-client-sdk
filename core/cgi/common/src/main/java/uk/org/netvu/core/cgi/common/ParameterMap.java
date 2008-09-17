package uk.org.netvu.core.cgi.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An immutable object that stores the values associated with parameters and
 * provides an interface to retrieving those values. For internal use only.
 */
public final class ParameterMap
{
    /**
     * The validator that is executed against a ParameterMap that has new
     * values. An invalid ParameterMap is never returned to callers.
     */
    public final Validator validator;

    /**
     * The map that stores the value for each parameter.
     */
    public final Map<Parameter<?, ?>, Object> values;

    /**
     * Constructs a ParameterMap that is always valid.
     */
    public ParameterMap()
    {
        this( new Validator()
        {
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
                return true;
            }
        } );
    }

    /**
     * Constructs a ParameterMap with the supplied validator.
     * 
     * @param validator
     *        a test against a ParameterMap for validity.
     */
    public ParameterMap( final Validator validator )
    {
        this( new HashMap<Parameter<?, ?>, Object>(), validator );
    }

    ParameterMap( final Map<Parameter<?, ?>, Object> values,
            final Validator validator )
    {
        this.validator = validator;
        this.values = Collections.unmodifiableMap( values );
    }

    /**
     * Constructs a new ParameterMap with the specified value for the specified
     * parameter, or throws an IllegalStateException if the new ParameterMap
     * would be invalid.
     * 
     * @param <T>
     *        the input type of the parameter.
     * @param <R>
     *        the output type of the parameter.
     * @param parameter
     *        the parameter to store a value for.
     * @param value
     *        the value to store.
     * @return a new ParameterMap with the specified value for the specified
     *         parameter.
     */
    public <T, R> ParameterMap with( final Parameter<T, R> parameter,
            final T value )
    {
        final Map<Parameter<?, ?>, Object> copy = new HashMap<Parameter<?, ?>, Object>(
                values );

        copy.put( parameter, parameter.reduce( value, get( parameter ) ) );

        final ParameterMap built = new ParameterMap( copy, validator );
        if ( !validator.isValid( built ) )
        {
            throw new IllegalStateException( value + " for "
                    + parameter.getName()
                    + " violates the constraints on this parameter map" );
        }

        return built;
    }

    /**
     * Identifies whether the ParameterMap only has the parameter's default
     * value.
     * 
     * @param parameter
     *        the parameter of interest.
     * @return true if the ParameterMap only has the parameter's default value,
     *         false otherwise.
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

    private <T, R> ParameterMap withFromString( final Parameter<T, R> param,
            final URLParameter keyAndValue )
    {
        return with( param, param.fromURLParameter( keyAndValue ) );
    }

    /**
     * Parses the specified URL, with the specified parameters, to return a
     * ParameterMap holding the values parsed from the URL.
     * 
     * @param url
     *        the URL to parse.
     * @param params
     *        the parameters of interest.
     * @return a ParameterMap holding the values parsed from the URL.
     */
    public static ParameterMap fromURL( final String url,
            final List<? extends Parameter<?, ?>> params )
    {
        ParameterMap parameterMap = new ParameterMap();
        final List<URLParameter> parts = URLExtractor.nameValuePairs( url );

        for ( final URLParameter part : parts )
        {
            for ( final Parameter<?, ?> param : params )
            {
                if ( part.name.startsWith( param.getName() ) )
                {
                    parameterMap = parameterMap.withFromString( param, part );
                }
            }
        }

        return parameterMap;
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
     * parameter, returning a ParameterMap holding the parsed values.
     * 
     * @param params
     *        the parameters of interest, corresponding with the strings
     *        parameter by index.
     * @param strings
     *        the Strings of interest, corresponding with the params parameter
     *        by index.
     * @return a ParameterMap holding the parsed values.
     */
    public static ParameterMap fromStrings( final List<Parameter<?, ?>> params,
            final List<String> strings )
    {
        ParameterMap parameterMap = new ParameterMap();

        for ( final Pair<Parameter<?, ?>, String> pair : Lists.zip( params,
                strings ) )
        {
            parameterMap = parameterMap.withFromString( pair.first(),
                    new URLParameter( pair.first().getName(), pair.second() ) );
        }

        return parameterMap;
    }

    public static <T> Conversion<ParameterMap, ParameterMap> withRef(
            final Parameter<T, ?> parameter, final T value )
    {
        return new Conversion<ParameterMap, ParameterMap>()
        {
            @Override
            public ParameterMap convert( final ParameterMap map )
            {
                return map.with( parameter, value );
            }
        };
    }
}