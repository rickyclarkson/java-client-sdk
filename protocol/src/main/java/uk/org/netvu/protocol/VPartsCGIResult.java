package uk.org.netvu.protocol;

import java.util.ArrayList;

import uk.org.netvu.util.CheckParameters;

/**
 * Builds and parses the results from vparts.cgi requests.
 */
public class VPartsCGIResult
{
    private static final ParameterDescription<Integer, Option<Integer>> INDEX =
            ParameterDescription.parameterWithoutDefault( "index", StringConversion.integer() );
    private static final ParameterDescription<String, Option<String>> DIRECTORY =
            ParameterDescription.parameterWithoutDefault( "directory", StringConversion.string() );
    private static final ParameterDescription<String, Option<String>> FILENAME =
            ParameterDescription.parameterWithoutDefault( "filename", StringConversion.string() );
    private static final ParameterDescription<Integer, Option<Integer>> START_TIME =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithoutDefault( "start_time",
                    StringConversion.integer() ) );
    private static final ParameterDescription<Integer, Option<Integer>> END_TIME =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithoutDefault( "end_time",
                    StringConversion.integer() ) );
    private static final ParameterDescription<Integer, Option<Integer>> EXPIRY_TIME =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithoutDefault( "expiry_time",
                    StringConversion.integer() ) );
    private static final ParameterDescription<Integer, Option<Integer>> NUMBER_OF_ENTRIES =
            ParameterDescription.parameterWithoutDefault( "n_entries", StringConversion.integer() );
    private static final ParameterDescription<Integer, Option<Integer>> CAM_MASK =
            ParameterDescription.parameterWithoutDefault( "cammask", StringConversion.integer() );

    private static final ArrayList<ParameterDescription<?, ? extends Option<?>>> parameterDescriptions =
            new ArrayList<ParameterDescription<?, ? extends Option<?>>>()
            {
                {
                    // this is an anonymous intialiser - it is creating a new
                    // ArrayList and adding values to it inline.
                    add( INDEX );
                    add( DIRECTORY );
                    add( FILENAME );
                    add( START_TIME );
                    add( END_TIME );
                    add( EXPIRY_TIME );
                    add( NUMBER_OF_ENTRIES );
                    add( CAM_MASK );
                }
            };

    /**
     * Parses comma separated values as specified in the Video Server
     * Specification, producing a VPartsCGIResult.
     * 
     * @param csv
     *        the comma separated values to parse.
     * @throws NullPointerException
     *         if csv is null.
     * @return a VPartsCGIResult containing the values from the comma separated
     *         values.
     */
    public static VPartsCGIResult fromCSV( final String csv )
    {
        CheckParameters.areNotNull( csv );
        ParameterMap parameterMap = new ParameterMap();
        final String[] elements = Strings.splitCSV( csv );
        int a = 0;
        for ( final ParameterDescription<?, ?> parameterDescription : parameterDescriptions )
        {
            parameterMap = setFromString( parameterMap, parameterDescription, elements[a] );
            a++;
        }
        return new VPartsCGIResult( parameterMap );
    }

    /**
     * Sets a parameter to a given value after parsing it from a String. This
     * exists as a separate method because of type parameter inference
     * limitations.
     * 
     * @param <T>
     *        the input type of the parameter.
     * @param <R>
     *        the output type of the parameter.
     * @param parameterMap
     *        the ParameterMap to apply the value to.
     * @param param
     *        the ParameterDescription describing the parameter.
     * @param s
     *        the String to parse.
     * @throws NullPointerException
     *         if parameterMap, param or s are null.
     * @return a new ParameterMap containing the specified value, plus all the
     *         other values from the specified ParameterMap.
     */
    private static <T, R> ParameterMap setFromString( final ParameterMap parameterMap,
            final ParameterDescription<T, R> param, final String s )
    {
        return parameterMap.set( param, param.fromURLParameter( new URLParameter( param.name, s ) ).get() );
    }

    private final ParameterMap builtMap;

    /**
     * Constructs a VPartsCGIResult using the specified ParameterMap's values.
     * 
     * @param parameterMap
     *        a ParameterMap to get values from.
     */
    private VPartsCGIResult( final ParameterMap parameterMap )
    {
        for ( final ParameterDescription<?, ? extends Option<?>> parameterDescription : parameterDescriptions )
        {
            if ( parameterMap.get( parameterDescription ).isEmpty() )
            {
                throw new IllegalStateException( "The parameter " + parameterDescription.name
                        + " has not been given a value" );
            }
        }

        builtMap = parameterMap;

        if ( getEndTime() < getStartTime() )
        {
            throw new IllegalStateException( "The end time ( " + getEndTime() + " ) is less than the start time ( "
                    + getStartTime() + " )." );
        }
    }

    /**
     * Gets the mask of cameras that the video partition was recorded from.
     * 
     * @return the mask of cameras that the video partition was recorded from.
     */
    public int getCamMask()
    {
        return builtMap.get( CAM_MASK ).get();
    }

    /**
     * Gets the directory that the video partition can be found in.
     * 
     * @return the directory that the video partition can be found in.
     */
    public String getDirectory()
    {
        return builtMap.get( DIRECTORY ).get();
    }

    /**
     * Gets the end time for the video partition.
     * 
     * @return the end time for the video partition.
     */
    public int getEndTime()
    {
        return builtMap.get( END_TIME ).get();
    }

    /**
     * Gets the time at which the video partition expires (can be overwritten).
     * 
     * @return the time at which the video partition expires (can be
     *         overwritten).
     */
    public int getExpiryTime()
    {
        return builtMap.get( EXPIRY_TIME ).get();
    }

    /**
     * Gets the filename for the video partition.
     * 
     * @return the filename for the video partition.
     */
    public String getFilename()
    {
        return builtMap.get( FILENAME ).get();
    }

    /**
     * Gets the index of the result in the result set.
     * 
     * @return the index of the result in the result set.
     */
    public int getIndex()
    {
        return builtMap.get( INDEX ).get();
    }

    /**
     * Gets the number of entries that the video partition contains.
     * 
     * @return the number of entries that the video partition contains.
     */
    public int getNumberOfEntries()
    {
        return builtMap.get( NUMBER_OF_ENTRIES ).get();
    }

    /**
     * Gets the start time for the video partition.
     * 
     * @return the start time for the video partition.
     */
    public int getStartTime()
    {
        return builtMap.get( START_TIME ).get();
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

        for ( final ParameterDescription<?, ? extends Option<?>> parameterDescription : parameterDescriptions )
        {
            result.append( builtMap.get( parameterDescription ).get() ).append( ", " );
        }

        return result.substring( 0, result.length() - 2 );
    }

    /**
     * A Builder for constructing VPartsCGIResults.
     */
    public static final class Builder
    {
        private ParameterMap parameterMap = new ParameterMap();

        /**
         * Builds a VPartsCGIResult from the stored values.
         * 
         * @return a VPartsCGIResult containing the stored values.
         */
        public VPartsCGIResult build()
        {
            return new VPartsCGIResult( parameterMap );
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
            parameterMap = parameterMap.set( CAM_MASK, camMask );
            return this;
        }

        /**
         * Sets the directory that the video partition can be found in.
         * 
         * @param directory
         *        the directory that the video partition can be found in.
         * @throws NullPointerException
         *         if directory is null.
         * @return the Builder.
         */
        public Builder directory( final String directory )
        {
            parameterMap = parameterMap.set( DIRECTORY, directory );
            return this;
        }

        /**
         * Sets the end time for the video partition. The end time cannot be
         * less than the start time. If it is, then build() will fail with an
         * IllegalStateException.
         * 
         * @param endTime
         *        the end time for the video partition.
         * @return the Builder.
         */
        public Builder endTime( final int endTime )
        {
            parameterMap = parameterMap.set( END_TIME, endTime );
            return this;
        }

        /**
         * Sets the time at which the video partition expires (can be
         * overwritten).
         * 
         * @param expiryTime
         *        the time at which the video partition expires (can be
         *        overwritten).
         * @return the Builder.
         */
        public Builder expiryTime( final int expiryTime )
        {
            parameterMap = parameterMap.set( EXPIRY_TIME, expiryTime );
            return this;
        }

        /**
         * Sets the filename for the video partition.
         * 
         * @param filename
         *        the filename for the video partition.
         * @throws NullPointerException
         *         if filename is null.
         * @return the Builder.
         */
        public Builder filename( final String filename )
        {
            parameterMap = parameterMap.set( FILENAME, filename );
            return this;
        }

        /**
         * Sets the index of the result in the result set.
         * 
         * @param index
         *        the index of the result in the result set.
         * @return the Builder.
         */
        public Builder index( final int index )
        {
            parameterMap = parameterMap.set( INDEX, index );
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
            parameterMap = parameterMap.set( NUMBER_OF_ENTRIES, numberOfEntries );
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
            parameterMap = parameterMap.set( START_TIME, startTime );
            return this;
        }
    }
}
