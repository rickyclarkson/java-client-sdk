/**
 * 
 */
package uk.org.netvu.core.cgi.demo;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Pair;

final class Hack1
        extends Conversion<Pair<String, Integer>, Boolean>
{
    @Override
    public Boolean convert( final Pair<String, Integer> valueAndIndex )
    {
        return valueAndIndex.first().length() != 0;
    }
}
