package uk.org.netvu.core.cgi.vparts;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VPartsCGIResultTest
{
    @Test(expected = IllegalStateException.class)
    public void incomplete()
    {
        new VPartsCGIResult.Builder().build();
    }

    @Test
    public void success()
    {
        final VPartsCGIResult result = new VPartsCGIResult.Builder().index( 100 ).camMask(
                4 ).directory( "foo" ).endTime( 10 ).expiryTime( 100 ).filename(
                "bar" ).numberOfEntries( 5 ).startTime( 1 ).build();

        assertTrue( result.getIndex() == 100 );
        assertTrue( result.getCamMask() == 4 );
        assertTrue( result.getDirectory().equals( "foo" ) );
        assertTrue( result.getEndTime() == 10 );
        assertTrue( result.getExpiryTime() == 100 );
        assertTrue( result.getFilename().equals( "bar" ) );
        assertTrue( result.getNumberOfEntries() == 5 );
        assertTrue( result.getStartTime() == 1 );
    }

    @Test(expected = IllegalStateException.class)
    public void twice()
    {
        new VPartsCGIResult.Builder().camMask( 4 ).camMask( 4 );
    }

    @Test
    public void fromCSV()
    {
        final String csv = "2, C:\\VIDEO, VID02495.VID, 958038878, 958038962, 0, 519, 15";
        System.out.println( VPartsCGIResult.fromCSV( csv ).toCSV() );
        assertTrue( VPartsCGIResult.fromCSV( csv ).toCSV().equals( csv ) );
    }
}
