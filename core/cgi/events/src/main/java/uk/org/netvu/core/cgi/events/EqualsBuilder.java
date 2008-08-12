package uk.org.netvu.core.cgi.events;

import java.util.ArrayList;
import java.util.List;

class EqualsBuilder
{
    public static class Rejection
    {
        public final Object one;
        public final Object two;
        public final String description;

        public <T, U extends T> Rejection( final T one, final U two,
                final String description )
        {
            this.one = one;
            this.two = two;
            this.description = description;
        }
    }

    private final List<Rejection> rejections = new ArrayList<Rejection>();

    public <T, U extends T> EqualsBuilder with( final T t, final U u,
            final String description )
    {
        if ( !t.equals( u ) )
        {
            rejections.add( new Rejection( t, u, description ) );
        }

        return this;
    }

    public boolean equal()
    {
        return rejections.isEmpty();
    }

    public String rejections()
    {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for ( final Rejection rejection : rejections )
        {
            builder.append( rejection.description ).append( ": " ).append(
                    rejection.one ).append( " != " ).append( rejection.two );

            if ( first )
            {
                first = false;
                builder.append( '\n' );
            }
        }
        return builder.toString();
    }
}
