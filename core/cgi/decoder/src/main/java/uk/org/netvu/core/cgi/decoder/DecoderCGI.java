package uk.org.netvu.core.cgi.decoder;

import static uk.org.netvu.core.cgi.common.Parameter.param;
import static uk.org.netvu.core.cgi.common.Parameter.sparseArrayParam;

import java.util.List;
import java.util.Map;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Pair;
import uk.org.netvu.core.cgi.common.Parameter;

public class DecoderCGI
{
    private static final Parameter<Pair<Integer, Connection>, Map<Integer, Connection>> connectionsParam = sparseArrayParam(
            "connections",
            "The differences to make to the connections system variable" );

    private static final Parameter<Pair<Integer, Layout>, Map<Integer, Layout>> layoutsParam = sparseArrayParam(
            "layouts", "The differences to make to the layouts system variable" );

    private static final Parameter<String[], Option<String[]>> outputTitlesParam = param(
            "output_titles", "The titles to give to each output channel",
            Conversion.<String, String[]> throwUnsupportedOperationException() );

    private static final Parameter<Pair<Integer, String>, Map<Integer, String>> commandsParam = sparseArrayParam(
            "commands",
            "The differences to make to the commands system variable" );

    private final GenericBuilder builder;

    public DecoderCGI()
    {
        this( new GenericBuilder() );
    }

    private DecoderCGI( final GenericBuilder builder )
    {
        this.builder = builder;
    }

    public DecoderCGI connection( final int index, final Connection connection )
    {
        return new DecoderCGI( builder.with( connectionsParam, Pair.pair(
                index, connection ) ) );
    }

    public DecoderCGI layout( final int index, final Layout layout )
    {
        return new DecoderCGI( builder.with( layoutsParam, Pair.pair( index,
                layout ) ) );
    }

    public DecoderCGI outputTitles( final String... titles )
    {
        return new DecoderCGI( builder.with( outputTitlesParam, titles ) );
    }

    public DecoderCGI command( final int index, final String command )
    {
        return new DecoderCGI( builder.with( commandsParam, Pair.pair( index,
                command ) ) );
    }

    public static final class Connections
    {
        public final int startIndex;
        public final List<Connection> connections;

        public Connections( final int startIndex,
                final List<Connection> connections )
        {
            this.startIndex = startIndex;
            this.connections = connections;
        }
    }

    public static final class Connection
    {
        private static final Parameter<String, Option<String>> slaveIPParam = param(
                "slaveIP", "The source video server address",
                Conversion.<String> identity() );
        private static final Parameter<String, Option<String>> seqParam = param(
                "seq", "Bitmask of source cameras",
                Conversion.<String> identity() );
        private static final Parameter<Integer, Option<Integer>> dwellParam = param(
                "dwell", "The time to dwell on each camera in the seq bitmask",
                Conversion.stringToInt );
        private static final Parameter<Integer, Option<Integer>> camParam = param(
                "cam", "The source camera", Conversion.stringToInt );
        private static final Parameter<Integer, Option<Integer>> audioChannelParam = param(
                "audio", "The source audio channel", Conversion.stringToInt );

        private final GenericBuilder builder;

        public Connection()
        {
            this( new GenericBuilder() );
        }

        private Connection( final GenericBuilder builder )
        {
            this.builder = builder;
        }

        public Connection slaveIP( final String slaveIP )
        {
            return new Connection( builder.with( slaveIPParam, slaveIP ) );
        }

        public Connection seq( final String seq )
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
            return new Connection( builder.with( audioChannelParam,
                    audioChannel ) );
        }
    }

    public Map<Integer, String> getCommands()
    {
        return builder.get( commandsParam );
    }

    public Map<Integer, Layout> getLayouts()
    {
        return builder.get( layoutsParam );
    }

    public String[] getOutputTitles()
    {
        return builder.get( outputTitlesParam ).get();
    }
}
