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

    public Connection slaveIP( final String slaveIP )
    {
        return new Connection( builder.with( slaveIPParam, slaveIP ) );
    }

    public Connection seq( final int seq )
    {
        return new Connection( builder.with( seqParam, seq ) );
    }

    public Connection dwell( final int time )
    {
        return new Connection( builder.with( dwellParam, time ) );
    }

    public Connection cam( final int cam )
    {
        return new Connection( builder.with( camParam, cam ) );
    }

    public Connection audio( final int audioChannel )
    {
        return new Connection( builder.with( audioChannelParam, audioChannel ) );
    }

    public static final Conversion<Connection, String> urlEncode = new Conversion<Connection, String>()
    {
        @Override
        public String convert( final Connection connection )
        {
            return "\""
                    + URLBuilder.encode( connection.builder.toURLParameters(
                            params ).replaceAll( "&", "," ) ) + "\"";
        }
    };

    public static final Conversion<String, Connection> fromURL = new Conversion<String, Connection>()
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

    public String getSlaveIP()
    {
        return builder.get( slaveIPParam ).get();
    }

    public int getCam()
    {
        return builder.get( camParam ).get();
    }
}
