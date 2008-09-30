package uk.org.netvu.core.cgi.decoder;

import static uk.org.netvu.core.cgi.common.Parameter.parameter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.ReversibleReplace;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.TwoWayConversion;
import uk.org.netvu.core.cgi.common.URLBuilder;
import uk.org.netvu.core.cgi.common.Validator;

/**
 * A Connection tells the decoder which camera to use, and what parameter values
 * to send to it.
 */
public final class Connection
{
    private static final Parameter<String, Option<String>> SLAVE_IP_PARAM = parameter(
            "slaveip", "The source video server address",
            TwoWayConversion.string );
    private static final Parameter<Integer, Option<Integer>> SEQ_PARAM = parameter(
            "seq", "Bitmask of source cameras", TwoWayConversion.hexInt );
    private static final Parameter<Integer, Option<Integer>> DWELL_PARAM = parameter(
            "dwell", "The time to dwell on each camera in the seq bitmask",
            TwoWayConversion.integer );
    private static final Parameter<Integer, Option<Integer>> CAM = parameter(
            "cam", "The source camera", TwoWayConversion.integer );
    private static final Parameter<Integer, Option<Integer>> AUDIO_CHANNEL_PARAM = parameter(
            "audio", "The source audio channel", TwoWayConversion.integer );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<Parameter<?, ? extends Option<?>>> params = new ArrayList<Parameter<?, ? extends Option<?>>>()
    {
        {
            add( SLAVE_IP_PARAM );
            add( SEQ_PARAM );
            add( DWELL_PARAM );
            add( CAM );
            add( AUDIO_CHANNEL_PARAM );
        }
    };

    private final ParameterMap parameterMap;

    /**
     * Constructs a Connection.
     */
    public Connection()
    {
        this( new ParameterMap( new Validator()
        {
            @Override
            public boolean isValid( final ParameterMap parameterMap )
            {
                return parameterMap.isDefault( CAM ) ? true
                        : parameterMap.isDefault( SEQ_PARAM )
                                && parameterMap.isDefault( DWELL_PARAM );
            }
        } ) );
    }

    private Connection( final ParameterMap parameterMap )
    {
        this.parameterMap = parameterMap;
    }

    /**
     * Constructs a new Connection containing the same values as this
     * Connection, but with the slaveIP set to the parameter passed in.
     * 
     * @param slaveIP
     *        the IP address of the slave camera.
     * @return the new Connection.
     */
    public Connection slaveIP( final String slaveIP )
    {
        return new Connection( parameterMap.set( SLAVE_IP_PARAM, slaveIP ) );
    }

    /**
     * Constructs a new Connection containing the same values as this
     * Connection, but with seq set to the parameter passed in.
     * 
     * @param seq
     *        a bitmask of source cameras.
     * @return the new Connection.
     */
    public Connection seq( final int seq )
    {
        return new Connection( parameterMap.set( SEQ_PARAM, seq ) );
    }

    /**
     * Constructs a new Connection containing the same values as this
     * Connection, but with dwell set to the parameter passed in.
     * 
     * @param dwell
     *        the time to dwell on each camera in the seq bitmask.
     * @return the new Connection.
     */
    public Connection dwell( final int dwell )
    {
        return new Connection( parameterMap.set( DWELL_PARAM, dwell ) );
    }

    /**
     * Constructs a new Connection containing the same values as this
     * Connection, but with cam set to the parameter passed in.
     * 
     * @param cam
     *        the source camera.
     * @return the new Connection.
     */
    public Connection cam( final int cam )
    {
        return new Connection( parameterMap.set( CAM, cam ) );
    }

    /**
     * Constructs a new Connection containing the same values as this
     * Connection, but with audio set to the parameter passed in.
     * 
     * @param audioChannel
     *        the source audio channel.
     * @return the new Connection.
     */
    public Connection audio( final int audioChannel )
    {
        return new Connection( parameterMap.set( AUDIO_CHANNEL_PARAM,
                audioChannel ) );
    }

    private static final ReversibleReplace replacer = Strings.reversibleReplace(
            "&", "," );

    /**
     * A Conversion that, given a Connection, produces a URL-encoded String
     * containing a representation of it as URL parameters.
     */
    static final Conversion<Connection, String> urlEncode = new Conversion<Connection, String>()
    {
        @Override
        public String convert( final Connection connection )
        {
            return URLBuilder.encode( replacer.replace( connection.parameterMap.toURLParameters( params ) ) );
        }
    }.andThen( Strings.surroundWithQuotes );

    /**
     * A Conversion that, given a String containing URL parameters, produces a
     * Connection from it.
     */
    static final Conversion<String, Connection> fromURL = new Conversion<String, Connection>()
    {
        @Override
        public Connection convert( final String urlParameters )
        {
            try
            {
                Option<ParameterMap> map = ParameterMap.fromURL(
                        replacer.undo( URLDecoder.decode( urlParameters,
                                "UTF-8" ) ), params );

                if ( map.isNone() )
                    throw new IllegalArgumentException( "Cannot parse "
                            + urlParameters + " into a Connection, because "
                            + map.reason() );

                return new Connection( map.get() );
            }
            catch ( final UnsupportedEncodingException e )
            {
                throw new RuntimeException( e );
            }
        }
    };

    /**
     * Returns the source video server address, or throws an
     * IllegalStateException if none exists.
     * 
     * @return the source video server address.
     */
    public String getSlaveIP()
    {
        return parameterMap.get( SLAVE_IP_PARAM ).get();
    }

    /**
     * Returns the source camera, or throws an IllegalStateException if none
     * exists.
     * 
     * @return the source camera.
     */
    public int getCam()
    {
        return parameterMap.get( CAM ).get();
    }

    /**
     * Returns the source camera mask, or throws an IllegalStateException if
     * none exists.
     * 
     * @return the source camera mask.
     */
    public int getSeq()
    {
        return parameterMap.get( SEQ_PARAM ).get();
    }

    /**
     * Returns the time to dwell on each camera in the seq bitmask, or throws an
     * IllegalStateException if this has not been set.
     * 
     * @return the time to dwell on each camera in the seq bitmask.
     */
    public int getDwell()
    {
        return parameterMap.get( DWELL_PARAM ).get();
    }
}
