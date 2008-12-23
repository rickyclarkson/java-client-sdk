package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;

import uk.org.netvu.util.CheckParameters;

/**
 * A data structure compatible with the image_data struct that video frames
 * arrive in in binary streams.
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

    /**
     * The size of a short, in bytes.
     */
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
     * Constructs an ImageDataStruct with the specified ByteBuffer as its
     * storage. The ByteBuffer must contain valid ImageDataStruct data.
     * 
     * @param buffer
     *        the ByteBuffer to use as the storage for this ImageDataStruct.
     * @return an ImageDataStruct with the specified ByteBuffer as its storage.
     * @throws IllegalStateException
     *         if the ByteBuffer does not contain valid ImageDataStruct data.
     * @throws NullPointerException
     *         if buffer is null.
     */
    public static ImageDataStruct construct( final ByteBuffer buffer )
    {
        CheckParameters.areNotNull( buffer );
        return new ImageDataStruct( buffer );
    }

    /**
     * Constructs an empty ImageDataStruct, with a backing ByteBuffer large
     * enough to hold the ImageDataStruct plus the specified comment size and
     * data size.
     * 
     * @param commentSize
     *        the size of the comment data that this ImageDataStruct will hold.
     * @param dataSize
     *        the size of the image data that this ImageDataStruct will hold.
     * @return an empty ImageDataStruct.
     */
    public static ImageDataStruct construct( final int commentSize, final int dataSize )
    {
        final ByteBuffer buffer = ByteBuffer.allocate( IMAGE_DATA_STRUCT_SIZE + commentSize + dataSize );
        buffer.putInt( 0xDECADE11 );
        return new ImageDataStruct( buffer );
    }

    /**
     * Prepends an appropriate ImageDataStruct header and comment block to a
     * ByteBuffer.
     * 
     * @param data
     *        the video frame to include in the resulting ByteBuffer.
     * @param comment
     *        the comment block to include in the resulting ByteBuffer.
     * @param videoFormat
     *        the format of the video frame.
     * @param targetLines
     *        the vertical resolution of the video frame.
     * @param targetPixels
     *        the horizontal resolution of the video frame.
     * @return an ImageDataStruct according to the supplied parameters.
     * @throws NullPointerException
     *         if any of the parameters are null.
     */
    static ImageDataStruct construct( final ByteBuffer data, final String comment, final VideoFormat videoFormat,
            final short targetLines, final short targetPixels )
    {
        data.position( 0 );

        CheckParameters.areNotNull( data, comment, videoFormat );
        final byte[] commentBytes = IO.stringToBytes( comment );

        final ByteBuffer imageDataBuffer =
                ByteBuffer.allocate( ImageDataStruct.IMAGE_DATA_STRUCT_SIZE + commentBytes.length + data.limit() ).putInt(
                        0xDECADE11 );
        imageDataBuffer.position( ImageDataStruct.IMAGE_DATA_STRUCT_SIZE );
        imageDataBuffer.put( commentBytes );
        imageDataBuffer.put( data );

        final ImageDataStruct imageDataStruct = new ImageDataStruct( imageDataBuffer );

        imageDataStruct.setMode( 2 );

        imageDataStruct.setCamera( IO.findInt( comment, "Number: ", 0 ) );

        imageDataStruct.setVideoFormat( videoFormat );

        imageDataStruct.setStartOffset( commentBytes.length );
        imageDataStruct.setSize( data.limit() );
        imageDataStruct.setMaxSize( 0 );
        imageDataStruct.setTargetSize( 0 );
        imageDataStruct.setQFactor( -1 );
        imageDataStruct.setAlarmBitmaskHigh( 0 );
        imageDataStruct.setStatus( 0 );

        try
        {
            imageDataStruct.setSessionTime( (int) ( JFIFHeader.getDateFormat().parse(
                    IO.find( comment, "Date: ", "01/01/1970" ) ).getTime() + JFIFHeader.getTimeFormat().parse(
                    IO.find( comment, "Time: ", "00:00:00" ) ).getTime() ) );
        }
        catch ( final ParseException e )
        {
            throw new IllegalStateException( e.getMessage() );
        }

        imageDataStruct.setMilliseconds( IO.findInt( comment, "MSec: ", 0 ) );

        imageDataStruct.setRes( "" );
        imageDataStruct.setTitle( IO.find( comment, "Name: ", "" ) );
        imageDataStruct.setAlarm( IO.find( comment, "Alarm-text: ", "" ) );
        imageDataStruct.setSrcPixels( (short) 0 );
        imageDataStruct.setSrcLines( (short) 0 );
        imageDataStruct.setTargetPixels( targetPixels );
        imageDataStruct.setTargetLines( targetLines );
        imageDataStruct.setPixelOffset( (short) 0 );
        imageDataStruct.setLineOffset( (short) 0 );
        imageDataStruct.setLocale( IO.find( comment, "Locale: ", "" ) );
        imageDataStruct.setUtcOffset( IO.findInt( comment, "UTCoffset: ", 0 ) );
        imageDataStruct.setAlarmBitmask( 0 );

        return new ImageDataStruct( imageDataStruct.getByteBuffer() );
    }

    /**
     * The ByteBuffer in which all the data for this ImageDataStruct is stored.
     */
    private final ByteBuffer buffer;

    /**
     * Constructs an ImageDataStruct with data from the specified ByteBuffer.
     * 
     * @param buffer
     *        the ByteBuffer to read data from.
     * @throws NullPointerException
     *         if buffer is null.
     */
    private ImageDataStruct( final ByteBuffer buffer )
    {
        CheckParameters.areNotNull( buffer );
        this.buffer = IO.duplicate( buffer );

        final int version = this.buffer.getInt( VERSION_OFFSET );

        if ( version != 0xDECADE10 && version != 0xDECADE11 )
        {
            throw new IllegalStateException( "version is " + Integer.toHexString( version ) );
        }
    }

    /**
     * Gets the alarm contained in the image data header.
     * 
     * @return the alarm contained in the image data header.
     */
    public String getAlarm()
    {
        return IO.nullTerminate( read( buffer, TITLE_LENGTH, ALARM_OFFSET ) );
    }

    /**
     * Gets the alarmBitmask contained in the image data header.
     * 
     * @return the alarmBitmask contained in the image data header.
     */
    public int getAlarmBitmask()
    {
        return buffer.getInt( ALM_BITMASK );
    }

    /**
     * Gets the alarmBitmaskHigh contained in the image data header.
     * 
     * @return the alarmBitmaskHigh contained in the image data header.
     */
    public int getAlarmBitmaskHigh()
    {
        return buffer.getInt( ALM_BITMASK_HI_OFFSET );
    }

    /**
     * Gets the ByteBuffer backing this ImageDataStruct.
     * 
     * @return the ByteBuffer backing this ImageDataStruct.
     */
    public ByteBuffer getByteBuffer()
    {
        return IO.duplicate( buffer ).asReadOnlyBuffer();
    }

    /**
     * Gets the camera contained in the image data header.
     * 
     * @return the camera contained in the image data header.
     */
    public int getCamera()
    {
        return buffer.getInt( CAM_OFFSET );
    }

    /**
     * Gets the image data contained in this ImageDataStruct.
     * 
     * @return the image data contained in this ImageDataStruct.
     */
    public ByteBuffer getData()
    {
        return IO.from( buffer, getStartOffset() + IMAGE_DATA_STRUCT_SIZE ).asReadOnlyBuffer();
    }

    /**
     * Gets the value of the line offset field from this ImageDataStruct.
     * 
     * @return the value of the line offset field from this ImageDataStruct.
     */
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
        return IO.nullTerminate( read( buffer, MAX_NAME_LENGTH, LOCALE_OFFSET ) );
    }

    /**
     * Gets the maxSize contained in the image data header.
     * 
     * @return the maxSize contained in the image data header.
     */
    public int getMaxSize()
    {
        return buffer.getInt( MAX_SIZE_OFFSET );
    }

    /**
     * Gets the milliseconds contained in the image data header.
     * 
     * @return the milliseconds contained in the image data header.
     */
    public int getMilliseconds()
    {
        return (int) ( ( 0xFFFFFFFFL & buffer.getInt( MILLISECONDS_OFFSET ) ) % 1000L );
    }

    /**
     * Gets the mode contained in the image data header.
     * 
     * @return the mode contained in the image data header.
     */
    public int getMode()
    {
        return buffer.getInt( MODE_OFFSET );
    }

    /**
     * Gets the value of the pixel offset field contained in the image data
     * header.
     * 
     * @return the value of the pixel offset field contained in the image data
     *         header.
     */
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
        return buffer.getInt( Q_FACTOR_OFFSET );
    }

    /**
     * Gets the res contained in the image data header.
     * 
     * @return the res contained in the image data header.
     */
    public String getRes()
    {
        final String res = IO.nullTerminate( read( buffer, 4, RES_OFFSET ) );
        return res.length() > 4 ? res.substring( 0, 4 ) : res;
    }

    /**
     * Gets the sessionTime contained in the image data header.
     * 
     * @return the sessionTime contained in the image data header.
     */
    public int getSessionTime()
    {
        return buffer.getInt( SESSION_TIME_OFFSET );
    }

    /**
     * Gets the size contained in the image data header.
     * 
     * @return the size contained in the image data header.
     */
    public int getSize()
    {
        return buffer.getInt( SIZE_OFFSET );
    }

    /**
     * Gets the source lines value (vertical resolution) contained in the image
     * data header.
     * 
     * @return the source lines value (vertical resolution) contained in the
     *         image data header.
     */
    public short getSrcLines()
    {
        return readShort( SRC_LINES_OFFSET );
    }

    /**
     * Gets the source pixels value (horizontal resolution) contained in the
     * image data header.
     * 
     * @return the source pixels value (horizontal resolution) contained in the
     *         image data header.
     */
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
        return buffer.getInt( START_OFFSET );
    }

    /**
     * Gets the status contained in the image data header.
     * 
     * @return the status contained in the image data header.
     */
    public int getStatus()
    {
        return buffer.getInt( STATUS_OFFSET );
    }

    /**
     * Gets the targetLines value contained in the image data header.
     * 
     * @return the targetLines value contained in the image data header.
     */
    public short getTargetLines()
    {
        return readShort( TARGET_LINES );
    }

    /**
     * Gets the targetPixels value contained in the image data header.
     * 
     * @return the targetPixels value contained in the image data header.
     */
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
        return buffer.getInt( TARGET_SIZE );
    }

    /**
     * Gets the title contained in the image data header.
     * 
     * @return the title contained in the image data header.
     */
    public String getTitle()
    {
        return IO.nullTerminate( read( buffer, TITLE_LENGTH, TITLE_OFFSET ) );
    }

    /**
     * Gets the utcOffset contained in the image data header.
     * 
     * @return the utcOffset contained in the image data header.
     */
    public int getUtcOffset()
    {
        return buffer.getInt( UTC_OFFSET_OFFSET );
    }

    /**
     * Gets the version of the image data header.
     * 
     * @return the version of the image data header.
     */
    public int getVersion()
    {
        return buffer.getInt( VERSION_OFFSET );
    }

    /**
     * Gets the videoFormat contained in the image data header.
     * 
     * @return the videoFormat contained in the image data header.
     */
    public VideoFormat getVideoFormat()
    {
        return VideoFormat.valueOf( buffer.getInt( VID_FORMAT_OFFSET ) );
    }

    /**
     * Copies the specified String to a byte[] of the specified length, using
     * the US-ASCII character encoding, and leaving all trailing bytes set to 0.
     * 
     * @param string
     *        the String to copy.
     * @param length
     *        the length of the byte[] to copy the String into.
     * @return a byte[] of the specified length containing the US-ASCII-encoded
     *         bytes from the specified String.
     * @throws NullPointerException
     *         if string is null.
     */
    private byte[] nullPad( final String string, final int length )
    {
        CheckParameters.areNotNull( string );
        final byte[] bytes = new byte[length];
        System.arraycopy( IO.stringToBytes( string ), 0, bytes, 0, string.length() );
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
     * Reads a short from the specified position within the ByteBuffer relative
     * to the offset, with the endianness according to the version field.
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

    /**
     * Sets the alarm contained in the image data header.
     * 
     * @param s
     *        the alarm to store in the image data header.
     * @throws NullPointerException
     *         if s is null.
     */
    void setAlarm( final String s )
    {
        CheckParameters.areNotNull( s );
        write( nullPad( s, TITLE_LENGTH ), ALARM_OFFSET );
    }

    /**
     * Sets the alarm bitmask contained in the image data header.
     * 
     * @param alarmBitmask
     *        the alarm bitmask to store in the image data header.
     */
    void setAlarmBitmask( final int alarmBitmask )
    {
        buffer.putInt( ALM_BITMASK, alarmBitmask );
    }

    /**
     * Sets the high-bit part of the alarm bitmask in the image data header.
     * 
     * @param alarmBitmaskHigh
     *        the high-bit part of the alarm bitmask to store.
     */
    void setAlarmBitmaskHigh( final int alarmBitmaskHigh )
    {
        buffer.putInt( ALM_BITMASK_HI_OFFSET, alarmBitmaskHigh );
    }

    /**
     * Sets the camera number contained in the image data header.
     * 
     * @param camera
     *        the camera number to store in the image data header.
     */
    void setCamera( final int camera )
    {
        buffer.putInt( CAM_OFFSET, camera );
    }

    /**
     * Sets the line offset contained in the image data header.
     * 
     * @param lineOffset
     *        the line offset to store in the image data header.
     */
    void setLineOffset( final short lineOffset )
    {
        writeShort( LINE_OFFSET, lineOffset );
    }

    /**
     * Sets the locale contained in the image data header.
     * 
     * @param locale
     *        the locale to store in the image data header.
     * @throws NullPointerException
     *         if locale is null.
     */
    void setLocale( final String locale )
    {
        CheckParameters.areNotNull( locale );
        write( nullPad( locale, MAX_NAME_LENGTH ), LOCALE_OFFSET );
    }

    /**
     * Sets the maxSize contained in the image data header.
     * 
     * @param maxSize
     *        the max size to store in the image data header.
     */
    void setMaxSize( final int maxSize )
    {
        buffer.putInt( MAX_SIZE_OFFSET, maxSize );
    }

    /**
     * Sets the milliseconds part of the timestamp contained in the image data
     * header.
     * 
     * @param milliseconds
     *        the milliseconds part of the timestamp to store in the image data
     *        header.
     */
    void setMilliseconds( final int milliseconds )
    {
        buffer.putInt( MILLISECONDS_OFFSET, milliseconds );
    }

    /**
     * Sets the mode contained in the image data header.
     * 
     * @param mode
     *        the mode contained in the image data header.
     */
    void setMode( final int mode )
    {
        buffer.putInt( MODE_OFFSET, mode );
    }

    /**
     * Sets the pixel offset contained in the image data header.
     * 
     * @param pixelOffset
     *        the pixel offset to store in the image data header.
     */
    void setPixelOffset( final short pixelOffset )
    {
        writeShort( PIXEL_OFFSET, pixelOffset );
    }

    /**
     * Sets the quantisation factor contained in the image data header.
     * 
     * @param qFactor
     *        the quantisation factor to store in the image data header.
     */
    void setQFactor( final int qFactor )
    {
        buffer.putInt( Q_FACTOR_OFFSET, qFactor );
    }

    /**
     * Sets the res value contained in the image data header.
     * 
     * @param res
     *        the res value to store in the image data header.
     * @throws NullPointerException
     *         if res is null.
     */
    void setRes( final String res )
    {
        CheckParameters.areNotNull( res );
        write( nullPad( res, 4 ), RES_OFFSET );
    }

    /**
     * Sets the session time contained in the image data header.
     * 
     * @param sessionTime
     *        the session time to store in the image data header.
     */
    void setSessionTime( final int sessionTime )
    {
        buffer.putInt( SESSION_TIME_OFFSET, sessionTime );
    }

    /**
     * Sets the size contained in the image data header.
     * 
     * @param size
     *        the size to store in the image data header.
     */
    void setSize( final int size )
    {
        buffer.putInt( SIZE_OFFSET, size );
    }

    /**
     * Sets the srcLines value contained in the image data header.
     * 
     * @param srcLines
     *        the srcLines value to store in the image data header.
     */
    void setSrcLines( final short srcLines )
    {
        writeShort( SRC_LINES_OFFSET, srcLines );
    }

    /**
     * Sets the srcPixels value contained in the image data header.
     * 
     * @param srcPixels
     *        the srcPixels value to store in the image data header.
     */
    void setSrcPixels( final short srcPixels )
    {
        writeShort( SRC_PIXELS_OFFSET, srcPixels );
    }

    /**
     * Sets the startOffset value contained in the image data header.
     * 
     * @param startOffset
     *        the startOffset value to store in the image data header.
     */
    void setStartOffset( final int startOffset )
    {
        buffer.putInt( START_OFFSET, startOffset );
    }

    /**
     * Sets the status contained in the image data header.
     * 
     * @param status
     *        the status to store in the image data header.
     */
    void setStatus( final int status )
    {
        buffer.putInt( STATUS_OFFSET, status );
    }

    /**
     * Sets the targetLines value contained in the image data header.
     * 
     * @param targetLines
     *        the targetLines value to store in the image data header.
     */
    void setTargetLines( final short targetLines )
    {
        writeShort( TARGET_LINES, targetLines );
    }

    /**
     * Sets the targetPixels value contained in the image data header.
     * 
     * @param targetPixels
     *        the targetPixels value to store in the image data header.
     */
    void setTargetPixels( final short targetPixels )
    {
        writeShort( TARGET_PIXELS_OFFSET, targetPixels );
    }

    /**
     * Sets the targetSize value contained in the image data header.
     * 
     * @param targetSize
     *        the targetSize value to store in the image data header.
     */
    void setTargetSize( final int targetSize )
    {
        buffer.putInt( TARGET_SIZE, targetSize );
    }

    /**
     * Sets the title contained in the image data header.
     * 
     * @param title
     *        the title to store in the image data header.
     * @throws NullPointerException
     *         if title is null.
     */
    void setTitle( final String title )
    {
        CheckParameters.areNotNull( title );
        write( nullPad( title, TITLE_LENGTH ), TITLE_OFFSET );
    }

    /**
     * Sets the offset from UTC contained in the image data header.
     * 
     * @param utcOffset
     *        the offset from UTC to store in the image data header.
     */
    void setUtcOffset( final int utcOffset )
    {
        buffer.putInt( UTC_OFFSET_OFFSET, utcOffset );
    }

    /**
     * Sets the video format contained in the image data header.
     * 
     * @param format
     *        the video format to store in the image data header.
     * @throws NullPointerException
     *         if format is null.
     */
    void setVideoFormat( final VideoFormat format )
    {
        CheckParameters.areNotNull( format );
        buffer.putInt( VID_FORMAT_OFFSET, format.index );
    }

    /**
     * Gives the ByteOrder for reading the short fields with. For 0xDECADE10
     * they are little-endian, and for 0xDECADE11 they are big-endian.
     * 
     * @return the ByteOrder for reading the short fields with.
     */
    private ByteOrder versionToByteOrder()
    {
        return getVersion() == 0xDECADE10 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }

    /**
     * Writes the specified bytes to the ByteBuffer at the specified position
     * within the ByteBuffer.
     * 
     * @param bytes
     *        the bytes to write.
     * @param position
     *        the position in the ByteBuffer at which to begin writing.
     * @throws NullPointerException
     *         if bytes is null.
     */
    private void write( final byte[] bytes, final int position )
    {
        CheckParameters.areNotNull( bytes );
        buffer.position( position );
        buffer.put( bytes );
    }

    /**
     * Writes the specified short to the ByteBuffer at the specified position.
     * The endianness used is set according to the version field.
     * 
     * @param position
     *        the position in the ByteBuffer at which to write the short.
     * @param value
     *        the value to write.
     */
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
