package uk.org.netvu.core.cgi.events;

import static uk.org.netvu.core.cgi.common.Option.someRef;
import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param;
import static uk.org.netvu.core.cgi.common.Parameter.param4;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.Validator;

/**
 * A parameter list for an events.cgi query. Use {@link EventsCGI.Builder} to
 * construct an EventsCGI, or {@link EventsCGI#fromString(String)}.
 */
public final class EventsCGI
{
    private static final Parameter<Integer, Integer> TIME = notNegative( param(
            "time", "The time from which to search, in seconds since 1970.", 0,
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> RANGE = notNegative( param(
            "range", "The timespan to search in seconds", Integer.MAX_VALUE,
            Conversion.stringToInt ) );

    private static final Parameter<Format, Format> FORMAT = param( "format",
            "The format that the results should be returned in", Format.CSV,
            Format.fromString );

    private static final Parameter<Integer, Integer> LENGTH = param(
            "listlength",
            "The maximum number of results to obtain.  Negative values reverse the direction of the search.",
            100, Conversion.stringToInt );

    private static final Parameter<String, String> TEXT = param(
            "text",
            "The text to search for in the text-in-image data.  If specified, "
                    + "causes the embedded text-in-image data to be searched for "
                    + "occurences of the supplied string.  The search is case sensitive. "
                    + "* can be used as a wildcard to replace one or more characters in "
                    + "the search string. ? can be used as a wildcard to replace a single "
                    + "character in the search string.", "",
            Option.<String> some() );

    private static final Parameter<Long, Long> CAMERA_MASK = param4( "cammask",
            "The 64-bit mask of cameras whose images we want to obtain.", 0L,
            someRef( Conversion.hexStringToLong ),
            someRef( Conversion.longToHexString ) );

    private static final Parameter<Integer, Integer> ALARM_MASK = param4(
            "almmask",
            "The 32-bit mask of the alarms that we are interested in.", 0,
            someRef( Conversion.hexStringToInt ),
            someRef( Conversion.intToHexString ) );

    private static final Parameter<Long, Long> VIDEO_MOTION_DETECTION_MASK = param4(
            "vmdmask",
            "The 64-bit mask of video motion detection channels to search in.",
            0L, someRef( Conversion.hexStringToLong ),
            someRef( Conversion.longToHexString ) );

    private static final Parameter<Integer, Integer> GPS_MASK = param4(
            "gpsmask", "The 32-bit mask of GPS event types to search for.", 0,
            someRef( Conversion.hexStringToInt ),
            someRef( Conversion.intToHexString ) );

    private static final Parameter<Integer, Integer> SYSTEM_MASK_PARAM = param4(
            "sysmask", "The 32-bit mask of system event types.", 0,
            someRef( Conversion.hexStringToInt ),
            someRef( Conversion.intToHexString ) );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( TIME );
            add( RANGE );
            add( FORMAT );
            add( LENGTH );
            add( TEXT );
            add( CAMERA_MASK );
            add( ALARM_MASK );
            add( VIDEO_MOTION_DETECTION_MASK );
            add( GPS_MASK );
            add( SYSTEM_MASK_PARAM );
        }
    };

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<Parameter<?, ?>> exclusiveParams = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( TEXT );
            add( ALARM_MASK );
            add( VIDEO_MOTION_DETECTION_MASK );
            add( GPS_MASK );
            add( SYSTEM_MASK_PARAM );
        }
    };

    /**
     * A builder that takes in all the optional values for events.cgi as per the
     * Video Server Specification, and produces an EventsCGI when build() is
     * called. Each parameter must be supplied no more than once, and the text,
     * alarm mask, VMD mask, GPS mask and sysmask parameters are mutually
     * exclusive.
     */
    public static final class Builder
    {
        private Option<ParameterMap> real = new Option.Some<ParameterMap>(
                new ParameterMap( Validator.mutuallyExclusive( exclusiveParams ) ) );

        /**
         * Constructs a Builder ready to take in all the optional values for
         * events.cgi.
         */
        public Builder()
        {
        }

        /**
         * The time from which to search, in seconds since 1970.
         * 
         * @param time
         *        the time from which to search.
         * @return the builder.
         */
        public Builder time( final int time )
        {
            real = real.map( ParameterMap.withRef( TIME, time ) );
            return this;
        }

        /**
         * The timespan to search, in seconds.
         * 
         * @param range
         * @return the builder.
         */
        public Builder range( final int range )
        {
            real = real.map( ParameterMap.withRef( RANGE, range ) );
            return this;
        }

        /**
         * The format that the results should come back in. Note that this API
         * defaults to CSV, though the servers default to JS; parsing CSV is
         * easier.
         * 
         * @param format
         *        the format that results should come back in.
         * @return the builder
         */
        public Builder format( final Format format )
        {
            real = real.map( ParameterMap.withRef( FORMAT, format ) );
            return this;
        }

        /**
         * The maximum number of results to obtain. Negative values reverse the
         * direction of the search.
         * 
         * @param maxLength
         *        the maximum number of results to obtain.
         * @return the builder.
         */
        public Builder length( final int maxLength )
        {
            real = real.map( ParameterMap.withRef( LENGTH, maxLength ) );
            return this;
        }

        /**
         * The text to search for in the text-in-image data. If specified,
         * causes the embedded text-in-image data to be searched for occurences
         * of the supplied string. The search is case sensitive. * can be used
         * as a wildcard to replace one or more characters in the search string.
         * ? can be used as a wildcard to replace a single character in the
         * search string.
         * 
         * @param text
         *        the text to search for.
         * @return the builder.
         */
        public Builder text( final String text )
        {
            real = real.map( ParameterMap.withRef( TEXT, text ) );
            return this;
        }

        /**
         * The 64-bit mask of cameras whose images we want to obtain.
         * 
         * @param camMask
         *        the mask of cameras whose images we want to obtain.
         * @return the builder.
         */
        public Builder cameraMask( final long camMask )
        {
            real = real.map( ParameterMap.withRef( CAMERA_MASK, camMask ) );
            return this;
        }

        /**
         * The 32-bit mask of the alarms that we are interested in.
         * 
         * @param alarmMask
         *        the mask of the alarms that we are interested in.
         * @return the builder.
         */
        public Builder alarmMask( final int alarmMask )
        {
            real = real.map( ParameterMap.withRef( ALARM_MASK, alarmMask ) );
            return this;
        }

        /**
         * The 64-bit mask of video motion detection channels to search in.
         * 
         * @param videoMotionDetectionMask
         *        the mask of video motion detection channels to search in.
         * @return the builder.
         */
        public Builder videoMotionDetectionMask(
                final long videoMotionDetectionMask )
        {
            real = real.map( ParameterMap.withRef( VIDEO_MOTION_DETECTION_MASK,
                    videoMotionDetectionMask ) );
            return this;
        }

        /**
         * The 32-bit mask of GPS event types to search for.
         * 
         * @param gpsMask
         *        the mask of GPS event types to search for.
         * @return the builder.
         */
        public Builder gpsMask( final int gpsMask )
        {
            real = real.map( ParameterMap.withRef( GPS_MASK, gpsMask ) );
            return this;
        }

        /**
         * The 32-bit mask of system event types to search for.
         * 
         * @param sysMask
         *        the mask of system event types to search for.
         * @return the builder.
         */
        public Builder systemMask( final int sysMask )
        {
            real = real.map( ParameterMap.withRef( SYSTEM_MASK_PARAM, sysMask ) );
            return this;
        }

        /**
         * Constructs an EventsCGI with the values from this builder.
         * 
         * @return an EventsCGI with the values from this builder.
         */
        public EventsCGI build()
        {
            try
            {
                return new EventsCGI( real.get() );
            }
            finally
            {
                real = new Option.None<ParameterMap>();
            }
        }
    }

    private final ParameterMap parameterMap;

    private EventsCGI( final ParameterMap parameterMap )
    {
        this.parameterMap = parameterMap;
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
     * The timespan to search, in seconds.
     * 
     * @return the timespan to search, in seconds.
     */
    public int getRange()
    {
        return parameterMap.get( RANGE );
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
     * The maximum number of results to obtain. Negative values reverse the
     * direction of the search.
     * 
     * @return the maximum number of results to obtain.
     */
    public int getMaxLength()
    {
        return parameterMap.get( LENGTH );
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
     * The 64-bit mask of cameras to search for.
     * 
     * @return the mask of cameras to search for.
     */
    public long getCameraMask()
    {
        return parameterMap.get( CAMERA_MASK );
    }

    /**
     * The 32-bit mask of the alarms to search for.
     * 
     * @return the mask of the alarms to search for.
     */
    public int getAlarmMask()
    {
        return parameterMap.get( ALARM_MASK );
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
     * The 32-bit mask of GPS event types to search for.
     * 
     * @return the mask of GPS event types to search for.
     */
    public int getGpsMask()
    {
        return parameterMap.get( GPS_MASK );
    }

    /**
     * The 32-bit mask of system event types to search for.
     * 
     * @return the mask of system event types to search for.
     */
    public int getSystemMask()
    {
        return parameterMap.get( SYSTEM_MASK_PARAM );
    }

    /**
     * Parses a URL (or the query part of a URL) to obtain an EventsCGI holding
     * the values obtained from the URL.
     * 
     * @param string
     *        the URL (or the query part of a URL) to parse.
     * @return an EventsCGI holding the values obtained from the URL.
     */
    public static EventsCGI fromString( final String string )
    {
        if ( string.length() == 0 )
        {
            throw new IllegalArgumentException(
                    "Cannot parse an empty String into an EventsCGI." );
        }

        return new EventsCGI( ParameterMap.fromURL( string, params ) );
    }

    /**
     * Returns /events.cgi? followed by all the parameters specified in this
     * EventsCGI.
     */
    @Override
    public String toString()
    {
        final String theRest = parameterMap.toURLParameters( Lists.remove(
                params, FORMAT ) );

        return "/events.cgi?format=" + getFormat()
                + ( theRest.length() == 0 ? "" : "&" ) + theRest;
    }
}
