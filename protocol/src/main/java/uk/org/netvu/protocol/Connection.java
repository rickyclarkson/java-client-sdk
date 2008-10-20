package uk.org.netvu.core.cgi.decoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Function;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.ParameterDescription;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.StringConversion;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.URLEncoder;
import uk.org.netvu.core.cgi.common.ParameterMap.Validator;

/**
 * A Connection tells the decoder which camera to use, and what parameter values
 * to send to it.
 */
public final class Connection
{
    private static final ParameterDescription<String, Option<String>> SLAVE_IP_PARAM =
            ParameterDescription.parameterWithoutDefault( "slaveip", StringConversion.string() );
    private static final ParameterDescription<Integer, Option<Integer>> SEQ_PARAM =
            ParameterDescription.parameterWithoutDefault( "seq", StringConversion.getHexToIntStringConversion() );
    private static final ParameterDescription<Integer, Option<Integer>> DWELL_PARAM =
            ParameterDescription.parameterWithoutDefault( "dwell", StringConversion.integer() );
    private static final ParameterDescription<Integer, Option<Integer>> CAM =
            ParameterDescription.parameterWithoutDefault( "cam", StringConversion.integer() );
    private static final ParameterDescription<Integer, Option<Integer>> AUDIO_CHANNEL_PARAM =
            ParameterDescription.parameterWithoutDefault( "audio", StringConversion.integer() );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<ParameterDescription<?, ? extends Option<?>>> params =
            new ArrayList<ParameterDescription<?, ? extends Option<?>>>()
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
     * A Function that, given a Connection, produces a URL-encoded String
     * containing a representation of it as URL parameters.
     */
    static final Function<Connection, String> urlEncode = new Function<Connection, String>()
    {
        @Override
        public String apply( final Connection connection )
        {
            return new URLEncoder().apply( connection.parameterMap.toURLParameters( params ).replaceAll( "&", "," ) );
        }
    }.andThen( Strings.surroundWithQuotes() );

    /**
     * A Function that, given a String containing URL parameters, produces a
     * Connection from it.
     */
    static final Function<String, Connection> fromURL = new Function<String, Connection>()
    {
        @Override
        public Connection apply( final String urlParameters )
        {
            try
            {
                final Option<ParameterMap> map =
                        ParameterMap.fromURL( URLDecoder.decode( urlParameters, "UTF-8" ).replaceAll( ",", "&" ),
                                params );

                if ( map.isEmpty() )
                {
                    throw new IllegalArgumentException( "Cannot parse " + urlParameters
                            + " into a Connection, because " + map.reason() );
                }

                return new Connection( map.get() );
            }
            catch ( final UnsupportedEncodingException e )
            {
                throw new RuntimeException( e );
            }
        }
    };

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
                return parameterMap.isDefault( CAM ) ? true : parameterMap.isDefault( SEQ_PARAM )
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
     * Connection, but with audio set to the parameter passed in.
     * 
     * @param audioChannel
     *        the source audio channel.
     * @return the new Connection.
     */
    public Connection audio( final int audioChannel )
    {
        return new Connection( parameterMap.set( AUDIO_CHANNEL_PARAM, audioChannel ) );
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
     * Returns the time to dwell on each camera in the seq bitmask, or throws an
     * IllegalStateException if this has not been set.
     * 
     * @return the time to dwell on each camera in the seq bitmask.
     */
    public int getDwell()
    {
        return parameterMap.get( DWELL_PARAM ).get();
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
}