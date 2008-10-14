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
     * Parses each of the specified Strings and gives it to the corresponding
     * parameter, returning a ParameterMap holding the parsed values.
     * 
     * @param parameterDescriptions
     *        the parameters of interest, corresponding with the 'strings'
     *        parameter by index.
     * @param strings
     *        the Strings of interest, corresponding with the parameterDescriptions parameter
     *        by index.
     * @return a ParameterMap holding the parsed values, wrapped in an Option, or an empty Option if any of the values don't parse.
     */
    public static Option<ParameterMap> fromStrings(
            final List<ParameterDescription<?, ?>> parameterDescriptions,
            final List<String> strings )
    {
        Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

        for ( final Pair<ParameterDescription<?, ?>, String> pair : Lists.zip(
                parameterDescriptions, strings ) )
        {
            parameterMap = parameterMap.bind( new Conversion<ParameterMap, Option<ParameterMap>>()
            {
                @Override
                public Option<ParameterMap> convert( final ParameterMap map )
                {
                    return map.withFromString( pair.getFirstComponent(), new URLParameter(
                            pair.getFirstComponent().name, pair.getSecondComponent() ) );
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
    public static Option<ParameterMap> fromURL(
            final String url,
            final List<? extends ParameterDescription<?, ?>> parameterDescriptions )
    {
        Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );
        final List<URLParameter> parts = URLExtractor.nameValuePairs( url );

        for ( final URLParameter part : parts )
        {
            for ( final ParameterDescription<?, ?> parameterDescription : parameterDescriptions )
            {
                if ( part.name.startsWith( parameterDescription.name ) )
                {
                    parameterMap = parameterMap.bind( new Conversion<ParameterMap, Option<ParameterMap>>()
                    {
                        @Override
                        public Option<ParameterMap> convert(
                                final ParameterMap map )
                        {
                            return map.withFromString( parameterDescription,
                                    part );
                        }
                    } );
                }
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

    private ParameterMap( final Map<ParameterDescription<?, ?>, Object> values,
            final Validator validator )
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
                : parameterDescription.getDefaultValue();
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
    public boolean isDefault(
            final ParameterDescription<?, ?> parameterDescription )
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
    public <T, R> ParameterMap set(
            final ParameterDescription<T, R> parameterDescription, final T value )
    {
        if ( value == null )
        {
            throw new NullPointerException( "Values for the "
                    + parameterDescription.name + " parameter cannot be null." );
        }

        final Map<ParameterDescription<?, ?>, Object> copy = new HashMap<ParameterDescription<?, ?>, Object>(
                values );

        copy.put( parameterDescription, parameterDescription.reduce( value,
                get( parameterDescription ) ) );

        final ParameterMap built = new ParameterMap( copy, validator );
        if ( !validator.isValid( built ) )
        {
            throw new IllegalStateException( value + " for "
                    + parameterDescription.name
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
    public String toURLParameters(
            final List<? extends ParameterDescription<?, ?>> parameterDescriptions )
    {
        final StringBuilder builder = new StringBuilder();
        for ( final ParameterDescription<?, ?> parameterDescription : parameterDescriptions )
	{
            for ( final String parameter : parameterDescription.toURLParameter( this ) )
            {
                builder.append( parameter ).append( "&" );
            }
        }

        return builder.toString().replaceAll( "[&]+$", "" ).replaceAll( "[&]+",
                "&" ).replaceAll( "^&", "" );
    }

    private <T, R> Option<ParameterMap> withFromString(
            final ParameterDescription<T, R> parameterDescription,
            final URLParameter keyAndValue )
    {
        return parameterDescription.fromURLParameter( keyAndValue ).map(
                new Conversion<T, ParameterMap>()
                {
                    @Override
                    public ParameterMap convert( final T value )
                    {
                        return set( parameterDescription, value );
                    }

                } );
    }

    /**
     * An object that can identify whether a ParameterMap is valid.
     */
    public static abstract class Validator
    {
        /**
         * A Validator that accepts any ParameterMap.
         */
        static final Validator ACCEPT_ALL = new Validator()
	{
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
		CheckParameters.areNotNull( parameterMap );
                return true;
            }
	    
        };

        /**
         * A convenience method that produces a Validator that ensures that only one
         * of the specified exclusive parameters has been set to a value.
         * 
         * @param exclusiveParameterDescriptions
         *        the parameters that are mutually exclusive.
         * @return a Validator that ensures that only one of the specified mutually
         *         exclusive parameters has been set to a value.
         */
        public static Validator mutuallyExclusive(
            final List<ParameterDescription<?, ?>> exclusiveParameterDescriptions )
        {
            CheckParameters.areNotNull( exclusiveParameterDescriptions );

            return new Validator()
        {
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
                CheckParameters.areNotNull( parameterMap );

                int count = 0;
                for ( final ParameterDescription<?, ?> exclusiveParameterDescription : exclusiveParameterDescriptions )
                {
                    count += parameterMap.isDefault( exclusiveParameterDescription ) ? 0
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

}
