package uk.org.netvu.data;

import java.nio.ByteBuffer;

import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Option;
import uk.org.netvu.util.ParameterDescription;
import uk.org.netvu.util.ParameterMap;
import uk.org.netvu.util.StringConversion;
import uk.org.netvu.util.ParameterDescription.ParameterDescriptionWithoutDefault;

/**
 * A Builder for ImageDataStruct objects.
 */
public final class ImageDataStructBuilder
{
    private static final ParameterDescriptionWithoutDefault<String> ALARM =
            ParameterDescription.parameterWithoutDefault( "alarm", StringConversion.string() );

    private static final ParameterDescriptionWithoutDefault<Integer> ALARM_BITMASK =
            ParameterDescription.parameterWithoutDefault( "alarmBitmask", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> ALARM_BITMASK_HIGH =
            ParameterDescription.parameterWithoutDefault( "alarmBitmaskHigh", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> CAMERA =
            ParameterDescription.parameterWithoutDefault( "camera", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<PictureStruct> FORMAT =
            ParameterDescription.parameterWithoutDefault( "format",
                    StringConversion.<PictureStruct> none( "Unsupported" ) );

    private static final ParameterDescriptionWithoutDefault<String> LOCALE =
            ParameterDescription.parameterWithoutDefault( "locale", StringConversion.string() );

    private static final ParameterDescriptionWithoutDefault<Integer> MAX_SIZE =
            ParameterDescription.parameterWithoutDefault( "maxSize", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> MILLISECONDS =
            ParameterDescription.parameterWithoutDefault( "milliseconds", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> MODE =
            ParameterDescription.parameterWithoutDefault( "mode", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> Q_FACTOR =
            ParameterDescription.parameterWithoutDefault( "qFactor", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<byte[]> RES =
            ParameterDescription.parameterWithoutDefault( "res", StringConversion.<byte[]> none( "Unsupported" ) );

    private static final ParameterDescriptionWithoutDefault<Integer> SESSION_TIME =
            ParameterDescription.parameterWithoutDefault( "sessionTime", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> SIZE =
            ParameterDescription.parameterWithoutDefault( "size", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> START_OFFSET =
            ParameterDescription.parameterWithoutDefault( "startOffset", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> STATUS =
            ParameterDescription.parameterWithoutDefault( "status", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> TARGET_SIZE =
            ParameterDescription.parameterWithoutDefault( "targetSize", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<String> TITLE =
            ParameterDescription.parameterWithoutDefault( "title", StringConversion.string() );

    private static final ParameterDescriptionWithoutDefault<Integer> UTC_OFFSET =
            ParameterDescription.parameterWithoutDefault( "utcOffset", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> VERSION =
            ParameterDescription.parameterWithoutDefault( "version", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<VideoFormat> VIDEO_FORMAT =
            ParameterDescription.parameterWithoutDefault( "videoFormat",
                    StringConversion.<VideoFormat> none( "Unsupported" ) );

    private static byte[] nullPad( final String text, final int length )
    {
        CheckParameters.areNotNull( text );

        if ( text.length() > length )
        {
            throw new IllegalArgumentException( "text must be no more than " + length + " characters" );
        }

        final byte[] result = new byte[length];
        System.arraycopy( text.getBytes(), 0, result, 0, text.getBytes().length );
        return result;
    }

    private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

    /**
     * Constructs an ImageDataStructBuilder ready to add parameters to.
     */
    public ImageDataStructBuilder()
    {
    }

    /**
     * Sets the alarm parameter for the built ImageDataStruct.
     * 
     * @param alarm
     *        the alarm parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder alarm( final String alarm )
    {
        return set( ALARM, alarm );
    }

    /**
     * Sets the alarmBitmask parameter for the built ImageDataStruct.
     * 
     * @param alarmBitmask
     *        the alarmBitmask parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder alarmBitmask( final int alarmBitmask )
    {
        return set( ALARM_BITMASK, alarmBitmask );
    }

    /**
     * Sets the alarmBitmaskHigh parameter for the built ImageDataStruct.
     * 
     * @param alarmBitmaskHigh
     *        the alarmBitmaskHigh parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder alarmBitmaskHigh( final int alarmBitmaskHigh )
    {
        return set( ALARM_BITMASK_HIGH, alarmBitmaskHigh );
    }

    /**
     * Builds an ImageDataStruct using the supplied parameters.
     * 
     * @return an ImageDataStruct built using the supplied parameters.
     */
    public ImageDataStruct build()
    {
        final ByteBuffer buffer = ByteBuffer.wrap( new byte[ImageDataStruct.IMAGE_DATA_STRUCT_SIZE] );
        final ParameterMap map = parameterMap.get();
        buffer.putInt( map.get( VERSION ).get() );
        buffer.putInt( map.get( MODE ).get() );
        buffer.putInt( map.get( CAMERA ).get() );
        buffer.putInt( map.get( VIDEO_FORMAT ).get().index );
        buffer.putInt( map.get( START_OFFSET ).get() );
        buffer.putInt( map.get( SIZE ).get() );
        buffer.putInt( map.get( MAX_SIZE ).get() );
        buffer.putInt( map.get( TARGET_SIZE ).get() );
        buffer.putInt( map.get( Q_FACTOR ).get() );
        buffer.putInt( map.get( ALARM_BITMASK_HIGH ).get() );
        buffer.putInt( map.get( STATUS ).get() );
        buffer.putInt( map.get( SESSION_TIME ).get() );
        buffer.putInt( map.get( MILLISECONDS ).get() );

        final byte[] res = map.get( RES ).get();
        if ( res.length != 4 )
        {
            throw new IllegalStateException( "res must have bytes" );
        }
        buffer.put( res );

        buffer.put( nullPad( map.get( TITLE ).get(), ImageDataStruct.TITLE_LENGTH ) );
        buffer.put( nullPad( map.get( ALARM ).get(), ImageDataStruct.TITLE_LENGTH ) );
        buffer.put( map.get( FORMAT ).get().getByteBuffer() );
        buffer.put( nullPad( map.get( LOCALE ).get(), ImageDataStruct.MAX_NAME_LENGTH ) );

        buffer.putInt( map.get( UTC_OFFSET ).get() );
        buffer.putInt( map.get( ALARM_BITMASK ).get() );
        buffer.position( 0 );

        try
        {
            return new ImageDataStruct( buffer );
        }
        finally
        {
            parameterMap = Option.getEmptyOption( "The ImageDataStructBuilder has already had build() called on it" );
        }
    }

    /**
     * Sets the camera parameter for the built ImageDataStruct.
     * 
     * @param camera
     *        the camera parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder camera( final int camera )
    {
        return set( CAMERA, camera );
    }

    /**
     * Sets the format parameter for the built ImageDataStruct.
     * 
     * @param format
     *        the format parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder format( final PictureStruct format )
    {
        return set( FORMAT, format );
    }

    /**
     * Sets the locale parameter for the built ImageDataStruct.
     * 
     * @param locale
     *        the locale parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder locale( final String locale )
    {
        return set( LOCALE, locale );
    }

    /**
     * Sets the maxSize parameter for the built ImageDataStruct.
     * 
     * @param maxSize
     *        the maxSize parameter for the build ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder maxSize( final int maxSize )
    {
        return set( MAX_SIZE, maxSize );
    }

    /**
     * Sets the milliseconds parameter for the built ImageDataStruct.
     * 
     * @param milliseconds
     *        the milliseconds parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder milliseconds( final int milliseconds )
    {
        return set( MILLISECONDS, milliseconds );
    }

    /**
     * Sets the mode parameter for the built ImageDataStruct.
     * 
     * @param mode
     *        the mode parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder mode( final int mode )
    {
        return set( MODE, mode );
    }

    /**
     * Sets the qFactor parameter for the built ImageDataStruct.
     * 
     * @param qFactor
     *        the qFactor parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder qFactor( final int qFactor )
    {
        return set( Q_FACTOR, qFactor );
    }

    /**
     * Sets the res parameter for the built ImageDataStruct.
     * 
     * @param res
     *        the res parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder res( final byte[] res )
    {
        return set( RES, res );
    }

    /**
     * Sets the sessionTime parameter for the built ImageDataStruct.
     * 
     * @param sessionTime
     *        the sessionTime parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder sessionTime( final int sessionTime )
    {
        return set( SESSION_TIME, sessionTime );
    }

    /**
     * Sets the size parameter for the built ImageDataStruct.
     * 
     * @param size
     *        the size parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder size( final int size )
    {
        return set( SIZE, size );
    }

    /**
     * Sets the startOffset parameter for the built ImageDataStruct.
     * 
     * @param startOffset
     *        the startOffset parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder startOffset( final int startOffset )
    {
        return set( START_OFFSET, startOffset );
    }

    /**
     * Sets the status parameter for the built ImageDataStruct.
     * 
     * @param status
     *        the status parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder status( final int status )
    {
        return set( STATUS, status );
    }

    /**
     * Sets the targetSize parameter for the built ImageDataStruct.
     * 
     * @param targetSize
     *        the targetSize parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder targetSize( final int targetSize )
    {
        return set( TARGET_SIZE, targetSize );
    }

    /**
     * Sets the title parameter for the built ImageDataStruct.
     * 
     * @param title
     *        the title parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder title( final String title )
    {
        return set( TITLE, title );
    }

    /**
     * Sets the utcOffset parameter for the built ImageDataStruct.
     * 
     * @param utcOffset
     *        the utcOffset parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder utcOffset( final int utcOffset )
    {
        return set( UTC_OFFSET, utcOffset );
    }

    /**
     * Sets the version parameter for the built ImageDataStruct.
     * 
     * @param version
     *        the version parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder version( final int version )
    {
        return set( VERSION, version );
    }

    /**
     * Sets the videoFormat parameter for the built ImageDataStruct.
     * 
     * @param format
     *        the videoFormat parameter for the built ImageDataStruct.
     * @return the ImageDataStructBuilder.
     */
    public ImageDataStructBuilder videoFormat( final VideoFormat format )
    {
        return set( VIDEO_FORMAT, format );
    }

    /**
     * Sets the value of a parameter to a given value, and returns the
     * ImageDataStructBuilder.
     * 
     * @param <T>
     *        the input type of the specified parameter.
     * @param parameter
     *        the parameter to set a value for.
     * @param value
     *        the value to give that parameter.
     * @return the ImageDataStructBuilder.
     * @throws IllegalStateException
     *         if the ImageDataStructBuilder has already been built once.
     * @throws NullPointerException
     *         if parameter or value are null.
     */
    private <T> ImageDataStructBuilder set( final ParameterDescriptionWithoutDefault<T> parameter, final T value )
    {
        if ( parameterMap.isEmpty() )
        {
            final String message = "The Builder has already been built (build() has been called on it).";
            throw new IllegalStateException( message );
        }

        parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
        return this;
    }
}
