package uk.org.netvu.core.cgi.decoder;

import static uk.org.netvu.core.cgi.common.Option.someRef;
import static uk.org.netvu.core.cgi.common.Parameter.sparseArrayParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Pair;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.Reduction;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.URLBuilder;

/**
 * DecoderCGI is used for building CGI requests to send to decoders, using
 * decoder.frm or decoder.var, and for parsing those requests from URLs.
 */
public final class DecoderCGI
{
    private static final Parameter<Persistence, Persistence> PERSISTENCE = Parameter.param(
            "persistence",
            "Whether to make the changes persistent (survive reboot) or temporary",
            Persistence.TEMPORARY, Option.<String, Persistence> noneRef() );

    private static final Parameter<List<Pair<Integer, Connection>>, TreeMap<Integer, Connection>> CONNECTIONS = sparseArrayParam(
            "connections",
            "The differences to make to the connections system variable",
            someRef( Connection.fromURL ), someRef( Connection.urlEncode ) );

    private static final Parameter<List<Pair<Integer, Layout>>, TreeMap<Integer, Layout>> LAYOUTS = sparseArrayParam(
            "layouts",
            "The differences to make to the layouts system variable",
            someRef( Layout.fromURL ), someRef( Layout.urlEncode ) );

    private static final Parameter<String[], Option<String[]>> OUTPUT_TITLES = Parameter.param1(
            "output_titles", "The titles to give to each output channel",
            Option.<String, String[]> noneRef(),
            someRef( new Conversion<String[], String>()
            {
                @Override
                public String convert( final String[] array )
                {
                    return Lists.reduce( Lists.map( Arrays.asList( array ),
                            Strings.surroundWithQuotes ),
                            Reduction.intersperseWith( "," ) );
                }
            } ) );

    private static final Parameter<List<Pair<Integer, String>>, TreeMap<Integer, String>> COMMANDS = sparseArrayParam(
            "commands",
            "The differences to make to the commands system variable",
            someRef( Conversion.<String> identity() ),
            someRef( URLBuilder.encode.andThen( Strings.surroundWithQuotes ) ) );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>()
    {
        {
            add( CONNECTIONS );
            add( LAYOUTS );
            add( OUTPUT_TITLES );
            add( COMMANDS );
        }
    };

    private final ParameterMap parameterMap;

    /**
     * Constructs a DecoderCGI with no values set.
     */
    public DecoderCGI()
    {
        this( new ParameterMap() );
    }

    private DecoderCGI( final ParameterMap parameterMap )
    {
        this.parameterMap = parameterMap;
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * persistence parameter to the passed-in value.
     * 
     * @param persistence
     *        the value to use.
     * @return a new DecoderCGI.
     */
    public DecoderCGI persistence( final Persistence persistence )
    {
        return new DecoderCGI( parameterMap.with( PERSISTENCE, persistence ) );
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * indexed connection set to the passed in Connection.
     * 
     * @param index
     *        the index of the new Connection.
     * @param connection
     *        the Connection to store.
     * @return a new DecoderCGI.
     */
    public DecoderCGI connection( final int index, final Connection connection )
    {
        return new DecoderCGI( parameterMap.with( CONNECTIONS,
                Collections.singletonList( Pair.pair( index, connection ) ) ) );
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * indexed layout set to the passed in Layout.
     * 
     * @param index
     *        the index of the new Layout.
     * @param layout
     *        the Layout to store.
     * @return a new DecoderCGI.
     */
    public DecoderCGI layout( final int index, final Layout layout )
    {
        return new DecoderCGI( parameterMap.with( LAYOUTS,
                Collections.singletonList( Pair.pair( index, layout ) ) ) );
    }

    /**
     * Constructs a DecoderCGI containing the output titles passed in.
     * 
     * @param titles
     *        the output titles to use.
     * @return a new DecoderCGI.
     */
    public DecoderCGI outputTitles( final String... titles )
    {
        return new DecoderCGI( parameterMap.with( OUTPUT_TITLES, titles ) );
    }

    /**
     * Constructs a DecoderCGI containing the current values, but with the
     * indexed command set to the passed-in command.
     * 
     * @param index
     *        the index of the new command.
     * @param command
     *        the command to store.
     * @return a new DecoderCGI.
     */
    public DecoderCGI command( final int index, final String command )
    {
        return new DecoderCGI( parameterMap.with( COMMANDS,
                Collections.singletonList( Pair.pair( index, command ) ) ) );
    }

    /**
     * Gets the commands that have been set for this DecoderCGI.
     * 
     * @return the commands that have been set for this DecoderCGI.
     */
    public Map<Integer, String> getCommands()
    {
        return parameterMap.get( COMMANDS );
    }

    /**
     * Gets the layouts that have been set for this DecoderCGI.
     * 
     * @return the layouts that have been set for this DecoderCGI.
     */
    public Map<Integer, Layout> getLayouts()
    {
        return parameterMap.get( LAYOUTS );
    }

    /**
     * Gets the output titles that have been set for this DecoderCGI.
     * 
     * @return the output titles that have been set for this DecoderCGI.
     */
    public String[] getOutputTitles()
    {
        return parameterMap.get( OUTPUT_TITLES ).get();
    }

    /**
     * Gets the connections that have been set for this DecoderCGI.
     * 
     * @return the connections that have been set for this DecoderCGI.
     */
    public TreeMap<Integer, Connection> getConnections()
    {
        return parameterMap.get( CONNECTIONS );
    }

    /**
     * Produces part of a URL that can be passed to a decoder, beginning with
     * decoder.frm or decoder.var.
     * 
     * @return part of a URL that can be passed to a decoder.
     */
    public String toURLParameters()
    {
        return "decoder" + getPersistence() + '?'
                + parameterMap.toURLParameters( params );
    }

    /**
     * @return the persistence mode for this DecoderCGI.
     */
    public Persistence getPersistence()
    {
        return parameterMap.get( PERSISTENCE );
    }

    /**
     * Parses a URL, or part of a URL, into a DecoderCGI. If it cannot be
     * determined whether it should be a persistent (.frm) or a temporary (.var)
     * request, it will default to .var.
     * 
     * @param string
     *        the URL to parse.
     * @return a DecoderCGI.
     */
    public static DecoderCGI fromString( final String string )
    {
        return new DecoderCGI( ParameterMap.fromURL( string, params ) ).persistence( string.contains( ".frm" ) ? Persistence.PERSISTENT
                : Persistence.TEMPORARY );
    }
}
