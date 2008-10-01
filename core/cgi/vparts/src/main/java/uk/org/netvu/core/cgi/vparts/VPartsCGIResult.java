package uk.org.netvu.core.cgi.vparts;

import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.parameter;

import java.util.ArrayList;

import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.TwoWayConversion;
import uk.org.netvu.core.cgi.common.URLParameter;

/**
 * Builds and parses the results from vparts.cgi requests.
 */
public class VPartsCGIResult
{
    private static final Parameter<Integer, Option<Integer>> INDEX = parameter( "index", TwoWayConversion.integer );
    private static final Parameter<String, Option<String>> DIRECTORY = parameter( "directory", TwoWayConversion.string );
    private static final Parameter<String, Option<String>> FILENAME = parameter( "filename", TwoWayConversion.string );
    private static final Parameter<Integer, Option<Integer>> START_TIME = notNegative( parameter( "start_time", TwoWayConversion.integer ) );
    private static final Parameter<Integer, Option<Integer>> END_TIME = notNegative( parameter( "end_time", TwoWayConversion.integer ) );
    private static final Parameter<Integer, Option<Integer>> EXPIRY_TIME = notNegative( parameter( "expiry_time", TwoWayConversion.integer ) );
    private static final Parameter<Integer, Option<Integer>> NUMBER_OF_ENTRIES = parameter( "n_entries", TwoWayConversion.integer );
    private static final Parameter<Integer, Option<Integer>> CAM_MASK = parameter( "cammask", TwoWayConversion.integer );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final ArrayList<Parameter<?, ? extends Option<?>>> params = new ArrayList<Parameter<?, ? extends Option<?>>>()
    {
        {
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

    private final ParameterMap builtMap;

    private VPartsCGIResult( final ParameterMap parameterMap )
    {
        for ( final Parameter<?, ? extends Option<?>> param : params )
        {
            if ( parameterMap.get( param ).isNone() )
            {
                throw new IllegalStateException( "The parameter " + param.name
                        + " has not been given a value" );
            }
        }

        builtMap = parameterMap;
    }

    /**
     * A Builder for constructing VPartsCGIResults.
     */
    public static final class Builder
    {
        private ParameterMap parameterMap = new ParameterMap();

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
         * Sets the directory that the video partition can be found in.
         * 
         * @param directory
         *        the directory that the video partition can be found in.
         * @return the Builder.
         */
        public Builder directory( final String directory )
        {
            parameterMap = parameterMap.set( DIRECTORY, directory );
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
            parameterMap = parameterMap.set( FILENAME, filename );
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

        /**
         * Sets the end time for the video partition.
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
         *        overwritten)..
         * @return the Builder.
         */
        public Builder expiryTime( final int expiryTime )
        {
            parameterMap = parameterMap.set( EXPIRY_TIME, expiryTime );
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
         * Builds a VPartsCGIResult from the stored values.
         * 
         * @return a VPartsCGIResult containing the stored values.
         */
        public VPartsCGIResult build()
        {
            return new VPartsCGIResult( parameterMap );
        }
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
     * Gets the directory that the video partition can be found in.
     * 
     * @return the directory that the video partition can be found in.
     */
    public String getDirectory()
    {
        return builtMap.get( DIRECTORY ).get();
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
     * Gets the start time for the video partition.
     * 
     * @return the start time for the video partition.
     */
    public int getStartTime()
    {
        return builtMap.get( START_TIME ).get();
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
     * Gets the number of entries that the video partition contains.
     * 
     * @return the number of entries that the video partition contains.
     */
    public int getNumberOfEntries()
    {
        return builtMap.get( NUMBER_OF_ENTRIES ).get();
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
            result.append( builtMap.get( param ).get() ).append( ", " );
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
        ParameterMap parameterMap = new ParameterMap();
        final String[] elements = Strings.split( csv );
        int a = 0;
        for ( final Parameter<?, ?> param : params )
        {
            parameterMap = hack( parameterMap, param, elements[a] );
            a++;
        }
        return new VPartsCGIResult( parameterMap );
    }

    private static <T, R> ParameterMap hack( final ParameterMap parameterMap,
            final Parameter<T, R> param, final String s )
    {
        return parameterMap.set( param, param.fromURLParameter(
                new URLParameter( param.name, s ) ).get() );
    }
}
