package uk.org.netvu.core.cgi.vparts;

import static uk.org.netvu.core.cgi.common.Parameter.bound;
import static uk.org.netvu.core.cgi.common.Parameter.not;
import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Format;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;

/**
 * Builds and parses vparts.cgi requests.
 */
public final class VPartsCGI
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

    private final ParameterMap parameterMap;

    private VPartsCGI( final ParameterMap parameterMap )
    {
        this.parameterMap = parameterMap;
    }

    /**
     * A Builder for constructing VPartsCGIs.
     */
    public static final class Builder
    {
        ParameterMap real = new ParameterMap();

        /**
         * Constructs a VPartsCGI containing the stored parameters.
         * 
         * @return a VPartsCGI containing the stored parameters.
         */
        public VPartsCGI build()
        {
            return new VPartsCGI( real );
        }

        /**
         * Sets the function of the cgi call.
         * 
         * @param mode
         *        the {@link Mode} to use.
         * @return the Builder.
         */
        public Builder mode( final Mode mode )
        {
            real = real.with( modeParam, mode );
            return this;
        }

        /**
         * Sets the Julianised GMT start time for database search.
         * 
         * @param time
         *        the start time for database search.
         * @return the Builder.
         */
        public Builder time( final int time )
        {
            real = real.with( timeParam, time );
            return this;
        }

        /**
         * Time span to search in seconds.
         * 
         * @param range
         *        the time span to search in seconds.
         * @return the Builder.
         */
        public Builder range( final int range )
        {
            real = real.with( rangeParam, range );
            return this;
        }

        // TODO decide whether to restrict expiry to a VPartsCGI with protect
        // set.

        /**
         * Sets the expiry time for partitions, in Julianised GMT (only used in
         * protect mode).
         * 
         * @param expiry
         *        the expiry time for partitions
         * @return the Builder.
         */
        public Builder expiry( final int expiry )
        {
            real = real.with( expiryParam, expiry );
            return this;
        }

        // TODO decide whether to restrict watermark to read mode.
        /**
         * Sets whether to generate watermark codes (read mode only).
         * 
         * @param watermark
         *        true if the server should generate watermark codes, false
         *        otherwise.
         * @return the Builder.
         */
        public Builder watermark( final boolean watermark )
        {
            real = real.with( watermarkParam, watermark );
            return this;
        }

        // TODO make this and the watermark parameter be mutually 'inclusive'.
        /**
         * defines the step size to be used in watermark code generation, used
         * in conjunction with the watermark parameter.
         * 
         * @param step
         *        the step size to be used in watermark code generation.
         * @return the Builder.
         */
        public Builder watermarkStep( final int step )
        {
            real = real.with( wmarkstepParam, step );
            return this;
        }

        /**
         * Determines the output format (defaults to CSV in this API, which is
         * different to the servers' defaults).
         * 
         * @param format
         *        the output format.
         * @return the Builder.
         */
        public Builder format( final Format format )
        {
            real = real.with( formatParam, format );
            return this;
        }

        /**
         * Determines the maximum number of elements in the list.
         * 
         * @param listlength
         *        the maximum number of elements in the list.
         * @return the Builder.
         */
        public Builder listlength( final int listlength )
        {
            real = real.with( listlengthParam, listlength );
            return this;
        }

        /**
         * Determines the format of directory paths, short or long.
         * 
         * @param style
         *        the format of directory paths.
         * @return the Builder.
         */
        public Builder pathstyle( final DirectoryPathFormat style )
        {
            real = real.with( pathstyleParam, style );
            return this;
        }
    }

    /**
     * The function of the cgi call.
     * 
     * @return the {@link Mode} that the CGI call is in.
     */
    public Mode getMode()
    {
        return parameterMap.get( modeParam );
    }

    /**
     * Julianised GMT time to start the search from.
     * 
     * @return the Julianised GMT time to start the search from.
     */
    public int getTime()
    {
        return parameterMap.get( timeParam );
    }

    /**
     * The timespan to search in seconds.
     * 
     * @return the timespan to search in seconds.
     */
    public int getRange()
    {
        return parameterMap.get( rangeParam );
    }

    /**
     * The expiry time for partitions, in Julianised GMT (only used in protect
     * mode).
     * 
     * @return the expiry time for partitions.
     */
    public int getExpiry()
    {
        return parameterMap.get( expiryParam );
    }

    /**
     * Whether or not to generate watermark codes (read mode only).
     * 
     * @return true if the server should generate watermark codes (read mode
     *         only), false otherwise.
     */
    public boolean getWatermark()
    {
        return parameterMap.get( watermarkParam );
    }

    /**
     * The step size to be used in watermark code generation.
     * 
     * @return the step size to be used in watermark code generation.
     */
    public int getWatermarkStep()
    {
        return parameterMap.get( wmarkstepParam );
    }

    /**
     * The output format.
     * 
     * @return the output format.
     */
    public Format getFormat()
    {
        return parameterMap.get( formatParam );
    }

    /**
     * The maximum number of elements in the list.
     * 
     * @return the maximum number of elements in the list.
     */
    public int getListlength()
    {
        return parameterMap.get( listlengthParam );
    }

    /**
     * The format of directory paths, short or long.
     * 
     * @return the format of directory paths, short or long.
     */
    public DirectoryPathFormat getPathstyle()
    {
        return parameterMap.get( pathstyleParam );
    }

    @Override
    public String toString()
    {
        return "/vparts.cgi?format="
                + parameterMap.get( formatParam )
                + "&"
                + parameterMap.toURLParameters( Lists.remove( params,
                        formatParam ) );
    }

    /**
     * Parses a vparts.cgi request into a VPartsCGI.
     * 
     * @param url
     *        the vparts request to parse.
     * @return a VPartsCGI containing the values from the URL.
     */
    public static VPartsCGI fromString( final String url )
    {
        return new VPartsCGI( ParameterMap.fromURL( url, params ) );
    }
}
