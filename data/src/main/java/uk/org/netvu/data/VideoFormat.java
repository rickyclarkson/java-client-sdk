package uk.org.netvu.data;

public enum VideoFormat
{
    JPEG_422(0),
    JPEG_411(1),
        MPEG4_P_FRAME(2),
        MPEG4_I_FRAME(3),
        MPEG4_GOV_P_FRAME(4),
        MPEG4_GOV_I_FRAME(5);

    private final int index;

    VideoFormat(int index)
    {
        this.index = index;
    }

    public static VideoFormat valueOf(int i)
            {
                for (final VideoFormat format: values())
                    {
                        if (format.index == i)
                            return format;
                    }
                throw new IllegalArgumentException(Integer.toHexString(i)+" is not a supported video format");
            }
}