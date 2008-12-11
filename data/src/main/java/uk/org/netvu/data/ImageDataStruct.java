package uk.org.netvu.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import uk.org.netvu.util.CheckParameters;

/**
 * A representation of the 'image data' part of a minimised-JFIF.
 */
public final class ImageDataStruct
{
    /**
     * The size of an int, in bytes.
     */
    private static final int INT = 4;

    /**
     * The size of a timestamp, in bytes.
     */
    private static final int TIME_SIZE = 4;

    /**
     * The length of the title (the title may be smaller than this and
     * null-terminated, but the number of bytes used is constant).
     */
    static final int TITLE_LENGTH = 31;

    /**
     * The length of the name (the name may be smaller than this and
     * null-terminated, but the number of bytes used is constant).
     */
    static final int MAX_NAME_LENGTH = 30;

    /**
     * The position of the VERSION field in the image data.
     */
    private static final int VERSION = 0;

    /**
     * The position of the MODE field in the image data.
     */
    private static final int MODE = VERSION + INT;

    /**
     * The position of the CAM field in the image data.
     */
    private static final int CAM = MODE + INT;

    /**
     * The position of the VID_FORMAT field in the image data.
     */
    private static final int VID_FORMAT = CAM + INT;

    /**
     * The position of the START_OFFSET field in the image data.
     */
    private static final int START_OFFSET = VID_FORMAT + INT;

    /**
     * The position of the SIZE field in the image data.
     */
    private static final int SIZE = START_OFFSET + INT;

    /**
     * The position of the MAX_SIZE field in the image data.
     */
    private static final int MAX_SIZE = SIZE + INT;

    /**
     * The position of the TARGET_SIZE field in the image data.
     */
    private static final int TARGET_SIZE = MAX_SIZE + INT;

    /**
     * The position of the Q_FACTOR field in the image data.
     */
    private static final int Q_FACTOR = TARGET_SIZE + INT;

    /**
     * The position of the ALM_BITMASK_HI field in the image data.
     */
    private static final int ALM_BITMASK_HI = Q_FACTOR + INT;

    /**
     * The position of the STATUS field in the image data.
     */
    private static final int STATUS = ALM_BITMASK_HI + INT;

    /**
     * The position of the SESSION_TIME field in the image data.
     */
    private static final int SESSION_TIME = STATUS + INT;

    /**
     * The position of the MILLISECONDS field in the image data.
     */
    private static final int MILLISECONDS = SESSION_TIME + TIME_SIZE;

    /**
     * The position of the RES field in the image data.
     */
    private static final int RES = MILLISECONDS + INT;

    /**
     * The position of the TITLE field in the image data.
     */
    private static final int TITLE = RES + 4;

    /**
     * The position of the ALARM field in the image data.
     */
    private static final int ALARM = TITLE + TITLE_LENGTH;

    /**
     * The position of the FORMAT field in the image data.
     */
    private static final int SRC_PIXELS = ALARM + TITLE_LENGTH;

    private static final int INT16 = 2;

    /**
     * The position of the SRC_LINES field in the picture struct.
     */
    private static final int SRC_LINES = SRC_PIXELS + INT16;

    /**
     * The position of the TARGET_PIXELS field in the picture struct.
     */
    private static final int TARGET_PIXELS = SRC_LINES + INT16;

    /**
     * The position of the TARGET_LINES field in the picture struct.
     */
    private static final int TARGET_LINES = TARGET_PIXELS + INT16;

    /**
     * The position of the PIXEL_OFFSET field in the picture struct.
     */
    private static final int PIXEL_OFFSET = TARGET_LINES + INT16;

    /**
     * The position of the LINE_OFFSET field in the picture struct.
     */
    private static final int LINE_OFFSET = PIXEL_OFFSET + INT16;

    /**
     * The position of the LOCALE field in the image data.
     */
    private static final int LOCALE = LINE_OFFSET + 2;

    /**
     * The position of the UTC_OFFSET field in the image data.
     */
    private static final int UTC_OFFSET = LOCALE + MAX_NAME_LENGTH;

    /**
     * The position of the ALM_BITMASK field in the image data.
     */
    private static final int ALM_BITMASK = UTC_OFFSET + INT;

    /**
     * The size of an image data block in bytes.
     */
    public static final int IMAGE_DATA_STRUCT_SIZE = ALM_BITMASK + INT;

    /**
     * Reads an ASCII String from the specified byte[], stopping when a 0 byte
     * is found or the end of the array is reached.
     * 
     * @param input
     *        the byte[] to read from.
     * @return an ASCII String.
     * @throws NullPointerException
     *         if input is null.
     */
    private static String nullTerminate( final byte[] input )
    {
        CheckParameters.areNotNull( input );
        try
        {
            return nullTerminate( new String( input, "US-ASCII" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Takes the part of the specified String before its first '\0', or the
     * whole String if none is found.
     * 
     * @param input
     *        the String to read from.
     * @return a String without '\0' characters.
     * @throws NullPointerException
     *         if input is null.
     */
    private static String nullTerminate( final String input )
    {
        CheckParameters.areNotNull( input );
        final int indexOfNull = input.indexOf( '\0' );
        return indexOfNull == -1 ? input : input.substring( 0, indexOfNull );
    }

    private final ByteBuffer buffer;

    /**
     * Constructs an ImageDataStruct with data from the specified ByteBuffer.
     * 
     * @param buffer
     *        the ByteBuffer to read data from.
     * @throws NullPointerException
     *         if buffer is null.
     */
    public ImageDataStruct( final ByteBuffer buffer )
    {
        CheckParameters.areNotNull( buffer );
        this.buffer = buffer.duplicate();
        this.buffer.position( 0 );
        final int version = IO.readInt( this.buffer, VERSION );

        if ( version != 0xDECADE10 && version != 0xDECADE11 )
        {
            throw new IllegalArgumentException( "version is " + Integer.toHexString( version ) );
        }
    }

    /**
     * Gets the alarm contained in the image data header.
     * 
     * @return the alarm contained in the image data header.
     */
    public String getAlarm()
    {
        return nullTerminate( read( buffer, TITLE_LENGTH, ALARM ) );
    }

    /**
     * Gets the alarmBitmask contained in the image data header.
     * 
     * @return the alarmBitmask contained in the image data header.
     */
    public int getAlarmBitmask()
    {
        return IO.readInt( buffer, ALM_BITMASK );
    }

    /**
     * Gets the alarmBitmaskHigh contained in the image data header.
     * 
     * @return the alarmBitmaskHigh contained in the image data header.
     */
    public int getAlarmBitmaskHigh()
    {
        return IO.readInt( buffer, ALM_BITMASK_HI );
    }

    public ByteBuffer getByteBuffer()
    {
        final ByteBuffer result = buffer.duplicate();
        result.position( 0 );
        return result;
    }

    /**
     * Gets the camera contained in the image data header.
     * 
     * @return the camera contained in the image data header.
     */
    public int getCamera()
    {
        return IO.readInt( buffer, CAM );
    }

    public short getLineOffset()
    {
        return readShort( LINE_OFFSET );
    }

    /**
     * Gets the locale contained in the image data header.
     * 
     * @return the locale contained in the image data header.
     */
    public String getLocale()
    {
        return nullTerminate( read( buffer, MAX_NAME_LENGTH, LOCALE ) );
    }

    /**
     * Gets the maxSize contained in the image data header.
     * 
     * @return the maxSize contained in the image data header.
     */
    public int getMaxSize()
    {
        return IO.readInt( buffer, MAX_SIZE );
    }

    /**
     * Gets the milliseconds contained in the image data header.
     * 
     * @return the milliseconds contained in the image data header.
     */
    public int getMilliseconds()
    {
        return (int) ( ( 0xFFFFFFFFL & IO.readInt( buffer, MILLISECONDS ) ) % 1000L );
    }

    /**
     * Gets the mode contained in the image data header.
     * 
     * @return the mode contained in the image data header.
     */
    public int getMode()
    {
        return IO.readInt( buffer, MODE );
    }

    public short getPixelOffset()
    {
        return readShort( PIXEL_OFFSET );
    }

    /**
     * Gets the qFactor contained in the image data header.
     * 
     * @return the qFactor contained in the image data header.
     */
    public int getQFactor()
    {
        return IO.readInt( buffer, Q_FACTOR );
    }

    /**
     * Gets the res contained in the image data header.
     * 
     * @return the res contained in the image data header.
     */
    public String getRes()
    {
        final String res = nullTerminate( read( buffer, 4, RES ) );
        return res.length() > 4 ? res.substring( 0, 4 ) : res;
    }

    /**
     * Gets the sessionTime contained in the image data header.
     * 
     * @return the sessionTime contained in the image data header.
     */
    public int getSessionTime()
    {
        return IO.readInt( buffer, SESSION_TIME );
    }

    /**
     * Gets the size contained in the image data header.
     * 
     * @return the size contained in the image data header.
     */
    public int getSize()
    {
        return IO.readInt( buffer, SIZE );
    }

    public short getSrcLines()
    {
        return readShort( SRC_LINES );
    }

    public short getSrcPixels()
    {
        return readShort( SRC_PIXELS );
    }

    /**
     * Gets the startOffset contained in the image data header.
     * 
     * @return the startOffset contained in the image data header.
     */
    public int getStartOffset()
    {
        return IO.readInt( buffer, START_OFFSET );
    }

    /**
     * Gets the status contained in the image data header.
     * 
     * @return the status contained in the image data header.
     */
    public int getStatus()
    {
        return IO.readInt( buffer, STATUS );
    }

    public short getTargetLines()
    {
        return readShort( TARGET_LINES );
    }

    public short getTargetPixels()
    {
        return readShort( TARGET_PIXELS );
    }

    /**
     * Gets the targetSize contained in the image data header.
     * 
     * @return the targetSize contained in the image data header.
     */
    public int getTargetSize()
    {
        return IO.readInt( buffer, TARGET_SIZE );
    }

    /**
     * Gets the title contained in the image data header.
     * 
     * @return the title contained in the image data header.
     */
    public String getTitle()
    {
        return nullTerminate( read( buffer, TITLE_LENGTH, TITLE ) );
    }

    /**
     * Gets the utcOffset contained in the image data header.
     * 
     * @return the utcOffset contained in the image data header.
     */
    public int getUtcOffset()
    {
        return IO.readInt( buffer, UTC_OFFSET );
    }

    /**
     * Gets the version of the image data header.
     * 
     * @return the version of the image data header.
     */
    public int getVersion()
    {
        return IO.readInt( buffer, VERSION );
    }

    /**
     * Gets the videoFormat contained in the image data header.
     * 
     * @return the videoFormat contained in the image data header.
     */
    public VideoFormat getVideoFormat()
    {
        return VideoFormat.valueOf( IO.readInt( buffer, VID_FORMAT ) );
    }

    public void setAlarm( final String s )
    {
        write( nullPad( s, TITLE_LENGTH ), ALARM );
    }

    public void setAlarmBitmask( final int alarmBitmask )
    {
        buffer.putInt( ALM_BITMASK, alarmBitmask );
    }

    public void setAlarmBitmaskHigh( final int alarmBitmaskHigh )
    {
        buffer.putInt( ALM_BITMASK_HI, alarmBitmaskHigh );
    }

    public void setCamera( final int camera )
    {
        buffer.putInt( CAM, camera );
    }

    public void setLineOffset( final short lineOffset )
    {
        writeShort( LINE_OFFSET, lineOffset );
    }

    public void setLocale( final String locale )
    {
        write( nullPad( locale, MAX_NAME_LENGTH ), LOCALE );
    }

    public void setMaxSize( final int maxSize )
    {
        buffer.putInt( MAX_SIZE, maxSize );
    }

    public void setMilliseconds( final int milliseconds )
    {
        buffer.putInt( MILLISECONDS, milliseconds );
    }

    public void setMode( final int mode )
    {
        buffer.putInt( MODE, mode );
    }

    public void setPixelOffset( final short pixelOffset )
    {
        writeShort( PIXEL_OFFSET, pixelOffset );
    }

    public void setQFactor( final int qFactor )
    {
        buffer.putInt( Q_FACTOR, qFactor );
    }

    public void setRes( final String res )
    {
        write( nullPad( res, 4 ), RES );
    }

    public void setSessionTime( final int sessionTime )
    {
        buffer.putInt( SESSION_TIME, sessionTime );
    }

    public void setSize( final int size )
    {
        buffer.putInt( SIZE, size );
    }

    public void setSrcLines( final short srcLines )
    {
        writeShort( SRC_LINES, srcLines );
    }

    public void setSrcPixels( final short srcPixels )
    {
        writeShort( SRC_PIXELS, srcPixels );
    }

    public void setStartOffset( final int startOffset )
    {
        buffer.putInt( START_OFFSET, startOffset );
    }

    public void setStatus( final int status )
    {
        buffer.putInt( STATUS, status );
    }

    public void setTargetLines( final short targetLines )
    {
        writeShort( TARGET_LINES, targetLines );
    }

    public void setTargetPixels( final short targetPixels )
    {
        writeShort( TARGET_PIXELS, targetPixels );
    }

    public void setTargetSize( final int targetSize )
    {
        buffer.putInt( TARGET_SIZE, targetSize );
    }

    public void setTitle( final String title )
    {
        write( nullPad( title, TITLE_LENGTH ), TITLE );
    }

    public void setUtcOffset( final int utcOffset )
    {
        buffer.putInt( UTC_OFFSET, utcOffset );
    }

    public void setVideoFormat( final VideoFormat format )
    {
        buffer.putInt( VID_FORMAT, format.index );
    }

    private byte[] nullPad( final String string, final int length )
    {
        try
        {
            final byte[] bytes = new byte[length];
            System.arraycopy( string.getBytes( "US-ASCII" ), 0, bytes, 0, string.length() );
            return bytes;
        }
        catch ( final UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Reads the specified number of bytes from the specified ByteBuffer, from
     * the specified position.
     * 
     * @param buffer
     *        the ByteBuffer to read data from.
     * @param length
     *        the number of bytes to read.
     * @param where
     *        the position in the ByteBuffer to read from.
     * @return an array of bytes containing the specified number of bytes read
     *         from the specified ByteBuffer, at the specified position.
     * @throws NullPointerException
     *         if buffer is null.
     */
    private byte[] read( final ByteBuffer buffer, final int length, final int where )
    {
        CheckParameters.areNotNull( buffer );
        final byte[] result = new byte[length];
        buffer.position( where );
        buffer.get( result );
        return result;
    }

    /**
     * Reads a little-endian short from the specified position within the
     * ByteBuffer relative to the offset.
     * 
     * @param where
     *        the position within the ByteBuffer relative to the offset to read
     *        from.
     * @return a short read from the ByteBuffer.
     */
    private short readShort( final int where )
    {
        buffer.order( versionToByteOrder() );
        try
        {
            return buffer.getShort( where );
        }
        finally
        {
            buffer.order( ByteOrder.BIG_ENDIAN );
        }

    }

    private ByteOrder versionToByteOrder()
    {
        return getVersion() == 0xDECADE10 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }

    private void write( final byte[] bytes, final int position )
    {
        buffer.position( position );
        buffer.put( bytes );
    }

    private void writeShort( final int position, final short value )
    {
        buffer.order( versionToByteOrder() );
        try
        {
            buffer.putShort( position, value );
        }
        finally
        {
            buffer.order( ByteOrder.BIG_ENDIAN );
        }
    }
}
