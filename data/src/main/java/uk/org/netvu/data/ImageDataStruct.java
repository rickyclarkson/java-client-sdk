package uk.org.netvu.data;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;

import uk.org.netvu.util.CheckParameters;

/**
 * A representation of the 'image data' part of a minimised-JFIF.
 */
public final class ImageDataStruct
{
    /**
     * The size of an int, in bytes.
     */
    private static final int INT_SIZE = 4;

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
    private static final int VERSION_OFFSET = 0;

    /**
     * The position of the MODE field in the image data.
     */
    private static final int MODE_OFFSET = VERSION_OFFSET + INT_SIZE;

    /**
     * The position of the CAM field in the image data.
     */
    private static final int CAM_OFFSET = MODE_OFFSET + INT_SIZE;

    /**
     * The position of the VID_FORMAT field in the image data.
     */
    private static final int VID_FORMAT_OFFSET = CAM_OFFSET + INT_SIZE;

    /**
     * The position of the START_OFFSET field in the image data.
     */
    private static final int START_OFFSET = VID_FORMAT_OFFSET + INT_SIZE;

    /**
     * The position of the SIZE field in the image data.
     */
    private static final int SIZE_OFFSET = START_OFFSET + INT_SIZE;

    /**
     * The position of the MAX_SIZE field in the image data.
     */
    private static final int MAX_SIZE_OFFSET = SIZE_OFFSET + INT_SIZE;

    /**
     * The position of the TARGET_SIZE field in the image data.
     */
    private static final int TARGET_SIZE = MAX_SIZE_OFFSET + INT_SIZE;

    /**
     * The position of the Q_FACTOR field in the image data.
     */
    private static final int Q_FACTOR_OFFSET = TARGET_SIZE + INT_SIZE;

    /**
     * The position of the ALM_BITMASK_HI field in the image data.
     */
    private static final int ALM_BITMASK_HI_OFFSET = Q_FACTOR_OFFSET + INT_SIZE;

    /**
     * The position of the STATUS field in the image data.
     */
    private static final int STATUS_OFFSET = ALM_BITMASK_HI_OFFSET + INT_SIZE;

    /**
     * The position of the SESSION_TIME field in the image data.
     */
    private static final int SESSION_TIME_OFFSET = STATUS_OFFSET + INT_SIZE;

    /**
     * The position of the MILLISECONDS field in the image data.
     */
    private static final int MILLISECONDS_OFFSET = SESSION_TIME_OFFSET + TIME_SIZE;

    /**
     * The position of the RES field in the image data.
     */
    private static final int RES_OFFSET = MILLISECONDS_OFFSET + INT_SIZE;

    /**
     * The position of the TITLE field in the image data.
     */
    private static final int TITLE_OFFSET = RES_OFFSET + 4;

    /**
     * The position of the ALARM field in the image data.
     */
    private static final int ALARM_OFFSET = TITLE_OFFSET + TITLE_LENGTH;

    /**
     * The position of the FORMAT field in the image data.
     */
    private static final int SRC_PIXELS_OFFSET = ALARM_OFFSET + TITLE_LENGTH;

    private static final int SHORT_SIZE = 2;

    /**
     * The position of the SRC_LINES field in the picture struct.
     */
    private static final int SRC_LINES_OFFSET = SRC_PIXELS_OFFSET + SHORT_SIZE;

    /**
     * The position of the TARGET_PIXELS field in the picture struct.
     */
    private static final int TARGET_PIXELS_OFFSET = SRC_LINES_OFFSET + SHORT_SIZE;

    /**
     * The position of the TARGET_LINES field in the picture struct.
     */
    private static final int TARGET_LINES = TARGET_PIXELS_OFFSET + SHORT_SIZE;

    /**
     * The position of the PIXEL_OFFSET field in the picture struct.
     */
    private static final int PIXEL_OFFSET = TARGET_LINES + SHORT_SIZE;

    /**
     * The position of the LINE_OFFSET field in the picture struct.
     */
    private static final int LINE_OFFSET = PIXEL_OFFSET + SHORT_SIZE;

    /**
     * The position of the LOCALE field in the image data.
     */
    private static final int LOCALE_OFFSET = LINE_OFFSET + 2;

    /**
     * The position of the UTC_OFFSET field in the image data.
     */
    private static final int UTC_OFFSET_OFFSET = LOCALE_OFFSET + MAX_NAME_LENGTH;

    /**
     * The position of the ALM_BITMASK field in the image data.
     */
    private static final int ALM_BITMASK = UTC_OFFSET_OFFSET + INT_SIZE;

    /**
     * The size of an image data block in bytes.
     */
    public static final int IMAGE_DATA_STRUCT_SIZE = ALM_BITMASK + INT_SIZE;

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
        return nullTerminate( IO.bytesToString( input ) );
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
        final int version = IO.readInt( this.buffer, VERSION_OFFSET );

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
        return nullTerminate( read( buffer, TITLE_LENGTH, ALARM_OFFSET ) );
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
        return IO.readInt( buffer, ALM_BITMASK_HI_OFFSET );
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
        return IO.readInt( buffer, CAM_OFFSET );
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
        return nullTerminate( read( buffer, MAX_NAME_LENGTH, LOCALE_OFFSET ) );
    }

    /**
     * Gets the maxSize contained in the image data header.
     * 
     * @return the maxSize contained in the image data header.
     */
    public int getMaxSize()
    {
        return IO.readInt( buffer, MAX_SIZE_OFFSET );
    }

    /**
     * Gets the milliseconds contained in the image data header.
     * 
     * @return the milliseconds contained in the image data header.
     */
    public int getMilliseconds()
    {
        return (int) ( ( 0xFFFFFFFFL & IO.readInt( buffer, MILLISECONDS_OFFSET ) ) % 1000L );
    }

    /**
     * Gets the mode contained in the image data header.
     * 
     * @return the mode contained in the image data header.
     */
    public int getMode()
    {
        return IO.readInt( buffer, MODE_OFFSET );
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
        return IO.readInt( buffer, Q_FACTOR_OFFSET );
    }

    /**
     * Gets the res contained in the image data header.
     * 
     * @return the res contained in the image data header.
     */
    public String getRes()
    {
        final String res = nullTerminate( read( buffer, 4, RES_OFFSET ) );
        return res.length() > 4 ? res.substring( 0, 4 ) : res;
    }

    /**
     * Gets the sessionTime contained in the image data header.
     * 
     * @return the sessionTime contained in the image data header.
     */
    public int getSessionTime()
    {
        return IO.readInt( buffer, SESSION_TIME_OFFSET );
    }

    /**
     * Gets the size contained in the image data header.
     * 
     * @return the size contained in the image data header.
     */
    public int getSize()
    {
        return IO.readInt( buffer, SIZE_OFFSET );
    }

    public short getSrcLines()
    {
        return readShort( SRC_LINES_OFFSET );
    }

    public short getSrcPixels()
    {
        return readShort( SRC_PIXELS_OFFSET );
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
        return IO.readInt( buffer, STATUS_OFFSET );
    }

    public short getTargetLines()
    {
        return readShort( TARGET_LINES );
    }

    public short getTargetPixels()
    {
        return readShort( TARGET_PIXELS_OFFSET );
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
        return nullTerminate( read( buffer, TITLE_LENGTH, TITLE_OFFSET ) );
    }

    /**
     * Gets the utcOffset contained in the image data header.
     * 
     * @return the utcOffset contained in the image data header.
     */
    public int getUtcOffset()
    {
        return IO.readInt( buffer, UTC_OFFSET_OFFSET );
    }

    /**
     * Gets the version of the image data header.
     * 
     * @return the version of the image data header.
     */
    public int getVersion()
    {
        return IO.readInt( buffer, VERSION_OFFSET );
    }

    /**
     * Gets the videoFormat contained in the image data header.
     * 
     * @return the videoFormat contained in the image data header.
     */
    public VideoFormat getVideoFormat()
    {
        return VideoFormat.valueOf( IO.readInt( buffer, VID_FORMAT_OFFSET ) );
    }

    public void setAlarm( final String s )
    {
        write( nullPad( s, TITLE_LENGTH ), ALARM_OFFSET );
    }

    public void setAlarmBitmask( final int alarmBitmask )
    {
        buffer.putInt( ALM_BITMASK, alarmBitmask );
    }

    public void setAlarmBitmaskHigh( final int alarmBitmaskHigh )
    {
        buffer.putInt( ALM_BITMASK_HI_OFFSET, alarmBitmaskHigh );
    }

    public void setCamera( final int camera )
    {
        buffer.putInt( CAM_OFFSET, camera );
    }

    public void setLineOffset( final short lineOffset )
    {
        writeShort( LINE_OFFSET, lineOffset );
    }

    public void setLocale( final String locale )
    {
        write( nullPad( locale, MAX_NAME_LENGTH ), LOCALE_OFFSET );
    }

    public void setMaxSize( final int maxSize )
    {
        buffer.putInt( MAX_SIZE_OFFSET, maxSize );
    }

    public void setMilliseconds( final int milliseconds )
    {
        buffer.putInt( MILLISECONDS_OFFSET, milliseconds );
    }

    public void setMode( final int mode )
    {
        buffer.putInt( MODE_OFFSET, mode );
    }

    public void setPixelOffset( final short pixelOffset )
    {
        writeShort( PIXEL_OFFSET, pixelOffset );
    }

    public void setQFactor( final int qFactor )
    {
        buffer.putInt( Q_FACTOR_OFFSET, qFactor );
    }

    public void setRes( final String res )
    {
        write( nullPad( res, 4 ), RES_OFFSET );
    }

    public void setSessionTime( final int sessionTime )
    {
        buffer.putInt( SESSION_TIME_OFFSET, sessionTime );
    }

    public void setSize( final int size )
    {
        buffer.putInt( SIZE_OFFSET, size );
    }

    public void setSrcLines( final short srcLines )
    {
        writeShort( SRC_LINES_OFFSET, srcLines );
    }

    public void setSrcPixels( final short srcPixels )
    {
        writeShort( SRC_PIXELS_OFFSET, srcPixels );
    }

    public void setStartOffset( final int startOffset )
    {
        buffer.putInt( START_OFFSET, startOffset );
    }

    public void setStatus( final int status )
    {
        buffer.putInt( STATUS_OFFSET, status );
    }

    public void setTargetLines( final short targetLines )
    {
        writeShort( TARGET_LINES, targetLines );
    }

    public void setTargetPixels( final short targetPixels )
    {
        writeShort( TARGET_PIXELS_OFFSET, targetPixels );
    }

    public void setTargetSize( final int targetSize )
    {
        buffer.putInt( TARGET_SIZE, targetSize );
    }

    public void setTitle( final String title )
    {
        write( nullPad( title, TITLE_LENGTH ), TITLE_OFFSET );
    }

    public void setUtcOffset( final int utcOffset )
    {
        buffer.putInt( UTC_OFFSET_OFFSET, utcOffset );
    }

    public void setVideoFormat( final VideoFormat format )
    {
        buffer.putInt( VID_FORMAT_OFFSET, format.index );
    }

    private byte[] nullPad( final String string, final int length )
    {
       final byte[] bytes = new byte[length];
       System.arraycopy( IO.stringToBytes(string), 0, bytes, 0, string.length() );
       return bytes;
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

  static ImageDataStruct createImageDataStruct( final ByteBuffer data, final String comment,
            final VideoFormat videoFormat, final short targetLines, final short targetPixels )
    {
        final ByteBuffer imageDataBuffer =
                ByteBuffer.allocate( ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + comment.length() + data.limit() ).putInt(
                        0xDECADE11 );
        imageDataBuffer.position( ImageDataStruct.IMAGE_DATA_STRUCT_SIZE );
        imageDataBuffer.put( IO.stringToBytes(comment) );
        imageDataBuffer.put( data );

        final ImageDataStruct imageDataStruct = new ImageDataStruct( imageDataBuffer );

        final int modeChosenByReadingGenericVideoHeader = 2;
        imageDataStruct.setMode( modeChosenByReadingGenericVideoHeader );

        imageDataStruct.setCamera( IO.findInt( comment, "Number: ", 0 ) );

        imageDataStruct.setVideoFormat( videoFormat );

        imageDataStruct.setStartOffset( comment.length() );
        imageDataStruct.setSize( data.limit() );

        final int maxSizeChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setMaxSize( maxSizeChosenByReadingGenericVideoHeader );

        final int targetSizeChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setTargetSize( targetSizeChosenByReadingGenericVideoHeader );

        final int qFactorChosenByReadingGenericVideoHeader = -1;
        imageDataStruct.setQFactor( qFactorChosenByReadingGenericVideoHeader );

        final int alarmBitmaskHighChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setAlarmBitmaskHigh( alarmBitmaskHighChosenByReadingGenericVideoHeader );

        final int statusChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setStatus( statusChosenByReadingGenericVideoHeader );

        try
        {
            imageDataStruct.setSessionTime( (int) ( JFIFHeader.getDateFormat().parse(
                    IO.find( comment, "Date: ", "01/01/1970" ) ).getTime() + JFIFHeader.getTimeFormat().parse(
                    IO.find( comment, "Time: ", "00:00:00" ) ).getTime() ) );
        }
        catch ( final ParseException e )
        {
            throw new RuntimeException( e );
        }

        imageDataStruct.setMilliseconds( IO.findInt( comment, "MSec: ", 0 ) );

        final String resChosenByReadingGenericVideoHeader = "";
        imageDataStruct.setRes( resChosenByReadingGenericVideoHeader );
        imageDataStruct.setTitle( IO.find( comment, "Name: ", "" ) );
        imageDataStruct.setAlarm( IO.find( comment, "Alarm-text: ", "" ) );

        final short srcPixelsChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setSrcPixels( srcPixelsChosenByReadingGenericVideoHeader );

        final short srcLinesChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setSrcLines( srcLinesChosenByReadingGenericVideoHeader );

        imageDataStruct.setTargetPixels( targetPixels );
        imageDataStruct.setTargetLines( targetLines );

        final short pixelOffsetChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setPixelOffset( pixelOffsetChosenByReadingGenericVideoHeader );

        final short lineOffsetChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setLineOffset( lineOffsetChosenByReadingGenericVideoHeader );

        imageDataStruct.setLocale( IO.find( comment, "Locale: ", "" ) );
        imageDataStruct.setUtcOffset( IO.findInt( comment, "UTCoffset: ", 0 ) );

        final int alarmBitmaskChosenByReadingGenericVideoHeader = 0;
        imageDataStruct.setAlarmBitmask( alarmBitmaskChosenByReadingGenericVideoHeader );

        return imageDataStruct;
    }
}
