package uk.org.netvu.data;

import uk.org.netvu.util.ParameterMap;
import uk.org.netvu.util.ParameterDescription;
import uk.org.netvu.util.ParameterDescription.ParameterDescriptionWithoutDefault;
import uk.org.netvu.util.Option;
import uk.org.netvu.util.StringConversion;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import uk.org.netvu.util.CheckParameters;

public final class PictureStructBuilder
{
    private static final StringConversion<Short> none = StringConversion.none("Unsupported");
    private static final ParameterDescriptionWithoutDefault<Short> SRC_PIXELS = ParameterDescription.parameterWithoutDefault("srcPixels", none);
    private static final ParameterDescriptionWithoutDefault<Short> SRC_LINES = ParameterDescription.parameterWithoutDefault("srcLines", none);
    private static final ParameterDescriptionWithoutDefault<Short> TARGET_PIXELS = ParameterDescription.parameterWithoutDefault("targetPixels", none);
    private static final ParameterDescriptionWithoutDefault<Short> TARGET_LINES = ParameterDescription.parameterWithoutDefault("targetLines", none);
    private static final ParameterDescriptionWithoutDefault<Short> PIXEL_OFFSET = ParameterDescription.parameterWithoutDefault("pixelOffset", none);
    private static final ParameterDescriptionWithoutDefault<Short> LINE_OFFSET = ParameterDescription.parameterWithoutDefault("lineOffset", none);

    private Option<ParameterMap> parameterMap = Option.getFullOption(new ParameterMap());

    private final ByteOrder byteOrder;

    public PictureStructBuilder(ByteOrder byteOrder)
    {
        CheckParameters.areNotNull(byteOrder);
        this.byteOrder = byteOrder;
    }

    public PictureStructBuilder srcPixels( short srcPixels) { return set( SRC_PIXELS, srcPixels ); }
    public PictureStructBuilder srcLines( short srcLines) { return set( SRC_LINES, srcLines ); }
    public PictureStructBuilder targetPixels( short targetPixels) { return set( TARGET_PIXELS, targetPixels ); }
    public PictureStructBuilder targetLines( short targetLines) { return set( TARGET_LINES, targetLines ); }
    public PictureStructBuilder pixelOffset( short pixelOffset) { return set( PIXEL_OFFSET, pixelOffset ); }
    public PictureStructBuilder lineOffset( short lineOffset) { return set( LINE_OFFSET, lineOffset ); }

    /**
     * Sets the value of a parameter to a given value, and returns the
     * PictureStructBuilder.
     * 
     * @param <T>
     *        the input type of the specified parameter.
     * @param parameter
     *        the parameter to set a value for.
     * @param value
     *        the value to give that parameter.
     * @return the PictureStructBuilder.
     * @throws IllegalStateException
     *         if the PictureStructBuilder has already been built once.
     * @throws NullPointerException
     *         if parameter or value are null.
     */
    private <T> PictureStructBuilder set( final ParameterDescriptionWithoutDefault<T> parameter, final T value )
    {
        CheckParameters.areNotNull(parameter, value);

        if ( parameterMap.isEmpty() )
        {
            final String message = "The PictureStructBuilder has already been built (build() has been called on it).";
            throw new IllegalStateException( message );
        }
        
        parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
        return this;
    }

    public PictureStruct build()
    {
        ParameterMap map = parameterMap.get();

        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.order(byteOrder);
        List<ParameterDescriptionWithoutDefault<Short>> params = Arrays.asList(SRC_PIXELS, SRC_LINES, TARGET_PIXELS, TARGET_LINES, PIXEL_OFFSET, LINE_OFFSET);
        for (final ParameterDescriptionWithoutDefault<Short> param: params)
        {
            buffer.putShort(map.get(param).get());
        }

        buffer.position(0);
        buffer.order(ByteOrder.BIG_ENDIAN);

        try
        {
            return new PictureStruct(buffer, byteOrder, 0);
        }
        finally
        {
            parameterMap = Option.getEmptyOption( "The PictureStructBuilder has already had build() called on it");
        }
    }
}