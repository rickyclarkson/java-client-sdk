package uk.org.netvu.core.cgi.vparts;

import static uk.org.netvu.core.cgi.common.Parameter.bound;
import static uk.org.netvu.core.cgi.common.Parameter.not;
import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Iterables;
import uk.org.netvu.core.cgi.common.Parameter;

final class VPartsCGI
{
    private static final Parameter<Format, Format> formatParam = not(
            Format.HTML,
            param(
                    "format",
                    "Determines output format.  js is for JavaScript, csv for comma separated variables",
                    Format.CSV, Format.fromString ) );

    private static final Parameter<Mode, Mode> modeParam = param( "mode",
            "Determines the function of the cgi call", Mode.READ,
            Mode.fromString );

    private static final Parameter<Integer, Integer> timeParam = notNegative( param(
            "time", "Julianised GMT start time for database search", 0,
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> rangeParam = notNegative( param(
            "range", "Timespan to search in seconds.", Integer.MAX_VALUE,
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> expiryParam = param(
            "expiry",
            "Sets the expiry time for partitions, used in protect mode; Julianised GMT",
            0, Conversion.stringToInt );

    private static final Parameter<Boolean, Boolean> watermarkParam = param(
            "watermark",
            "Tells the CGI to generate watermark codes in read mode", false,
            Conversion.stringToBoolean );

    private static final Parameter<Integer, Integer> wmarkstepParam = bound( 1,
            256, param( "wmarkstepParam",
                    "Defines the step size to be used in watermark.", 1,
                    Conversion.stringToInt ) );

    private static final Parameter<Integer, Integer> listlengthParam = param(
            "listlength", "Maximum number of entries in the list", 100,
            Conversion.stringToInt );

    private static final Parameter<DirectoryPathFormat, DirectoryPathFormat> pathstyleParam = param(
            "pathstyle", "Format of directory paths.",
            DirectoryPathFormat.SHORT, DirectoryPathFormat.fromString );

    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( formatParam );
            add( modeParam );
            add( timeParam );
            add( rangeParam );
            add( expiryParam );
            add( watermarkParam );
            add( wmarkstepParam );
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
            real = real.with( timeParam, time );
            return this;
        }

        /**
         * Time span to search in seconds.
         */
        public Builder range( final int range )
        {
            real = real.with( rangeParam, range );
            return this;
        }

        public Builder expiry( final int expiry )
        {
            real = real.with( expiryParam, expiry );
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
        return builder.get( timeParam );
    }

    /**
     * The timespan to search in seconds.
     */
    public int getRange()
    {
        return builder.get( rangeParam );
    }

    public int getExpiry()
    {
        return builder.get( expiryParam );
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
        return "/vparts.cgi?format="
                + builder.get( formatParam )
                + "&"
                + builder.toURLParameters( Iterables.remove( params,
                        formatParam ) );
    }

    public static VPartsCGI fromString( final String url )
    {
        return new VPartsCGI( GenericBuilder.fromURL( url, params ) );
    }
}
