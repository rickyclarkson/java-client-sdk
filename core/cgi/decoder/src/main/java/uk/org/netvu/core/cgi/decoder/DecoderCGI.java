package uk.org.netvu.core.cgi.decoder;

import static uk.org.netvu.core.cgi.common.Parameter.param;
import static uk.org.netvu.core.cgi.common.Parameter.sparseArrayParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Iterables;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Pair;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.Reduction;
import uk.org.netvu.core.cgi.common.URLBuilder;

public class DecoderCGI
{
    private static final Parameter<Persistence, Persistence> persistenceParam = param(
            "persistence",
            "Whether to make the changes persistent (survive reboot) or temporary",
            Persistence.TEMPORARY, Persistence.fromString );

    private static final Parameter<Pair<Integer, Connection>, TreeMap<Integer, Connection>> connectionsParam = sparseArrayParam(
            "connections",
            "The differences to make to the connections system variable",
            Connection.urlEncode );

    private static final Parameter<Pair<Integer, Layout>, TreeMap<Integer, Layout>> layoutsParam = sparseArrayParam(
            "layouts",
            "The differences to make to the layouts system variable",
            Layout.urlEncode );

    private static final Parameter<String[], Option<String[]>> outputTitlesParam = param(
            "output_titles", "The titles to give to each output channel",
            Conversion.<String, String[]> throwUnsupportedOperationException(),
            new Conversion<String[], String>()
            {
                @Override
                public String convert( final String[] array )
                {
                    return Iterables.reduceLeft( Arrays.asList( array ),
                            new Reduction<String, String>()
                            {
                                @Override
                                public String reduce( final String value,
                                        final String accumulator )
                                {
                                    return accumulator + "," + value;
                                }
                            } );
                }
            } );

    private static final Parameter<Pair<Integer, String>, TreeMap<Integer, String>> commandsParam = sparseArrayParam(
            "commands",
            "The differences to make to the commands system variable",
            new Conversion<String, Conversion<URLBuilder, URLBuilder>>()
            {
                @Override
                public Conversion<URLBuilder, URLBuilder> convert(
                        final String s )
                {
                    return new Conversion<URLBuilder, URLBuilder>()
                    {
                        @Override
                        public URLBuilder convert( final URLBuilder builder )
                        {
                            return builder.literal( "\"" ).encoded( s ).literal(
                                    "\"" );
                        }
                    };
                }
            } );

    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( connectionsParam );
            add( layoutsParam );
            add( outputTitlesParam );
            add( commandsParam );
        }
    };

    private final GenericBuilder builder;

    public DecoderCGI()
    {
        this( new GenericBuilder() );
    }

    private DecoderCGI( final GenericBuilder builder )
    {
        this.builder = builder;
    }

    public DecoderCGI persistence( final Persistence persistence )
    {
        return new DecoderCGI( builder.with( persistenceParam, persistence ) );
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

    public String toURLParameters()
    {
        return builder.toURLParameters( params );
    }
}