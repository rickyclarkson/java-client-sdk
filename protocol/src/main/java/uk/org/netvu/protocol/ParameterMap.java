package uk.org.netvu.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Function;
import uk.org.netvu.util.Option;

/**
 * An immutable object that stores the values associated with parameters and
 * provides an interface to retrieving those values.
 */
final class ParameterMap
{
    /**
     * Parses each of the specified Strings and gives it to the corresponding
     * parameter, returning a ParameterMap holding the parsed values.
     * 
     * @param parameterDescriptions
     *        the parameters of interest, corresponding with the 'strings'
     *        parameter by index.
     * @param strings
     *        the Strings of interest, corresponding with the
     *        parameterDescriptions parameter by index.
     * @return a ParameterMap holding the parsed values, wrapped in an Option,
     *         or an empty Option if any of the values don't parse.
     */
    public static Option<ParameterMap> fromStrings( final List<ParameterDescription<?, ?>> parameterDescriptions,
            final List<String> strings )
    {
        Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

        for ( final Pair<ParameterDescription<?, ?>, String> pair : Lists.zip( parameterDescriptions, strings ) )
        {
            parameterMap = parameterMap.bind( new Function<ParameterMap, Option<ParameterMap>>()
            {
                @Override
                public Option<ParameterMap> apply( final ParameterMap map )
                {
                    return map.parseAndSet( pair.getFirstComponent(), new URLParameter( pair.getFirstComponent().name,
                            pair.getSecondComponent() ) );
                }
            } );
        }

        return parameterMap;
    }

    /**
     * Parses the specified URL, with the specified parameters, to return a
     * ParameterMap holding the values parsed from the URL, wrapped in an
     * Option.
     * 
     * @param url
     *        the URL to parse.
     * @param parameterDescriptions
     *        the parameters of interest.
     * @return a ParameterMap holding the values parsed from the URL.
     */
    public static Option<ParameterMap> fromURL( final String url,
            final List<? extends ParameterDescription<?, ?>> parameterDescriptions )
    {
        Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );
        final List<URLParameter> parts = URLExtractor.nameValuePairs( url );

        for ( final URLParameter part : parts )
        {
            boolean matched = false;

            for ( final ParameterDescription<?, ?> parameterDescription : parameterDescriptions )
            {
                if ( part.name.startsWith( parameterDescription.name ) )
                {
                    matched = true;
                    parameterMap = parameterMap.bind( new Function<ParameterMap, Option<ParameterMap>>()
                    {
                        @Override
                        public Option<ParameterMap> apply( final ParameterMap map )
                        {
                            return map.parseAndSet( parameterDescription, part );
                        }
                    } );
                }
            }

            if ( !matched )
            {
                parameterMap = Option.getEmptyOption( part.name + " is not a known URL parameter" );
            }
        }

        return parameterMap;
    }

    /**
     * The validator that is executed against a ParameterMap that has new
     * values. An invalid ParameterMap is never returned to callers.
     */
    public final Validator validator;

    /**
     * The map that stores the value for each parameter.
     */
    public final Map<ParameterDescription<?, ?>, Object> values;

    /**
     * Constructs a ParameterMap that is always valid.
     */
    public ParameterMap()
    {
        this( Validator.ACCEPT_ALL );
    }

    /**
     * Constructs a ParameterMap with the supplied validator.
     * 
     * @param validator
     *        a test against a ParameterMap for validity.
     */
    public ParameterMap( final Validator validator )
    {
        this( new HashMap<ParameterDescription<?, ?>, Object>(), validator );
    }

    /**
     * Constructs a ParameterMap using the specified Map for storing values, and
     * with the specified Validator.
     * 
     * @param values
     *        the Map to use for storing values.
     * @param validator
     *        a test against a ParameterMap for validity.
     */
    private ParameterMap( final Map<ParameterDescription<?, ?>, Object> values, final Validator validator )
    {
        CheckParameters.areNotNull( validator );

        this.validator = validator;
        this.values = Collections.unmodifiableMap( values );
    }

    /**
     * Gets the stored value for the specified parameter.
     * 
     * @param <T>
     *        the output type of the parameter.
     * @param parameterDescription
     *        the parameter of interest.
     * @return the stored value for the specified parameter.
     */
    @SuppressWarnings( "unchecked" )
    public <T> T get( final ParameterDescription<?, T> parameterDescription )
    {
        return values.containsKey( parameterDescription ) ? (T) values.get( parameterDescription )
                : parameterDescription.defaultValue.apply( this );
    }

    /**
     * Identifies whether the ParameterMap only has the parameter's default
     * value.
     * 
     * @param parameterDescription
     *        the parameter of interest.
     * @return true if the ParameterMap only has the parameter's default value,
     *         false otherwise.
     */
    public boolean isDefault( final ParameterDescription<?, ?> parameterDescription )
    {
        return values.get( parameterDescription ) == null;
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
     * @param parameterDescription
     *        the parameter to store a value for.
     * @param value
     *        the value to store.
     * @return a new ParameterMap with the specified value for the specified
     *         parameter.
     */
    public <T, R> ParameterMap set( final ParameterDescription<T, R> parameterDescription, final T value )
    {
        CheckParameters.areNotNull( parameterDescription );

        if ( value == null )
        {
            throw new NullPointerException( "Values for the " + parameterDescription.name
                    + " parameter cannot be null." );
        }

        final Map<ParameterDescription<?, ?>, Object> copy = new HashMap<ParameterDescription<?, ?>, Object>( values );

        copy.put( parameterDescription, parameterDescription.reduce( this, value, get( parameterDescription ) ) );

        final ParameterMap built = new ParameterMap( copy, validator );
        if ( !validator.isValid( built ) )
        {
            throw new IllegalStateException( value + " for " + parameterDescription.name
                    + " violates the constraints on this parameter map" );
        }

        return built;
    }

    /**
     * Produces the parameters part of a URL with the specified parameters in
     * order.
     * 
     * @param parameterDescriptions
     *        the parameters of interest, in order.
     * @return the parameters part of a URL.
     */
    public String toURLParameters( final List<? extends ParameterDescription<?, ?>> parameterDescriptions )
    {
        final StringBuilder builder = new StringBuilder();
        for ( final ParameterDescription<?, ?> parameterDescription : parameterDescriptions )
        {
            for ( final String parameter : parameterDescription.toURLParameter( this ) )
            {
                builder.append( parameter ).append( "&" );
            }
        }

        return builder.toString().replaceAll( "[&]+$", "" ).replaceAll( "[&]+", "&" ).replaceAll( "^&", "" );
    }

    private <T, R> Option<ParameterMap> parseAndSet( final ParameterDescription<T, R> parameterDescription,
            final URLParameter keyAndValue )
    {
        return parameterDescription.fromURLParameter( keyAndValue ).map( new Function<T, ParameterMap>()
        {
            @Override
            public ParameterMap apply( final T value )
            {
                return set( parameterDescription, value );
            }

        } );
    }

    /**
     * An object that can identify whether a ParameterMap is valid.
     */
    public abstract static class Validator
    {
        /**
         * A Validator that accepts any ParameterMap.
         */
        public static final Validator ACCEPT_ALL = new Validator()
        {
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
                CheckParameters.areNotNull( parameterMap );
                return true;
            }

        };

        /**
         * A convenience method that produces a Validator that ensures that only
         * one of the specified exclusive parameters has been set to a value.
         * 
         * @param exclusiveParameters
         *        the parameters that are mutually exclusive.
         * @return a Validator that ensures that only one of the specified
         *         mutually exclusive parameters has been set to a value.
         */
        public static Validator mutuallyExclusive( final List<ParameterDescription<?, ?>> exclusiveParameters )
        {
            CheckParameters.areNotNull( exclusiveParameters );

            return new Validator()
            {
                @Override
                public boolean isValid( final ParameterMap parameterMap )
                {
                    CheckParameters.areNotNull( parameterMap );

                    int count = 0;
                    for ( final ParameterDescription<?, ?> exclusiveParameterDescription : exclusiveParameters )
                    {
                        count += parameterMap.isDefault( exclusiveParameterDescription ) ? 0 : 1;
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

}
