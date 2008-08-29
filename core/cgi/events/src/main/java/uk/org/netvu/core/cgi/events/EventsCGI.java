package uk.org.netvu.core.cgi.events;

import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.UInt31;
import uk.org.netvu.core.cgi.common.Validator;

/**
 * A parameter list for an events.cgi query, checked for correctness.
 */
public final class EventsCGI
{
    private static final Parameter<UInt31, UInt31> timeParam = param( "time",
            "The time from which to search, in seconds since 1970.",
            new UInt31( 0 ), UInt31.fromString );

    private static final Parameter<UInt31, UInt31> rangeParam = param( "range",
            "The timespan to search in seconds",
            new UInt31( Integer.MAX_VALUE ), UInt31.fromString );

    private static final Parameter<Format, Format> formatParam = param(
            "format", "The format that the results should be returned in",
            Format.CSV, Format.fromString );

    private static final Parameter<Integer, Integer> lengthParam = param(
            "listlength",
            "The maximum number of results to obtain.  Negative values reverse the direction of the search.",
            100, Conversion.stringToInt );

    private static final Parameter<String, String> textParam = param(
            "text",
            "The text to search for in the text-in-image data.  If specified, causes the embedded text-in-image data to be searched for occurences of the supplied string.  The search is case sensitive. * can be used as a wildcard to replace one or more characters in the search string. ? can be used as a wildcard to replace a single character in the search string.",
            "", Conversion.<String> identity() );

    private static final Parameter<Long, Long> camMaskParam = param( "cammask",
            "The 64-bit mask of cameras whose images we want to obtain.", 0L,
            Conversion.hexStringToLong );

    private static final Parameter<Integer, Integer> alarmMaskParam = param(
            "almmask",
            "The 32-bit mask of the alarms that we are interested in.", 0,
            Conversion.hexStringToInt );

    private static final Parameter<Long, Long> vmdMaskParam = param( "vmdmask",
            "The 64-bit mask of video motion detection channels to search in.",
            0L, Conversion.hexStringToLong );

    private static final Parameter<Integer, Integer> gpsMaskParam = param(
            "gpsmask", "The 32-bit mask of GPS event types to search for.", 0,
            Conversion.hexStringToInt );

    private static final Parameter<Integer, Integer> sysMaskParam = param(
            "sysmask", "The 32-bit mask of system event types.", 0,
            Conversion.hexStringToInt );

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
     * called. Each parameter must be supplied no more than once, and if a text
     * parameter is supplied, the alarm mask, VMD mask, GPS mask and sysmask
     * parameters cannot be supplied, as the text parameter overrides these in
     * the video servers.
     */
    public static final class Builder
    {
        private GenericBuilder real = new GenericBuilder( new Validator()
        {
            public boolean isValid( final GenericBuilder builder )
            {
                int count = 0;
                for ( final Parameter<?, ?> exclusiveParameter : exclusiveParams )
                {
                    count += builder.isDefault( exclusiveParameter ) ? 0 : 1;
                }

                return count < 2;
            }
        } );

        /**
         * The time from which to search, in seconds since 1970.
         * 
         * @return the builder.
         */
        public Builder time( final int time )
        {
            real = real.with( timeParam, new UInt31( time ) );
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
            real = real.with( rangeParam, new UInt31( range ) );
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
         * @return the builder.
         */
        public Builder gpsMask( final int gpsMask )
        {
            real = real.with( gpsMaskParam, gpsMask );
            return this;
        }

        /**
         * The 32-bit mask of system event types.
         * 
         * @return the builder.
         */
        public Builder sysMask( final int sysMask )
        {
            real = real.with( sysMaskParam, sysMask );
            return this;
        }

        /**
         * Constructs an EventsCGI with the values from this builder.
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
     */
    public int getTime()
    {
        return builder.get( timeParam ).toInt();
    }

    /**
     * The timespan to search, in seconds.
     */
    public int getRange()
    {
        return builder.get( rangeParam ).toInt();
    }

    /**
     * The format of the results.
     */
    public Format getFormat()
    {
        return builder.get( formatParam );
    }

    /**
     * The maximum number of results to obtain. Negative values reverse the
     * direction of the search.
     */
    public int getMaxLength()
    {
        return builder.get( lengthParam );
    }

    /**
     * The text to search for, in the text-in-image data.
     */
    public String getText()
    {
        return builder.get( textParam );
    }

    /**
     * The 64-bit mask of cameras whose images we want to obtain.
     */
    public long getCamMask()
    {
        return builder.get( camMaskParam );
    }

    /**
     * The 32-bit mask of the alarms that we are interested in.
     */
    public int getAlarmMask()
    {
        return builder.get( alarmMaskParam );
    }

    /**
     * The 64-bit mask of video motion detection channels to search in.
     */
    public long getVmdMask()
    {
        return builder.get( vmdMaskParam );
    }

    /**
     * The 32-bit mask of GPS event types to search for.
     */
    public int getGpsMask()
    {
        return builder.get( gpsMaskParam );
    }

    /**
     * The 32-bit mask of system event types.
     */
    public int getSysMask()
    {
        return builder.get( sysMaskParam );
    }

    /**
     * Parses a URL (or the query part of a URL) to obtain an EventsCGI holding
     * the values obtained from the URL.
     */
    public static EventsCGI fromString( final String string )
    {
        if ( string.equals( "" ) )
        {
            throw new IllegalArgumentException();
        }

        return new EventsCGI( GenericBuilder.fromURL( string, params ) );
    }

    private static String urlEncode( final String unencoded )
    {
        try
        {
            return URLEncoder.encode( unencoded, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Returns /events.cgi? followed by all the parameters specified in this
     * EventsCGI.
     */
    @Override
    public String toString()
    {
        return "/events.cgi?format=" + getFormat()
                + withIntParam( getTime(), 0, "time" )
                + withIntParam( getRange(), Integer.MAX_VALUE, "range" )
                + withIntParam( getMaxLength(), 100, "listlength" )
                + with( getText(), "", "text" )
                + withLongHex( getCamMask(), 0, "cammask" )
                + withIntHex( getAlarmMask(), 0, "almmask" )
                + withLongHex( getVmdMask(), 0, "vmdmask" )
                + withIntHex( getGpsMask(), 0, "gpsmask" )
                + withIntHex( getSysMask(), 0, "sysmask" );
    }

    private String urlParam( final String name, final String value )
    {
        return '&' + urlEncode( name ) + '=' + urlEncode( value );
    }

    private String withIntParam( final int value, final int defaultValue,
            final String name )
    {
        return value == defaultValue ? "" : urlParam( name,
                String.valueOf( value ) );
    }

    private String with( final String value, final String defaultValue,
            final String name )
    {
        return value.equals( defaultValue ) ? "" : urlParam( name, value );
    }

    private String withIntHex( final int value, final int defaultValue,
            final String name )
    {
        return value == defaultValue ? "" : urlParam( name,
                Integer.toHexString( value ) );
    }

    private String withLongHex( final long value, final long defaultValue,
            final String name )
    {
        return value == defaultValue ? "" : urlParam( name,
                Long.toHexString( value ) );
    }

    /**
     * Tests the given parameter to see whether it is an equal EventsCGI to this
     * one.
     * 
     * @return true if object is an equal EventsCGI to this one, false
     *         otherwise.
     */
    @Override
    public boolean equals( final Object object )
    {
        if ( object instanceof EventsCGI )
        {
            final EventsCGI other = (EventsCGI) object;
            final EqualsBuilder builder = new EqualsBuilder().with(
                    getAlarmMask(), other.getAlarmMask(), "almmask" );
            builder.with( getFormat(), other.getFormat(), "format" );
            builder.with( getCamMask(), other.getCamMask(), "cammask" );
            builder.with( getGpsMask(), other.getGpsMask(), "gpsmask" );
            builder.with( getMaxLength(), other.getMaxLength(), "listlength" );
            builder.with( getRange(), other.getRange(), "range" );
            builder.with( getSysMask(), other.getSysMask(), "sysmask" );
            builder.with( getText(), other.getText(), "text" );
            builder.with( getTime(), other.getTime(), "time" );
            builder.with( getVmdMask(), other.getVmdMask(), "vmdmask" );

            return builder.equal();
        }

        return false;
    }

    /**
     * Returns a hashcode that is consistent with the equals implementation.
     */
    @Override
    public int hashCode()
    {
        return 1;
    }
}
