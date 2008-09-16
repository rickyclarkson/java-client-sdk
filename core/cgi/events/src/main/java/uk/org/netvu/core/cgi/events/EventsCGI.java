package uk.org.netvu.core.cgi.events;

import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.Validator;

/**
 * A parameter list for an events.cgi query. Use {@link EventsCGI.Builder} to
 * construct an EventsCGI, or {@link EventsCGI#fromString(String)}.
 */
public final class EventsCGI
{
    private static final Parameter<Integer, Integer> timeParam = notNegative( param(
            "time", "The time from which to search, in seconds since 1970.", 0,
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> rangeParam = notNegative( param(
            "range", "The timespan to search in seconds", Integer.MAX_VALUE,
            Conversion.stringToInt ) );

    private static final Parameter<Format, Format> formatParam = param(
            "format", "The format that the results should be returned in",
            Format.CSV, Format.fromString );

    private static final Parameter<Integer, Integer> lengthParam = param(
            "listlength",
            "The maximum number of results to obtain.  Negative values reverse the direction of the search.",
            100, Conversion.stringToInt );

    private static final Parameter<String, String> textParam = param(
            "text",
            "The text to search for in the text-in-image data.  If specified, "
                    + "causes the embedded text-in-image data to be searched for "
                    + "occurences of the supplied string.  The search is case sensitive. "
                    + "* can be used as a wildcard to replace one or more characters in "
                    + "the search string. ? can be used as a wildcard to replace a single "
                    + "character in the search string.", "",
            Conversion.<String> identity() );

    private static final Parameter<Long, Long> camMaskParam = param( "cammask",
            "The 64-bit mask of cameras whose images we want to obtain.", 0L,
            Conversion.hexStringToLong, Conversion.longToHexString );

    private static final Parameter<Integer, Integer> alarmMaskParam = param(
            "almmask",
            "The 32-bit mask of the alarms that we are interested in.", 0,
            Conversion.hexStringToInt, Conversion.intToHexString );

    private static final Parameter<Long, Long> vmdMaskParam = param( "vmdmask",
            "The 64-bit mask of video motion detection channels to search in.",
            0L, Conversion.hexStringToLong, Conversion.longToHexString );

    private static final Parameter<Integer, Integer> gpsMaskParam = param(
            "gpsmask", "The 32-bit mask of GPS event types to search for.", 0,
            Conversion.hexStringToInt, Conversion.intToHexString );

    private static final Parameter<Integer, Integer> sysMaskParam = param(
            "sysmask", "The 32-bit mask of system event types.", 0,
            Conversion.hexStringToInt, Conversion.intToHexString );

    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( timeParam );
            add( rangeParam );
            add( formatParam );
            add( lengthParam );
            add( textParam );
            add( camMaskParam );
            add( alarmMaskParam );
            add( vmdMaskParam );
            add( gpsMaskParam );
            add( sysMaskParam );
        }
    };

    private static final List<Parameter<?, ?>> exclusiveParams = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( textParam );
            add( alarmMaskParam );
            add( vmdMaskParam );
            add( gpsMaskParam );
            add( sysMaskParam );
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
        private GenericBuilder real = new GenericBuilder(
                Validator.mutuallyExclusive( exclusiveParams ) );

        /**
         * The time from which to search, in seconds since 1970.
         * 
         * @param time
         *        the time from which to search.
         * @return the builder.
         */
        public Builder time( final int time )
        {
            real = real.with( timeParam, time );
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
            real = real.with( rangeParam, range );
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
            real = real.with( formatParam, format );
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
            real = real.with( lengthParam, maxLength );
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
            real = real.with( textParam, text );
            return this;
        }

        /**
         * The 64-bit mask of cameras whose images we want to obtain.
         * 
         * @param camMask
         *        the mask of cameras whose images we want to obtain.
         * @return the builder.
         */
        public Builder camMask( final long camMask )
        {
            real = real.with( camMaskParam, camMask );
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
            real = real.with( alarmMaskParam, alarmMask );
            return this;
        }

        /**
         * The 64-bit mask of video motion detection channels to search in.
         * 
         * @param vmdMask
         *        the mask of video motion detection channels to search in.
         * @return the builder.
         */
        public Builder vmdMask( final long vmdMask )
        {
            real = real.with( vmdMaskParam, vmdMask );
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
            real = real.with( gpsMaskParam, gpsMask );
            return this;
        }

        /**
         * The 32-bit mask of system event types to search for.
         * 
         * @param sysMask
         *        the mask of system event types to search for.
         * @return the builder.
         */
        public Builder sysMask( final int sysMask )
        {
            real = real.with( sysMaskParam, sysMask );
            return this;
        }

        /**
         * Constructs an EventsCGI with the values from this builder.
         * 
         * @return an EventsCGI with the values from this builder.
         */
        public EventsCGI build()
        {
            return new EventsCGI( real );
        }
    }

    private final GenericBuilder builder;

    private EventsCGI( final GenericBuilder builder )
    {
        this.builder = builder;
    }

    /**
     * The time from which to search, in seconds since 1970.
     * 
     * @return the time from which to search, in seconds since 1970.
     */
    public int getTime()
    {
        return builder.get( timeParam );
    }

    /**
     * The timespan to search, in seconds.
     * 
     * @return the timespan to search, in seconds.
     */
    public int getRange()
    {
        return builder.get( rangeParam );
    }

    /**
     * The format of the results.
     * 
     * @return the format of the results.
     */
    public Format getFormat()
    {
        return builder.get( formatParam );
    }

    /**
     * The maximum number of results to obtain. Negative values reverse the
     * direction of the search.
     * 
     * @return the maximum number of results to obtain.
     */
    public int getMaxLength()
    {
        return builder.get( lengthParam );
    }

    /**
     * The text to search for, in the text-in-image data.
     * 
     * @return the text to search for, in the text-in-image data.
     */
    public String getText()
    {
        return builder.get( textParam );
    }

    /**
     * The 64-bit mask of cameras to search for.
     * 
     * @return the mask of cameras to search for.
     */
    public long getCamMask()
    {
        return builder.get( camMaskParam );
    }

    /**
     * The 32-bit mask of the alarms to search for.
     * 
     * @return the mask of the alarms to search for.
     */
    public int getAlarmMask()
    {
        return builder.get( alarmMaskParam );
    }

    /**
     * The 64-bit mask of video motion detection channels to search in.
     * 
     * @return the mask of video motion detection channels to search in.
     */
    public long getVmdMask()
    {
        return builder.get( vmdMaskParam );
    }

    /**
     * The 32-bit mask of GPS event types to search for.
     * 
     * @return the mask of GPS event types to search for.
     */
    public int getGpsMask()
    {
        return builder.get( gpsMaskParam );
    }

    /**
     * The 32-bit mask of system event types to search for.
     * 
     * @return the mask of system event types to search for.
     */
    public int getSysMask()
    {
        return builder.get( sysMaskParam );
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
            throw new IllegalArgumentException();
        }

        return new EventsCGI( GenericBuilder.fromURL( string, params ) );
    }

    /**
     * Returns /events.cgi? followed by all the parameters specified in this
     * EventsCGI.
     */
    @Override
    public String toString()
    {
        final String theRest = builder.toURLParameters( Lists.remove( params,
                formatParam ) );

        return "/events.cgi?format=" + getFormat()
                + ( theRest.length() == 0 ? "" : "&" ) + theRest;
    }
}
