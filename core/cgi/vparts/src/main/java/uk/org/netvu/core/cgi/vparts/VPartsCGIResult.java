package uk.org.netvu.core.cgi.vparts;

import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.UInt31;

public class VPartsCGIResult
{
    private static final Parameter<Integer> indexParam = param( "index",
            "The index of this result in the results", Conversion.stringToInt );
    private static final Parameter<String> directoryParam = param( "directory",
            "The directory where this video can be found",
            Conversion.<String> identity() );
    private static final Parameter<String> filenameParam = param( "filename",
            "The name of the file where this video can be found",
            Conversion.<String> identity() );
    private static final Parameter<UInt31> startTimeParam = param(
            "start_time", "The start time", UInt31.fromString );
    private static final Parameter<UInt31> endTimeParam = param( "end_time",
            "The end time", UInt31.fromString );
    private static final Parameter<UInt31> expiryTimeParam = param(
            "expiry_time", "The expiry time", UInt31.fromString );
    private static final Parameter<Integer> numberOfEntriesParam = param(
            "n_entries", "The number of entries", Conversion.stringToInt );
    private static final Parameter<Integer> camMaskParam = param( "cammask",
            "The bitmask of cameras that this video comes from",
            Conversion.stringToInt );

    private static final ArrayList<Parameter<?>> params = new ArrayList<Parameter<?>>()
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

    public static final class Builder
    {
        GenericBuilder real = new GenericBuilder();

        public Builder index( final int index )
        {
            real = real.with( indexParam, index );
            return this;
        }

        public Builder directory( final String directory )
        {
            real = real.with( directoryParam, directory );
            return this;
        }

        public Builder filename( final String filename )
        {
            real = real.with( filenameParam, filename );
            return this;
        }

        public Builder startTime( final int startTime )
        {
            real = real.with( startTimeParam, new UInt31( startTime ) );
            return this;
        }

        public Builder endTime( final int endTime )
        {
            real = real.with( endTimeParam, new UInt31( endTime ) );
            return this;
        }

        public Builder expiryTime( final int expiryTime )
        {
            real = real.with( expiryTimeParam, new UInt31( expiryTime ) );
            return this;
        }

        public Builder numberOfEntries( final int numberOfEntries )
        {
            real = real.with( numberOfEntriesParam, numberOfEntries );
            return this;
        }

        public Builder camMask( final int camMask )
        {
            real = real.with( camMaskParam, camMask );
            return this;
        }

        public VPartsCGIResult build()
        {
            for ( final Parameter<?> param : params )
            {
                if ( !real.isSet( param ) )
                {
                    throw new IllegalStateException( "The parameter "
                            + param.name + " has not been given a value" );
                }
            }

            return new VPartsCGIResult( real );
        }
    }

    public int getIndex()
    {
        return builder.get( indexParam );
    }

    public String getDirectory()
    {
        return builder.get( directoryParam );
    }

    public String getFilename()
    {
        return builder.get( filenameParam );
    }

    public int getStartTime()
    {
        return builder.get( startTimeParam ).toInt();
    }

    public int getEndTime()
    {
        return builder.get( endTimeParam ).toInt();
    }

    public int getExpiryTime()
    {
        return builder.get( expiryTimeParam ).toInt();
    }

    public int getNumberOfEntries()
    {
        return builder.get( numberOfEntriesParam );
    }

    public int getCamMask()
    {
        return builder.get( camMaskParam );
    }

    public String toCSV()
    {
        final StringBuilder result = new StringBuilder();

        for ( final Parameter<?> param : params )
        {
            result.append( builder.get( param ) ).append( ", " );
        }

        return result.substring( 0, result.length() - 2 );
    }

    public static VPartsCGIResult fromCSV( final String csv )
    {
        GenericBuilder builder = new GenericBuilder();
        final String[] elements = Strings.split( csv );
        int a = 0;
        for ( final Parameter<?> param : params )
        {
            builder = hack( builder, param, elements[a] );
            a++;
        }
        return new VPartsCGIResult( builder );
    }

    private static <T> GenericBuilder hack( final GenericBuilder builder,
            final Parameter<T> param, final String s )
    {
        return builder.with( param, param.fromString.convert( s ) );
    }
}
