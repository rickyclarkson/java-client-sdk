package uk.org.netvu.core.cgi.vparts;

import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.URLParameter;

/**
 * Builds and parses the results from vparts.cgi requests.
 */
public class VPartsCGIResult
{
    private static final Parameter<Integer, Option<Integer>> indexParam = param(
            "index", "The index of this result in the results",
            Conversion.stringToInt );
    private static final Parameter<String, Option<String>> directoryParam = param(
            "directory", "The directory where this video can be found",
            Conversion.<String> identity() );
    private static final Parameter<String, Option<String>> filenameParam = param(
            "filename", "The name of the file where this video can be found",
            Conversion.<String> identity() );
    private static final Parameter<Integer, Option<Integer>> startTimeParam = notNegative( param(
            "start_time", "The start time", Conversion.stringToInt ) );
    private static final Parameter<Integer, Option<Integer>> endTimeParam = notNegative( param(
            "end_time", "The end time", Conversion.stringToInt ) );
    private static final Parameter<Integer, Option<Integer>> expiryTimeParam = notNegative( param(
            "expiry_time", "The expiry time", Conversion.stringToInt ) );
    private static final Parameter<Integer, Option<Integer>> numberOfEntriesParam = param(
            "n_entries", "The number of entries", Conversion.stringToInt );
    private static final Parameter<Integer, Option<Integer>> camMaskParam = param(
            "cammask", "The bitmask of cameras that this video comes from",
            Conversion.stringToInt );

    private static final ArrayList<Parameter<?, ? extends Option<?>>> params = new ArrayList<Parameter<?, ? extends Option<?>>>()
    {
        {
            add( indexParam );
            add( directoryParam );
            add( filenameParam );
            add( startTimeParam );
            add( endTimeParam );
            add( expiryTimeParam );
            add( numberOfEntriesParam );
            add( camMaskParam );
        }
    };

    private final GenericBuilder builder;

    private VPartsCGIResult( final GenericBuilder builder )
    {
        this.builder = builder;
    }

    /**
     * A Builder for constructing VPartsCGIResults.
     */
    public static final class Builder
    {
        GenericBuilder real = new GenericBuilder();

        /**
         * Sets the index of the result in the result set.
         * 
         * @param index
         *        the index of the result in the result set.
         * @return the Builder.
         */
        public Builder index( final int index )
        {
            real = real.with( indexParam, index );
            return this;
        }

        /**
         * Sets the directory that the video partition can be found in.
         * 
         * @param directory
         *        the directory that the video partition can be found in.
         * @return the Builder.
         */
        public Builder directory( final String directory )
        {
            real = real.with( directoryParam, directory );
            return this;
        }

        /**
         * Sets the filename for the video partition.
         * 
         * @param filename
         *        the filename for the video partition.
         * @return the Builder.
         */
        public Builder filename( final String filename )
        {
            real = real.with( filenameParam, filename );
            return this;
        }

        /**
         * Sets the start time for the video partition.
         * 
         * @param startTime
         *        the start time for the video partition.
         * @return the Builder.
         */
        public Builder startTime( final int startTime )
        {
            real = real.with( startTimeParam, startTime );
            return this;
        }

        /**
         * Sets the end time for the video partition.
         * 
         * @param endTime
         *        the end time for the video partition.
         * @return the Builder.
         */
        public Builder endTime( final int endTime )
        {
            real = real.with( endTimeParam, endTime );
            return this;
        }

        /**
         * Sets the time at which the video partition expires (can be
         * overwritten).
         * 
         * @param expiryTime
         *        the time at which the video partition expires (can be
         *        overwritten)..
         * @return the Builder.
         */
        public Builder expiryTime( final int expiryTime )
        {
            real = real.with( expiryTimeParam, expiryTime );
            return this;
        }

        /**
         * Sets the number of entries that the video partition contains.
         * 
         * @param numberOfEntries
         *        the number of entries that the video partition contains.
         * @return the Builder.
         */
        public Builder numberOfEntries( final int numberOfEntries )
        {
            real = real.with( numberOfEntriesParam, numberOfEntries );
            return this;
        }

        /**
         * Sets the mask of cameras that the video partition was recorded from.
         * 
         * @param camMask
         *        the mask of cameras that the video partition was recorded
         *        from.
         * @return the Builder.
         */
        public Builder camMask( final int camMask )
        {
            real = real.with( camMaskParam, camMask );
            return this;
        }

        /**
         * Builds a VPartsCGIResult from the stored values.
         * 
         * @return a VPartsCGIResult containing the stored values.
         */
        public VPartsCGIResult build()
        {
            for ( final Parameter<?, ? extends Option<?>> param : params )
            {

                if ( real.get( param ).isNone() )
                {
                    throw new IllegalStateException( "The parameter "
                            + param.getName() + " has not been given a value" );
                }
            }

            return new VPartsCGIResult( real );
        }
    }

    /**
     * Gets the index of the result in the result set.
     * 
     * @return the index of the result in the result set.
     */
    public int getIndex()
    {
        return builder.get( indexParam ).get();
    }

    /**
     * Gets the directory that the video partition can be found in.
     * 
     * @return the directory that the video partition can be found in.
     */
    public String getDirectory()
    {
        return builder.get( directoryParam ).get();
    }

    /**
     * Gets the filename for the video partition.
     * 
     * @return the filename for the video partition.
     */
    public String getFilename()
    {
        return builder.get( filenameParam ).get();
    }

    /**
     * Gets the start time for the video partition.
     * 
     * @return the start time for the video partition.
     */
    public int getStartTime()
    {
        return builder.get( startTimeParam ).get();
    }

    /**
     * Gets the end time for the video partition.
     * 
     * @return the end time for the video partition.
     */
    public int getEndTime()
    {
        return builder.get( endTimeParam ).get();
    }

    /**
     * Gets the time at which the video partition expires (can be overwritten).
     * 
     * @return the time at which the video partition expires (can be
     *         overwritten).
     */
    public int getExpiryTime()
    {
        return builder.get( expiryTimeParam ).get();
    }

    /**
     * Gets the number of entries that the video partition contains.
     * 
     * @return the number of entries that the video partition contains.
     */
    public int getNumberOfEntries()
    {
        return builder.get( numberOfEntriesParam ).get();
    }

    /**
     * Gets the mask of cameras that the video partition was recorded from.
     * 
     * @return the mask of cameras that the video partition was recorded from.
     */
    public int getCamMask()
    {
        return builder.get( camMaskParam ).get();
    }

    /**
     * Generates comma separated values using the stored values in the same
     * format as defined in the Video Server Specification.
     * 
     * @return comma separated values using the values stored in this
     *         VPartsCGIResult.
     */
    public String toCSV()
    {
        final StringBuilder result = new StringBuilder();

        for ( final Parameter<?, ? extends Option<?>> param : params )
        {
            result.append( builder.get( param ).get() ).append( ", " );
        }

        return result.substring( 0, result.length() - 2 );
    }

    /**
     * Parses comma separated values as specified in the Video Server
     * Specification, producing a VPartsCGIResult.
     * 
     * @param csv
     *        the comma separated values to parse.
     * @return a VPartsCGIResult containing the values from the comma separated
     *         values.
     */
    public static VPartsCGIResult fromCSV( final String csv )
    {
        GenericBuilder builder = new GenericBuilder();
        final String[] elements = Strings.split( csv );
        int a = 0;
        for ( final Parameter<?, ?> param : params )
        {
            builder = hack( builder, param, elements[a] );
            a++;
        }
        return new VPartsCGIResult( builder );
    }

    private static <T, R> GenericBuilder hack( final GenericBuilder builder,
            final Parameter<T, R> param, final String s )
    {
        return builder.with( param, param.fromURLParameter( new URLParameter(
                param.getName(), s ) ) );
    }
}
