package uk.org.netvu.core.cgi.decoder;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.URLBuilder;

public enum Layout
{
    SINGLE, FOUR_WAY, NINE_WAY, SIXTEEN_WAY;

    public static final Conversion<Layout, Conversion<URLBuilder, URLBuilder>> urlEncode = new Conversion<Layout, Conversion<URLBuilder, URLBuilder>>()
    {
        @Override
        public Conversion<URLBuilder, URLBuilder> convert( final Layout layout )
        {
            return new Conversion<URLBuilder, URLBuilder>()
            {
                @Override
                public URLBuilder convert( final URLBuilder urlBuilder )
                {
                    return urlBuilder.encoded( layout.toString().toLowerCase().replaceAll(
                            "_", " " ) );
                }
            };
        }
    };
}
