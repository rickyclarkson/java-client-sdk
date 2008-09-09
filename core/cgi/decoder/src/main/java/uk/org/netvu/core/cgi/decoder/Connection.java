package uk.org.netvu.core.cgi.decoder;

import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.URLBuilder;
import uk.org.netvu.core.cgi.common.Validator;

/**
 * A Connection tells the decoder which camera to use, and what parameter values
 * to send to it.
 */
public final class Connection
{
    private static final Parameter<String, Option<String>> slaveIPParam = param(
            "slaveip", "The source video server address",
            Conversion.<String> identity() );
    private static final Parameter<Integer, Option<Integer>> seqParam = param(
            "seq", "Bitmask of source cameras", Conversion.hexStringToInt );
    private static final Parameter<Integer, Option<Integer>> dwellParam = param(
            "dwell", "The time to dwell on each camera in the seq bitmask",
            Conversion.stringToInt );
    private static final Parameter<Integer, Option<Integer>> camParam = param(
            "cam", "The source camera", Conversion.stringToInt );
    private static final Parameter<Integer, Option<Integer>> audioChannelParam = param(
            "audio", "The source audio channel", Conversion.stringToInt );

    private static final List<Parameter<?, ? extends Option<?>>> params = new ArrayList<Parameter<?, ? extends Option<?>>>()
    {
        {
            add( slaveIPParam );
            add( seqParam );
            add( dwellParam );
            add( camParam );
            add( audioChannelParam );
        }
    };

    private final GenericBuilder builder;

    /**
     * Constructs a Connection.
     */
    public Connection()
    {
        this( new GenericBuilder( new Validator()
        {
            @Override
            public boolean isValid( final GenericBuilder builder )
            {
                return builder.isDefault( camParam ) ? true
                        : builder.isDefault( seqParam )
                                && builder.isDefault( dwellParam );
            }
        } ) );
    }

    private Connection( final GenericBuilder builder )
    {
        this.builder = builder;
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
        return new Connection( builder.with( slaveIPParam, slaveIP ) );
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
        return new Connection( builder.with( seqParam, seq ) );
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
        return new Connection( builder.with( dwellParam, dwell ) );
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
        return new Connection( builder.with( camParam, cam ) );
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
        return new Connection( builder.with( audioChannelParam, audioChannel ) );
    }

    static final Conversion<Connection, String> urlEncode = new Conversion<Connection, String>()
    {
        @Override
        public String convert( final Connection connection )
        {
            return "\""
                    + URLBuilder.encode( connection.builder.toURLParameters(
                            params ).replaceAll( "&", "," ) ) + "\"";
        }
    };

    static final Conversion<String, Connection> fromURL = new Conversion<String, Connection>()
    {
        @Override
        public Connection convert( final String urlParameters )
        {
            try
            {
                return new Connection( GenericBuilder.fromURL(
                        URLDecoder.decode( urlParameters, "UTF-8" ).replaceAll(
                                ",", "&" ), params ) );
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
        return builder.get( slaveIPParam ).get();
    }

    /**
     * Returns the source camera, or throws an IllegalStateException if none
     * exists.
     * 
     * @return the source camera.
     */
    public int getCam()
    {
        return builder.get( camParam ).get();
    }

    /**
     * Returns the source camera mask, or throws an IllegalStateException if
     * none exists.
     * 
     * @return the source camera mask.
     */
    public int getSeq()
    {
        return builder.get( seqParam ).get();
    }

    /**
     * Returns the time to dwell on each camera in the seq bitmask, or throws an
     * IllegalStateException if this has not been set.
     * 
     * @return the time to dwell on each camera in the seq bitmask.
     */
    public int getDwell()
    {
        return builder.get( dwellParam ).get();
    }
}
