package uk.org.netvu.data;

import uk.org.netvu.util.ParameterMap;
import uk.org.netvu.util.ParameterDescription;
import uk.org.netvu.util.ParameterDescription.ParameterDescriptionWithoutDefault;
import uk.org.netvu.util.StringConversion;
import uk.org.netvu.util.CheckParameters;

import java.util.ArrayList;
import java.util.List;
import uk.org.netvu.util.Option;

import java.nio.ByteBuffer;
public final class ImageDataStructBuilder
{
    private static final ParameterDescriptionWithoutDefault<String> ALARM = ParameterDescription.parameterWithoutDefault( "alarm", StringConversion.string());

    private static final ParameterDescriptionWithoutDefault<Integer> ALARM_BITMASK = ParameterDescription.parameterWithoutDefault( "alarmBitmask", StringConversion.integer());

    private static final ParameterDescriptionWithoutDefault<Integer> ALARM_BITMASK_HIGH = ParameterDescription.parameterWithoutDefault( "alarmBitmaskHigh", StringConversion.integer());

    private static final ParameterDescriptionWithoutDefault<Integer> CAMERA = ParameterDescription.parameterWithoutDefault( "camera", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<PictureStruct> FORMAT = ParameterDescription.parameterWithoutDefault( "format", StringConversion.<PictureStruct>none("Unsupported") );

    private static final ParameterDescriptionWithoutDefault<String> LOCALE = ParameterDescription.parameterWithoutDefault( "locale", StringConversion.string() );

    private static final ParameterDescriptionWithoutDefault<Integer> MAX_SIZE = ParameterDescription.parameterWithoutDefault( "maxSize", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> MILLISECONDS = ParameterDescription.parameterWithoutDefault( "milliseconds", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> MODE = ParameterDescription.parameterWithoutDefault( "mode", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> Q_FACTOR = ParameterDescription.parameterWithoutDefault( "qFactor", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<byte[]> RES = ParameterDescription.parameterWithoutDefault( "res", StringConversion.<byte[]>none("Unsupported") );

    private static final ParameterDescriptionWithoutDefault<Integer> SESSION_TIME = ParameterDescription.parameterWithoutDefault( "sessionTime", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> SIZE = ParameterDescription.parameterWithoutDefault( "size", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> START_OFFSET = ParameterDescription.parameterWithoutDefault( "startOffset", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> STATUS = ParameterDescription.parameterWithoutDefault( "status", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> TARGET_SIZE = ParameterDescription.parameterWithoutDefault( "targetSize", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<String> TITLE = ParameterDescription.parameterWithoutDefault( "title", StringConversion.string() );

    private static final ParameterDescriptionWithoutDefault<Integer> UTC_OFFSET = ParameterDescription.parameterWithoutDefault( "utcOffset", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<Integer> VERSION = ParameterDescription.parameterWithoutDefault("version", StringConversion.integer() );

    private static final ParameterDescriptionWithoutDefault<VideoFormat> VIDEO_FORMAT = ParameterDescription.parameterWithoutDefault( "videoFormat", StringConversion.<VideoFormat>none("Unsupported") );

    private Option<ParameterMap> parameterMap = Option.getFullOption(new ParameterMap( ) );

    public ImageDataStructBuilder()
    {
    }

    public ImageDataStructBuilder alarm(String alarm)
    {
        return set( ALARM, alarm );
    }

    public ImageDataStructBuilder alarmBitmask(int alarmBitmask)
    {
        return set( ALARM_BITMASK, alarmBitmask );
    }

    public ImageDataStructBuilder alarmBitmaskHigh(int alarmBitmaskHigh)
    {
        return set( ALARM_BITMASK_HIGH, alarmBitmaskHigh );
    }

    public ImageDataStructBuilder camera(int camera)
    {
        return set( CAMERA, camera );
    }

    public ImageDataStructBuilder format(PictureStruct format)
    {
        return set( FORMAT, format );
    }

    public ImageDataStructBuilder locale(String locale)
    {
        return set( LOCALE, locale );
    }

    public ImageDataStructBuilder maxSize(int maxSize)
    {
        return set( MAX_SIZE, maxSize );
    }

    public ImageDataStructBuilder milliseconds(int milliseconds)
    {
        return set( MILLISECONDS, milliseconds );
    }

    public ImageDataStructBuilder mode(int mode)
    {
        return set( MODE, mode );
    }

    public ImageDataStructBuilder qFactor(int qFactor)
    {
        return set( Q_FACTOR, qFactor );
    }

    public ImageDataStructBuilder res(byte[] res)
    {
        return set( RES, res );
    }

    public ImageDataStructBuilder sessionTime(int sessionTime)
    {
        return set( SESSION_TIME, sessionTime );
    }

    public ImageDataStructBuilder size(int size)
    {
        return set( SIZE, size );
    }

    public ImageDataStructBuilder startOffset(int startOffset)
    {
        return set( START_OFFSET, startOffset );
    }

    public ImageDataStructBuilder status(int status)
    {
        return set( STATUS, status );
    }

    public ImageDataStructBuilder targetSize(int targetSize)
    {
        return set( TARGET_SIZE, targetSize );
    }

    public ImageDataStructBuilder title(String title)
    {
        return set( TITLE, title );
    }

    public ImageDataStructBuilder utcOffset(int utcOffset)
    {
        return set( UTC_OFFSET, utcOffset );
    }

    public ImageDataStructBuilder version(int version)
    {
        return set( VERSION, version );
    }

    public ImageDataStructBuilder videoFormat(VideoFormat format)
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

    public ImageDataStruct build()
    {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[ImageDataStruct.IMAGE_DATA_STRUCT_SIZE]);
        ParameterMap map = parameterMap.get();
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

        byte[] res = map.get( RES ).get();
        if (res.length != 4)
        {
            throw new IllegalStateException("res must have bytes");
        }
        buffer.put(res);

        buffer.put(nullPad(map.get( TITLE ).get(), ImageDataStruct.TITLE_LENGTH ) );
        buffer.put(nullPad(map.get( ALARM ).get(), ImageDataStruct.TITLE_LENGTH ) );
        buffer.put(map.get( FORMAT ).get().getByteBuffer() );
        buffer.put(nullPad(map.get( LOCALE ).get(), ImageDataStruct.MAX_NAME_LENGTH));
       
        buffer.putInt(map.get( UTC_OFFSET ).get());
        buffer.putInt(map.get( ALARM_BITMASK).get());
        buffer.position(0);
        
        try
        {
            return new ImageDataStruct(buffer);
        }
        finally
        {
            parameterMap = Option.getEmptyOption("The ImageDataStructBuilder has already had build() called on it");
        }
    }

    private static byte[] nullPad(String text, int length)
    {
        CheckParameters.areNotNull(text);

        if (text.length() > length)
        {
            throw new IllegalArgumentException("text must be no more than "+length+" characters");
        }

        byte[] result = new byte[length];
        System.arraycopy(text.getBytes(), 0, result, 0, text.getBytes().length);
        return result;
    }
}