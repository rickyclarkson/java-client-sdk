package uk.org.netvu.data;

import uk.org.netvu.util.CheckParameters;

/**
 * A factory that, given a StreamType, gives a Parser that can parse it.
 */
public final class ParserFactory
{
    /**
     * Gets a Parser for the specified StreamType.
     * 
     * @param streamType
     *        the StreamType to get a Parser for.
     * @return a Parser for the specified StreamType.
     * @throws NullPointerException
     *         if the streamType is null.
     */
    public static Parser parserFor( final StreamType streamType )
    {
        CheckParameters.areNotNull( streamType );
        return streamType.parser;
    }

  /**
   * Gets a Parser for the specified mime type.
   * @param mimeType the mime type to get a Parser for.
   * @return a Parser for the specified mime type.
   * @throws NullPointerException if mimeType is null.
   * @throws IllegalArgumentException if the mime type specified is not supported.
   */
  public static Parser parserFor( final String mimeType)
  {
    if (mimeType.equals("multipart/x-mixed-replace"))
      {
        return new MimeParser();
      }
    if (mimeType.equals("video/adhbinary"))
      return new BinaryParser();

    throw new IllegalArgumentException(mimeType + " is not a supported mime type.");
  }

    /**
     * Private to prevent instantiation.
     */
    private ParserFactory()
    {
    }
}
