package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import uk.org.netvu.util.CheckParameters;
import uk.org.netvu.util.Option;
import uk.org.netvu.util.ParameterDescription;
import uk.org.netvu.util.ParameterMap;
import uk.org.netvu.util.StringConversion;
import uk.org.netvu.util.ParameterDescription.ParameterDescriptionWithoutDefault;

/**
 * A Builder for building PictureStructs. All parameters are mandatory. Call
 * build() to get a PictureStruct.
 */
public final class PictureStructBuilder
{
    private static final StringConversion<Short> none = StringConversion.none( "Unsupported" );
    private static final ParameterDescriptionWithoutDefault<Short> SRC_PIXELS =
            ParameterDescription.parameterWithoutDefault( "srcPixels", none );
    private static final ParameterDescriptionWithoutDefault<Short> SRC_LINES =
            ParameterDescription.parameterWithoutDefault( "srcLines", none );
    private static final ParameterDescriptionWithoutDefault<Short> TARGET_PIXELS =
            ParameterDescription.parameterWithoutDefault( "targetPixels", none );
    private static final ParameterDescriptionWithoutDefault<Short> TARGET_LINES =
            ParameterDescription.parameterWithoutDefault( "targetLines", none );
    private static final ParameterDescriptionWithoutDefault<Short> PIXEL_OFFSET =
            ParameterDescription.parameterWithoutDefault( "pixelOffset", none );
    private static final ParameterDescriptionWithoutDefault<Short> LINE_OFFSET =
            ParameterDescription.parameterWithoutDefault( "lineOffset", none );

    private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

    private final ByteOrder byteOrder;

    /**
     * Constructs a PictureStructBuilder ready to be given parameters to build a
     * PictureStruct.
     * 
     * @param byteOrder
     *        whether to use big-endian or little-endian in the ByteBuffer that
     *        the PictureStruct holds.
     * @throws NullPointerException
     *         if byteOrder is null.
     */
    public PictureStructBuilder( final ByteOrder byteOrder )
    {
        CheckParameters.areNotNull( byteOrder );
        this.byteOrder = byteOrder;
    }

    /**
     * Builds the PictureStruct, if all the parameters are set. build() can only
     * be called once per PictureStructBuilder.
     * 
     * @return a PictureStruct.
     * @throws IllegalStateException
     *         if any of the parameters are unset.
     */
    public PictureStruct build()
    {
        final ParameterMap map = parameterMap.get();

        final ByteBuffer buffer = ByteBuffer.allocate( 12 );
        buffer.order( byteOrder );
        final List<ParameterDescriptionWithoutDefault<Short>> params =
                Arrays.asList( SRC_PIXELS, SRC_LINES, TARGET_PIXELS, TARGET_LINES, PIXEL_OFFSET, LINE_OFFSET );
        for ( final ParameterDescriptionWithoutDefault<Short> param : params )
        {
            buffer.putShort( map.get( param ).get() );
        }

        buffer.position( 0 );
        buffer.order( ByteOrder.BIG_ENDIAN );

        try
        {
            return new PictureStruct( buffer, byteOrder, 0 );
        }
        finally
        {
            parameterMap = Option.getEmptyOption( "The PictureStructBuilder has already had build() called on it" );
        }
    }

    /**
     * Sets the lineOffset parameter of the PictureStruct.
     * 
     * @param lineOffset
     *        the lineOffset to use in the PictureStruct.
     * @return the PictureStructBuilder.
     */
    public PictureStructBuilder lineOffset( final short lineOffset )
    {
        return set( LINE_OFFSET, lineOffset );
    }

    /**
     * Sets the pixelOffset parameter of the PictureStruct.
     * 
     * @param pixelOffset
     *        the pixelOffset to use in the PictureStruct.
     * @return the PictureStructBuilder.
     */
    public PictureStructBuilder pixelOffset( final short pixelOffset )
    {
        return set( PIXEL_OFFSET, pixelOffset );
    }

    /**
     * Sets the srcLines parameter of the PictureStruct.
     * 
     * @param srcLines
     *        the srcLines to use in the PictureStruct.
     * @return the PictureStructBuilder.
     */
    public PictureStructBuilder srcLines( final short srcLines )
    {
        return set( SRC_LINES, srcLines );
    }

    /**
     * Sets the srcPixels parameter of the PictureStruct.
     * 
     * @param srcPixels
     *        the srcPixels to use in the PictureStruct.
     * @return the PictureStructBuilder.
     */
    public PictureStructBuilder srcPixels( final short srcPixels )
    {
        return set( SRC_PIXELS, srcPixels );
    }

    /**
     * Sets the targetLines parameter of the PictureStruct.
     * 
     * @param targetLines
     *        the targetLines to use in the PictureStruct.
     * @return the PictureStructBuilder.
     */
    public PictureStructBuilder targetLines( final short targetLines )
    {
        return set( TARGET_LINES, targetLines );
    }

    /**
     * Sets the targetPixels parameter of the PictureStruct.
     * 
     * @param targetPixels
     *        the targetPixels to use in the PictureStruct.
     * @return the PictureStructBuilder.
     */
    public PictureStructBuilder targetPixels( final short targetPixels )
    {
        return set( TARGET_PIXELS, targetPixels );
    }

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
        CheckParameters.areNotNull( parameter, value );

        if ( parameterMap.isEmpty() )
        {
            final String message = "The PictureStructBuilder has already been built (build() has been called on it).";
            throw new IllegalStateException( message );
        }

        parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
        return this;
    }
}
