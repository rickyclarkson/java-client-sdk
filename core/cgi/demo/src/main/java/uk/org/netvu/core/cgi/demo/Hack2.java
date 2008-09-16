/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Pair;

final class Hack2
        extends Conversion<Pair<String, Integer>, String>
{
    @Override
    public String convert( final Pair<String, Integer> pair )
    {
        return pair.second() + " -> " + pair.first();
    }
}
