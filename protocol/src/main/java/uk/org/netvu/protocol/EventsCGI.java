package uk.org.netvu.core.cgi.events;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.Function;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.ParameterDescription;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.StringConversion;
import uk.org.netvu.core.cgi.common.ParameterMap.Validator;

/**
 * A parameter list for an events.cgi query. Use {@link EventsCGI.Builder} to
 * construct an EventsCGI, or {@link EventsCGI#fromString(String)}.
 */
public final class EventsCGI
{
    private static final ParameterDescription<Integer, Integer> TIME =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithDefault( "time", 0,
                    StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Integer> RANGE =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithDefault( "range",
                    Integer.MAX_VALUE, StringConversion.integer() ) );

    private static final ParameterDescription<Format, Format> FORMAT =
            ParameterDescription.parameterWithDefault( "format", Format.CSV,
                    StringConversion.convenientPartial( Format.fromString ) );

    private static final ParameterDescription<Integer, Integer> MAX_LENGTH =
            ParameterDescription.parameterWithDefault( "listlength", 100, StringConversion.integer() );

    private static final ParameterDescription<String, String> TEXT =
            ParameterDescription.parameterWithDefault( "text", "", StringConversion.string() );

    private static final ParameterDescription<Long, Long> CAMERA_MASK =
            ParameterDescription.parameterWithDefault( "cammask", 0L, StringConversion.getHexToLongStringConversion() );

    private static final ParameterDescription<Integer, Integer> ALARM_MASK =
            ParameterDescription.parameterWithDefault( "almmask", 0, StringConversion.getHexToIntStringConversion() );

    private static final ParameterDescription<Long, Long> VIDEO_MOTION_DETECTION_MASK =
            ParameterDescription.parameterWithDefault( "vmdmask",

            0L, StringConversion.getHexToLongStringConversion() );

    private static final ParameterDescription<Integer, Integer> GPS_MASK =
            ParameterDescription.parameterWithDefault( "gpsmask", 0, StringConversion.getHexToIntStringConversion() );

    private static final ParameterDescription<Integer, Integer> SYSTEM_MASK =
            ParameterDescription.parameterWithDefault( "sysmask", 0, StringConversion.getHexToIntStringConversion() );

    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>()
    {
        {
            // this is an anonymous initialiser - it creates an ArrayList and adds values to it inline.
            add( TIME );
            add( RANGE );
            add( FORMAT );
            add( MAX_LENGTH );
            add( TEXT );
            add( CAMERA_MASK );
            add( ALARM_MASK );
            add( VIDEO_MOTION_DETECTION_MASK );
            add( GPS_MASK );
            add( SYSTEM_MASK );
        }
    };

    private static final List<ParameterDescription<?, ?>> exclusiveParams = new ArrayList<ParameterDescription<?, ?>>()
    {
        {
            // this is an anonymous initialiser - it creates an ArrayList and adds values to it inline.
            add( TEXT );
            add( ALARM_MASK );
            add( VIDEO_MOTION_DETECTION_MASK );
            add( GPS_MASK );
            add( SYSTEM_MASK );
        }
    };

    /**
     * Parses a URL (or the query part of a URL) to obtain an EventsCGI holding
     * the values obtained from the URL.
     * 
     * @param string
     *        the URL (or the query part of a URL) to parse.
     * @throws NullPointerException if string is null.
     * @return an EventsCGI holding the values obtained from the URL.
     */
    public static EventsCGI fromURL( final String string )
    {
        if ( string.length() == 0 )
        {
            throw new IllegalArgumentException( "Cannot parse an empty String into an EventsCGI." );
        }

        final Option<ParameterMap> map = ParameterMap.fromURL( string, params );

        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( map.reason() );
        }

        return new EventsCGI( map.get() );
    }

    private final ParameterMap parameterMap;

    /**
     * Constructs an EventsCGI with the specified ParameterMap holding the
     * values of the parameters that EventsCGI needs.
     * 
     * @param parameterMap
     *        the ParameterMap to get values from.
     */
    private EventsCGI( final ParameterMap parameterMap )
    {
        this.parameterMap = parameterMap;
    }

    /**
     * The 32-bit mask of the alarm input zones to search for.
     * 
     * @return the mask of the alarm input zones to search for.
     */
    public int getAlarmMask()
    {
        return parameterMap.get( ALARM_MASK );
    }

    /**
     * The 64-bit mask of cameras to search for.
     * 
     * @return the mask of cameras to search for.
     */
    public long getCameraMask()
    {
        return parameterMap.get( CAMERA_MASK );
    }

    /**
     * The format of the results.
     * 
     * @return the format of the results.
     */
    public Format getFormat()
    {
        return parameterMap.get( FORMAT );
    }

    /**
     * The 32-bit mask of GPS event types to search for.
     * 
     * @return the mask of GPS event types to search for.
     */
    public int getGpsMask()
    {
        return parameterMap.get( GPS_MASK );
    }

    /**
     * The maximum number of results to obtain. Negative values reverse the
     * direction of the search.
     * 
     * @return the maximum number of results to obtain.
     */
    public int getMaxLength()
    {
        return parameterMap.get( MAX_LENGTH );
    }

    /**
     * The timespan to search, in seconds.
     * 
     * @return the timespan to search, in seconds.
     */
    public int getRange()
    {
        return parameterMap.get( RANGE );
    }

    /**
     * The 32-bit mask of system event types to search for.
     * 
     * @return the mask of system event types to search for.
     */
    public int getSystemMask()
    {
        return parameterMap.get( SYSTEM_MASK );
    }

    /**
     * The text to search for, in the text-in-image data.
     * 
     * @return the text to search for, in the text-in-image data.
     */
    public String getText()
    {
        return parameterMap.get( TEXT );
    }

    /**
     * The time from which to search, in seconds since 1970.
     * 
     * @return the time from which to search, in seconds since 1970.
     */
    public int getTime()
    {
        return parameterMap.get( TIME );
    }

    /**
     * The 64-bit mask of video motion detection channels to search in.
     * 
     * @return the mask of video motion detection channels to search in.
     */
    public long getVideoMotionDetectionMask()
    {
        return parameterMap.get( VIDEO_MOTION_DETECTION_MASK );
    }

    /**
     * Returns /events.cgi? followed by all the parameters specified in this
     * EventsCGI.
     */
    @Override
    public String toString()
    {
        final String theRest = parameterMap.toURLParameters( Lists.remove( params, FORMAT ) );

        return "/events.cgi?format=" + getFormat() + ( theRest.length() == 0 ? "" : "&" ) + theRest;
    }

    /**
     * A builder that takes in all the optional values for events.cgi as per the
     * Video Server Specification, and produces an EventsCGI when build() is
     * called. Each parameter must be supplied no more than once, and the text,
     * alarm mask, VMD mask, GPS mask and sysmask parameters are mutually
     * exclusive. A Builder can only be built once; that is, it can only have
     * build() called on it once. Calling it a second time will cause an
     * IllegalStateException. Setting its values after calling build() will
     * cause an IllegalStateException.
     */
    public static final class Builder
    {
        private Option<ParameterMap> parameterMap =
                Option.getFullOption( new ParameterMap( Validator.mutuallyExclusive( exclusiveParams ) ) );

        /**
         * Constructs a Builder ready to take in all the optional values for
         * events.cgi.
         */
        public Builder()
        {
        }

        /**
         * The 32-bit mask of the alarm input zones that we are interested in.
         * 
         * @param alarmMask
         *        the mask of the alarm input zones that we are interested in.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder alarmMask( final int alarmMask )
        {
            return set( ALARM_MASK, alarmMask );
        }

        /**
         * Constructs an EventsCGI with the values from this Builder.
         * 
         * @throws IllegalStateException
         *         if this Builder has already been built.
         * @return an EventsCGI containing the values from this Builder.
         */
        public EventsCGI build()
        {
            try
            {
                return new EventsCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption( "This Builder has already been built once." );
            }
        }

        /**
         * The 64-bit mask of cameras whose images we want to obtain.
         * 
         * @param camMask
         *        the mask of cameras whose images we want to obtain.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder cameraMask( final long camMask )
        {
            return set( CAMERA_MASK, camMask );
        }

        /**
         * The format that the results should come back in. Note that this API
         * defaults to CSV, though the servers default to JS; parsing CSV is
         * easier. Cannot be null.
         * 
         * @param format
         *        the format that results should come back in.
         * @throws NullPointerException
         *         if format is null.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder
         */
        public Builder format( final Format format )
        {
            return set( FORMAT, format );
        }

        /**
         * The 32-bit mask of GPS event types to search for.  
         * 
         * @param gpsMask
         *        the mask of GPS event types to search for.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder gpsMask( final int gpsMask )
        {
            return set( GPS_MASK, gpsMask );
        }

        /**
         * The maximum number of results to obtain. Negative values reverse the
         * direction of the search.
         * 
         * @param maxLength
         *        the maximum number of results to obtain.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder maxLength( final int maxLength )
        {
            return set( MAX_LENGTH, maxLength );
        }

        /**
         * The timespan to search, in seconds. Cannot be negative.
         * 
         * @param range
         *        the timespan to search, in seconds.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder range( final int range )
        {
            return set( RANGE, range );
        }

        /**
         * The 32-bit mask of system event types to search for.
         * 
         * @param sysMask
         *        the mask of system event types to search for.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder systemMask( final int sysMask )
        {
            return set( SYSTEM_MASK, sysMask );
        }

        /**
         * The text to search for in the text-in-image data. If specified,
         * causes the embedded text-in-image data to be searched for occurences
         * of the supplied string. The search is case sensitive. * can be used
         * as a wildcard to replace one or more characters in the search string.
         * ? can be used as a wildcard to replace a single character in the
         * search string. This parameter cannot be null.
         * 
         * @param text
         *        the text to search for.
         * @throws NullPointerException
         *         if text is null.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder text( final String text )
        {
            return set( TEXT, text );
        }

        /**
         * The time from which to search, in seconds since 1970. Cannot be
         * negative.
         * 
         * @param time
         *        the time from which to search.
         * @throws IllegalArgumentException
         *         if time is negative.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder time( final int time )
        {
            return set( TIME, time );
        }

        /**
         * The 64-bit mask of video motion detection channels to search in.
         * 
         * @param videoMotionDetectionMask
         *        the mask of video motion detection channels to search in.
         * @throws IllegalStateException
         *         if the Builder has already been built, or if this value has
         *         already been set.
         * @return the builder.
         */
        public Builder videoMotionDetectionMask( final long videoMotionDetectionMask )
        {
            return set( VIDEO_MOTION_DETECTION_MASK, videoMotionDetectionMask );
        }

        /**
         * Sets the value of a parameter to a given value, and returns the
         * Builder.
         * 
         * @param <T>
         *        the input type of the specified parameter.
         * @param parameter
         *        the parameter to set a value for.
         * @param value
         *        the value to give that parameter.
         * @return the Builder.
         * @throws IllegalStateException
         *         if the Builder has already been built once.
         */
        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            if ( parameterMap.isEmpty() )
            {
                throw new IllegalStateException( "The Builder has already been built (build() has been called on it)." );
            }
           
            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
            return this;
        }
    }
}