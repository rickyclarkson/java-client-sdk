package uk.org.netvu.core.cgi.decoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import uk.org.netvu.core.cgi.common.Conversion;

public enum Layout
{
    SINGLE, FOUR_WAY, NINE_WAY, SIXTEEN_WAY;

    public static final Conversion<Layout, String> urlEncode = new Conversion<Layout, String>()
    {
        @Override
        public String convert( final Layout layout )
        {
            return layout.toString().toLowerCase().replaceAll( "_", " " );
        }
    };

    public static final Conversion<String, Layout> fromURL = new Conversion<String, Layout>()
    {
        @Override
        public Layout convert( final String url )
        {
            try
            {
                return Layout.valueOf( URLDecoder.decode( url.toUpperCase(),
                        "UTF-8" ).replaceAll( " ", "_" ) );
            }
            catch ( final UnsupportedEncodingException e )
            {
                throw new RuntimeException( e );
            }
        }
    };
}
