package uk.org.netvu.core.cgi.vparts;

import static uk.org.netvu.core.cgi.common.Parameter.bound;
import static uk.org.netvu.core.cgi.common.Parameter.not;
import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.UInt31;
import uk.org.netvu.core.cgi.common.URLBuilder;

final class VPartsCGI
{
    private static final Parameter<Mode> modeParam = param( "mode",
            "Determines the function of the cgi call", Mode.READ,
            Mode.fromString );

    private static final Parameter<UInt31> timeParam = param( "time",
            "Julianised GMT start time for database search", new UInt31( 0 ),
            UInt31.fromString );

    private static final Parameter<UInt31> rangeParam = param( "range",
            "Timespan to search in seconds.", new UInt31( Integer.MAX_VALUE ),
            UInt31.fromString );

    private static final Parameter<UInt31> expiryParam = param(
            "expiry",
            "Sets the expiry time for partitions, used in protect mode; Julianised GMT",
            new UInt31( 0 ), UInt31.fromString );

    private static final Parameter<Boolean> watermarkParam = param(
            "watermark",
            "Tells the CGI to generate watermark codes in read mode", false,
            Conversion.stringToBoolean );

    private static final Parameter<Integer> wmarkstepParam = bound( 1, 256,
            param( "wmarkstepParam",
                    "Defines the step size to be used in watermark.", 1,
                    Conversion.stringToInt ) );

    private static final Parameter<Format> formatParam = not(
            Format.HTML,
            param(
                    "format",
                    "Determines output format.  js is for JavaScript, csv for comma separated variables",
                    Format.CSV, Format.fromString ) );

    private static final Parameter<Integer> listlengthParam = param(
            "listlength", "Maximum number of entries in the list", 100,
            Conversion.stringToInt );

    private static final Parameter<DirectoryPathFormat> pathstyleParam = param(
            "pathstyle", "Format of directory paths.",
            DirectoryPathFormat.SHORT, DirectoryPathFormat.fromString );

    private static final List<Parameter<?>> params = new ArrayList<Parameter<?>>()
    {
        {
            add( modeParam );
            add( timeParam );
            add( rangeParam );
            add( expiryParam );
            add( watermarkParam );
            add( wmarkstepParam );
            add( formatParam );
            add( listlengthParam );
            add( pathstyleParam );
        }
    };

    private final GenericBuilder builder;

    private VPartsCGI( final GenericBuilder builder )
    {
        this.builder = builder;
    }

    public static final class Builder
    {
        GenericBuilder real = new GenericBuilder();

        public VPartsCGI build()
        {
            return new VPartsCGI( real );
        }

        /**
         * Sets the function of the cgi call.
         */
        public Builder mode( final Mode mode )
        {
            real = real.with( modeParam, mode );
            return this;
        }

        /**
         * Sets the Julianised GMT start time for database search.
         */
        public Builder time( final int time )
        {
            real = real.with( timeParam, new UInt31( time ) );
            return this;
        }

        /**
         * Time span to search in seconds.
         */
        public Builder range( final int range )
        {
            real = real.with( rangeParam, new UInt31( range ) );
            return this;
        }

        public Builder expiry( final int expiry )
        {
            real = real.with( expiryParam, new UInt31( expiry ) );
            return this;
        }

        public Builder watermark( final boolean watermark )
        {
            real = real.with( watermarkParam, watermark );
            return this;
        }

        public Builder watermarkStep( final int step )
        {
            real = real.with( wmarkstepParam, step );
            return this;
        }

        public Builder format( final Format format )
        {
            real = real.with( formatParam, format );
            return this;
        }

        public Builder listlength( final int listlength )
        {
            real = real.with( listlengthParam, listlength );
            return this;
        }

        public Builder pathstyle( final DirectoryPathFormat style )
        {
            real = real.with( pathstyleParam, style );
            return this;
        }
    }

    /**
     * The function of the cgi call.
     */
    public Mode getMode()
    {
        return builder.get( modeParam );
    }

    /**
     * Julianised GMT time to start the search from.
     */
    public int getTime()
    {
        return builder.get( timeParam ).toInt();
    }

    /**
     * The timespan to search in seconds.
     */
    public int getRange()
    {
        return builder.get( rangeParam ).toInt();
    }

    public int getExpiry()
    {
        return builder.get( expiryParam ).toInt();
    }

    public boolean getWatermark()
    {
        return builder.get( watermarkParam );
    }

    public int getWatermarkStep()
    {
        return builder.get( wmarkstepParam );
    }

    public Format getFormat()
    {
        return builder.get( formatParam );
    }

    public int getListlength()
    {
        return builder.get( listlengthParam );
    }

    public DirectoryPathFormat getPathstyle()
    {
        return builder.get( pathstyleParam );
    }

    @Override
    public String toString()
    {
        return new URLBuilder( "/vparts.cgi?" ).withParam( formatParam.name,
                getFormat() ).withOptionalParam( modeParam.name, getMode(),
                modeParam.defaultValue.get() ).withOptionalParam(
                timeParam.name, new UInt31( getTime() ),
                timeParam.defaultValue.get() ).withOptionalParam(
                rangeParam.name, getRange(),
                rangeParam.defaultValue.get().toInt() ).withOptionalParam(
                expiryParam.name, getExpiry(),
                expiryParam.defaultValue.get().toInt() ).withOptionalParam(
                watermarkParam.name, getWatermark(),
                watermarkParam.defaultValue.get() ).withOptionalParam(
                wmarkstepParam.name, getWatermarkStep(),
                wmarkstepParam.defaultValue.get() ).withOptionalParam(
                listlengthParam.name, getListlength(),
                listlengthParam.defaultValue.get() ).withOptionalParam(
                pathstyleParam.name, getPathstyle(),
                pathstyleParam.defaultValue.get() ).toString();
    }

    public static VPartsCGI fromString( final String url )
    {
        GenericBuilder builder = new GenericBuilder();

        final String[] parts = Strings.fromLast( '?', url ).split( "&" );
        for ( final String part : parts )
        {
            final String[] both = part.split( "=" );
            for ( final Parameter<?> param : params )
            {
                if ( param.name.equals( both[0] ) )
                {
                    builder = builder.withFromString( param, both[1] );
                }
            }
        }

        return new VPartsCGI( builder );
    }
}
