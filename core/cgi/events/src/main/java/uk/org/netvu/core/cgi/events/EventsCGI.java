package uk.org.netvu.core.cgi.events;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * A parameter list for an events.cgi query, checked for correctness.
 */
public final class EventsCGI
{
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
        private final MaxOnce<UInt31> time = new MaxOnce<UInt31>(
                new UInt31( 0 ), "time" );

        private final MaxOnce<UInt31> range = new MaxOnce<UInt31>( new UInt31(
                Integer.MAX_VALUE ), "range" );

        private Format format = Format.JS;

        private final MaxOnce<Integer> maxLength = new MaxOnce<Integer>( 100,
                "maxLength" );

        private final MaxOnce<String> text = new MaxOnce<String>( "", "text" );

        private final MaxOnce<Long> camMask = new MaxOnce<Long>( 0L, "camMask" );

        private final MaxOnce<Integer> alarmMask = new MaxOnce<Integer>( 0,
                "alarmMask" );

        private final MaxOnce<Long> vmdMask = new MaxOnce<Long>( 0L, "vmdMask" );

        private final MaxOnce<Integer> gpsMask = new MaxOnce<Integer>( 0,
                "gpsMask" );

        private final MaxOnce<Integer> sysMask = new MaxOnce<Integer>( 0,
                "sysMask" );

        /**
         * The time from which to search, in seconds since 1970.
         * 
         * @return the builder.
         */
        public Builder time( final int time )
        {
            this.time.set( new UInt31( time ) );
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
            this.range.set( new UInt31( range ) );
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
            this.maxLength.set( maxLength );
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
            alarmMask.isUnset();
            vmdMask.isUnset();
            gpsMask.isUnset();
            sysMask.isUnset();
            this.text.set( text );
            return this;
        }

        /**
         * The 64-bit mask of cameras whose images we want to obtain.
         * 
         * @return the builder.
         */
        public Builder camMask( final long camMask )
        {
            this.camMask.set( camMask );
            return this;
        }

        /**
         * The 32-bit mask of the alarms that we are interested in.
         * 
         * @return the builder.
         */
        public Builder alarmMask( final int alarmMask )
        {
            text.isUnset();
            vmdMask.isUnset();
            gpsMask.isUnset();
            sysMask.isUnset();
            this.alarmMask.set( alarmMask );
            return this;
        }

        /**
         * The 64-bit mask of video motion detection channels to search in.
         * 
         * @return the builder.
         */
        public Builder vmdMask( final long vmdMask )
        {
            text.isUnset();
            alarmMask.isUnset();
            gpsMask.isUnset();
            sysMask.isUnset();
            this.vmdMask.set( vmdMask );
            return this;
        }

        /**
         * The 32-bit mask of GPS event types to search for.
         * 
         * @return the builder.
         */
        public Builder gpsMask( final int gpsMask )
        {
            text.isUnset();
            alarmMask.isUnset();
            vmdMask.isUnset();
            sysMask.isUnset();
            this.gpsMask.set( gpsMask );
            return this;
        }

        /**
         * The 32-bit mask of system event types.
         * 
         * @return the builder.
         */
        public Builder sysMask( final int sysMask )
        {
            text.isUnset();
            alarmMask.isUnset();
            vmdMask.isUnset();
            gpsMask.isUnset();
            this.sysMask.set( sysMask );
            return this;
        }

        /**
         * Constructs an EventsCGI with the values from this builder.
         */
        public EventsCGI build()
        {
            return new EventsCGI( this );
        }

        /**
         * The format of the results.
         * 
         * @return the builder.
         */
        public Builder format( final Format format )
        {
            this.format = format;
            return this;
        }
    }

    /**
     * Copies the values from the builder to the constructed EventsCGI.
     */
    private EventsCGI( final Builder builder )
    {
        time = builder.time.get();
        range = builder.range.get();
        format = builder.format;
        maxLength = builder.maxLength.get();
        text = builder.text.get();
        camMask = builder.camMask.get();
        alarmMask = builder.alarmMask.get();
        vmdMask = builder.vmdMask.get();
        gpsMask = builder.gpsMask.get();
        sysMask = builder.sysMask.get();
    }

    private final UInt31 time;
    private final UInt31 range;
    private final Format format;
    private final Integer maxLength;
    private final String text;
    private final long camMask;
    private final int alarmMask;
    private final long vmdMask;
    private final int gpsMask;
    private final int sysMask;

    /**
     * The time from which to search, in seconds since 1970.
     */
    public int getTime()
    {
        return time.toInt();
    }

    /**
     * The timespan to search, in seconds.
     */
    public int getRange()
    {
        return range.toInt();
    }

    /**
     * The format of the results.
     */
    public Format getFormat()
    {
        return format;
    }

    /**
     * The maximum number of results to obtain. Negative values reverse the
     * direction of the search.
     */
    public int getMaxLength()
    {
        return maxLength;
    }

    /**
     * The text to search for, in the text-in-image data.
     */
    public String getText()
    {
        return text;
    }

    /**
     * The 64-bit mask of cameras whose images we want to obtain.
     */
    public long getCamMask()
    {
        return camMask;
    }

    /**
     * The 32-bit mask of the alarms that we are interested in.
     */
    public int getAlarmMask()
    {
        return alarmMask;
    }

    /**
     * The 64-bit mask of video motion detection channels to search in.
     */
    public long getVmdMask()
    {
        return vmdMask;
    }

    /**
     * The 32-bit mask of GPS event types to search for.
     */
    public int getGpsMask()
    {
        return gpsMask;
    }

    /**
     * The 32-bit mask of system event types.
     */
    public int getSysMask()
    {
        return sysMask;
    }

    static String fromLast( final char c, final String string )
    {
        return string.contains( String.valueOf( c ) ) ? string.substring( string.lastIndexOf( c ) + 1 )
                : string;
    }

    /**
     * Parses a URL (or the query part of a URL) to obtain an EventsCGI holding
     * the values obtained from the URL.
     */
    public static EventsCGI fromString( final String string )
    {
        try
        {
            final Builder builder = new EventsCGI.Builder();

            final String[] split = fromLast( '?',
                    URLDecoder.decode( string, "UTF-8" ) ).split( "&" );

            final Conversion<String, Integer> parseInt = new Conversion<String, Integer>()
            {
                public Integer convert( final String string )
                {
                    return Integer.parseInt( string );
                }
            };

            final Conversion<String, UInt31> parseUInt31 = new Conversion<String, UInt31>()
            {
                public UInt31 convert( final String s )
                {
                    return new UInt31( Integer.parseInt( s ) );
                }
            };

            final Conversion<String, Long> parseHexLong = new Conversion<String, Long>()
            {
                public Long convert( final String s )
                {
                    return new BigInteger( s, 16 ).longValue();
                }
            };

            final Conversion<String, Integer> parseHexInt = new Conversion<String, Integer>()
            {
                public Integer convert( final String s )
                {
                    return (int) Long.parseLong( s, 16 );
                }
            };

            final Conversion<String, Format> parseFormat = new Conversion<String, Format>()
            {
                public Format convert( final String s )
                {
                    return Format.valueOf( s );
                }
            };

            final Map<String, Action<String>> strategy = new HashMap<String, Action<String>>();

            strategy.put( "time", join( builder.time.setter(), parseUInt31 ) );
            strategy.put( "range", join( builder.range.setter(), parseUInt31 ) );
            strategy.put( "format", join( new Action<Format>()
            {

                public void invoke( final Format format )
                {
                    builder.format( format );
                }
            }, parseFormat ) );

            strategy.put( "listlength", join( builder.maxLength.setter(),
                    parseInt ) );
            strategy.put( "text", builder.text.setter() );
            strategy.put( "cammask", join( builder.camMask.setter(),
                    parseHexLong ) );
            strategy.put( "almmask", join( builder.alarmMask.setter(),
                    parseHexInt ) );
            strategy.put( "vmdmask", join( builder.vmdMask.setter(),
                    parseHexLong ) );
            strategy.put( "gpsmask", join( builder.gpsMask.setter(),
                    parseHexInt ) );
            strategy.put( "sysmask", join( builder.sysMask.setter(),
                    parseHexInt ) );

            for ( final String param : split )
            {
                final String[] parts = param.split( "=" );
                strategy.get( parts[0] ).invoke( parts[1] );
            }

            return builder.build();
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }

    }

    private static <T, U> Action<U> join( final Action<T> action,
            final Conversion<U, T> conversion )
    {
        return new Action<U>()
        {
            public void invoke( final U u )
            {
                action.invoke( conversion.convert( u ) );
            }

        };
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
