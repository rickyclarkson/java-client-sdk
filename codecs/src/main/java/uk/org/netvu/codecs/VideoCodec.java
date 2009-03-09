package uk.org.netvu.codecs;

/**
 * {@code VideoCodec} is used purely as a type parameter for
 * {@link VideoDecoder}, to allow VideoDecoders for each supported type of video
 * data to be distinguished from each other.
 */
public class VideoCodec
{
    /**
     * Private to prevent instantiation.
     */
    private VideoCodec()
    {
    }

    /**
     * Represents MPEG-4 data. A {@code VideoDecoder<MPEG4>} is capable of
     * decoding MPEG-4 data.
     */
    public static final class MPEG4 extends VideoCodec
    {
    }

    /**
     * Represents JPEG data. A {@code VideoDecoder<JPEG>} is capable of decoding
     * JPEG data.
     */
    public static final class JPEG extends VideoCodec
    {
    }
}
