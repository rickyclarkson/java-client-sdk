package uk.org.netvu.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.text.SimpleDateFormat;

enum FrameType
{
    JFIF
    {
        public void deliverTo( InputStream input, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
            handler.jfif( new JFIFPacket( IO.readIntoByteBuffer( input, metadata.getLength() ), metadata ) );
        }
    },
    JPEG
    {
        public void deliverTo( InputStream input, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
            ImageData imageData = new ImageData( IO.readIntoByteBuffer( input, ImageData.IMAGE_DATA_SIZE ) );
            IO.readIntoByteBuffer( input, imageData.getStartOffset() );
            ByteBuffer restOfData = IO.readIntoByteBuffer( input, metadata.getLength() - imageData.getStartOffset() - ImageData.IMAGE_DATA_SIZE );
            handler.jfif( jpegToJfif( restOfData, metadata, imageData ) );
        }
    },
    UNKNOWN
    {
        public void deliverTo( InputStream data, StreamHandler handler, StreamMetadata metadata ) throws IOException
        {
            ByteBuffer buffer = ByteBuffer.allocate( metadata.getLength() );
            Channels.newChannel( data ).read( buffer );
            buffer.position( 0 );
            handler.dataArrived( IO.readIntoByteBuffer( data, metadata.getLength() ), metadata );
        }
    };

    public abstract void deliverTo( InputStream data, StreamHandler handler, StreamMetadata metadata ) throws IOException;

    private static final byte[] YVIS = { 16, 11, 12, 14, 12, 10, 16, 14, 13, 14, 18,
                                         17, 16, 19, 24, 40, 26, 24, 22, 22, 24, 49, 35, 37, 29, 40, 58, 51, 61,
                                         60, 57, 51, 56, 55, 64, 72, 92, 78, 64, 68, 87, 69, 55, 56, 80, 109,
                                         81, 87, 95, 98, 103, 104, 103, 62, 77, 113, 121, 112, 100, 120, 92,
                                         101, 103, 99 };

    private static final byte[] UVVIS = { 17, 18, 18, 24, 21, 24, 47, 26, 26, 47, 99,
                                          66, 56, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
                                          99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99,
                                          99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };

    private static JFIFPacket jpegToJfif( ByteBuffer source, StreamMetadata metadata, ImageData imageData )
    {
        final byte[] JFIF_HEADER = byteArrayLiteral(new int[]{ 0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00 });
        final byte[] SOC_HEADER = byteArrayLiteral(new int[]{ 0xFF, 0xFE });
        final byte[] YQ_HEADER = byteArrayLiteral(new int[]{ 0xFF, 0xDB, 0x00, 0x43, 0x00 });
        final byte[] UVQ_HEADER = byteArrayLiteral(new int[]{ 0xFF, 0xDB, 0x00, 0x43, 0x01 });
        final byte[] HUFFMAN_HEADER = byteArrayLiteral(new int[]{  0xFF, 0xC4, 0x00, 0x1F, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00,
                                                                   0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A,
                                                                   0x0B, 0xFF, 0xC4, 0x00, 0xB5, 0x10, 0x00, 0x02, 0x01, 0x03, 0x03, 0x02, 0x04, 0x03, 0x05, 0x05,
                                                                   0x04, 0x04, 0x00, 0x00, 0x01, 0x7D, 0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31,
                                                                   0x41, 0x06, 0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xA1, 0x08, 0x23, 0x42,
                                                                   0xB1, 0xC1, 0x15, 0x52, 0xD1, 0xF0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0A, 0x16, 0x17, 0x18,
                                                                   0x19, 0x1A, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x43,
                                                                   0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63,
                                                                   0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0x83,
                                                                   0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9A,
                                                                   0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 0xB7, 0xB8,
                                                                   0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6,
                                                                   0xD7, 0xD8, 0xD9, 0xDA, 0xE1, 0xE2, 0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF1, 0xF2,
                                                                   0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA, 0xFF, 0xC4, 0x00, 0x1F, 0x01, 0x00, 0x03, 0x01,
                                                                   0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02,
                                                                   0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0xFF, 0xC4, 0x00, 0xB5, 0x11, 0x00, 0x02,
                                                                   0x01, 0x02, 0x04, 0x04, 0x03, 0x04, 0x07, 0x05, 0x04, 0x04, 0x00, 0x01, 0x02, 0x77, 0x00, 0x01,
                                                                   0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41, 0x51, 0x07, 0x61, 0x71, 0x13, 0x22,
                                                                   0x32, 0x81, 0x08, 0x14, 0x42, 0x91, 0xA1, 0xB1, 0xC1, 0x09, 0x23, 0x33, 0x52, 0xF0, 0x15, 0x62,
                                                                   0x72, 0xD1, 0x0A, 0x16, 0x24, 0x34, 0xE1, 0x25, 0xF1, 0x17, 0x18, 0x19, 0x1A, 0x26, 0x27, 0x28,
                                                                   0x29, 0x2A, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A,
                                                                   0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A,
                                                                   0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89,
                                                                   0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7,
                                                                   0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5,
                                                                   0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE2, 0xE3,
                                                                   0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF2, 0xF3, 0xF4, 0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA });

        byte[] SOS_HEADER = byteArrayLiteral(new int[]{ 0xFF, 0xDA, 0x00, 0x0C, 0x03, 0x01, 0x00, 0x02, 0x11, 0x03, 0x11, 0x00, 0x3F, 0x00 });

        byte[] EOI_MARKER = byteArrayLiteral(new int[]{ 0xFF, 0xD9 });

        ByteBuffer comment = getComment(imageData, limit(source.duplicate(), imageData.getStartOffset()));
        byte[][] qTables = buildQTables(imageData.getQFactor());
        byte[] yqFactors = qTables[0];
        byte[] uvqFactors = qTables[1];
        ByteBuffer sof = ByteBuffer.allocate(19);
        sof.put(byteArrayLiteral(new int[]{ 0xFF, 0xC0, 0x00, 0x11, 0x08 }));
        sof.order(ByteOrder.LITTLE_ENDIAN);
        sof.putShort(imageData.getFormat().targetLines);
        sof.putShort(imageData.getFormat().targetPixels);
        sof.order(ByteOrder.BIG_ENDIAN);
        sof.put(byteArrayLiteral(new int[]{ 0x03, 0x01, imageData.getVidFormat() == VideoFormat.JPEG_411 ? 0x22 : 0x21, 0x00, 0x02,
                                            0x11, 0x01, 0x03, 0x11, 0x01 }));
        sof.position(0);
        
        ByteBuffer jfif = ByteBuffer.allocate( JFIF_HEADER.length + SOC_HEADER.length + 2 + comment.limit() + YQ_HEADER.length + yqFactors.length + UVQ_HEADER.length + uvqFactors.length + sof.limit() +
                                               HUFFMAN_HEADER.length + SOS_HEADER.length + source.limit() - imageData.getStartOffset() + EOI_MARKER.length );
        jfif.put(JFIF_HEADER);
        jfif.put(SOC_HEADER);
        jfif.putShort((short)(comment.limit() + 2));
        jfif.put(comment);
        jfif.put(YQ_HEADER);
        jfif.put(yqFactors);
        jfif.put(UVQ_HEADER);
        jfif.put(uvqFactors);
        jfif.put(sof);
        jfif.put(HUFFMAN_HEADER);
        jfif.put(SOS_HEADER);
        source.position(imageData.getStartOffset());
        jfif.put(source.slice());
        source.position(0);
        jfif.put(EOI_MARKER);
        jfif.position(0);
        return new JFIFPacket( jfif, metadata );
    }

    private static byte[][] buildQTables(int qFactor)
    {
        if (qFactor < 1 || qFactor > 255)
            throw null;

        byte[] yqFactors = new byte[YVIS.length];
        byte[] uvqFactors = new byte[YVIS.length];

        for (int a = 0; a < YVIS.length; a++)
        {
            yqFactors[a] = (byte)Math.min(255,Math.max(1,(YVIS[a] * qFactor + 25)/50));
            uvqFactors[a] = (byte)Math.min(255, Math.max(1, (UVVIS[a] * qFactor + 25)/50));
        }

        return new byte[][]{ yqFactors, uvqFactors };
    }

    private static byte[] byteArrayLiteral(int[] literals)
    {
        byte[] results = new byte[literals.length];
        for (int a=0;a<literals.length;a++)
            results[a] = (byte)literals[a];
        return results;
    }

    private static ByteBuffer limit(ByteBuffer buffer, int limit)
    {
        buffer.limit(limit);
        return buffer;
    }

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

    private static ByteBuffer getComment(ImageData imageData, ByteBuffer commentData)
    {
        String locale = nullTerminate(imageData.getLocale());
        String alarmText = nullTerminate(imageData.getAlarm());
        String title = nullTerminate(imageData.getTitle());
        int bufferCapacity = getCommentByteCount(imageData.getCam(), imageData.getUtcOffset(), commentData) + title.length() + alarmText.length() + "dd/MM/yyyy".length() + "HH:mm:ss".length() + 256;
        ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity);
        println(buffer, "Version: 00.03");
        println(buffer, "Number: " + imageData.getCam());
        println(buffer, "Name: " + title);
        Date date = new Date(imageData.getSessionTime() * 1000L);
        println(buffer, "Date: " + dateFormatter.format(date));
        println(buffer, "Time: " + timeFormatter.format(date));
        println(buffer, "MSec: " + imageData.getMilliseconds() % 1000);
        println(buffer, "Locale: " + locale);
        println(buffer, "UTCoffset: " + imageData.getUtcOffset());
        if (alarmText.length() > 0)
        {
            println(buffer, "Alarm-text: " + alarmText);
        }
        buffer.put(commentData);
        buffer.position(0);
        return buffer;
    }

    private static void println(ByteBuffer buffer, String string)
    {
        buffer.put((string + '\n').getBytes(US_ASCII));
    }

    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    private static String nullTerminate(byte[] input)
    {
        String result = new String(input, US_ASCII);
        int indexOfNull = result.indexOf('\0');
        return indexOfNull == -1 ? result : result.substring(0, indexOfNull);
    }

    private static int getCommentByteCount(int camera, int utcOffset, ByteBuffer commentData)
    {
        final int COMMENT_BYTE_TABLE_LENGTH = 9;
        return COMMENT_BYTE_TABLE_LENGTH + widthOfInt(camera) + widthOfInt(utcOffset) + commentData.limit();
    }

    private static int widthOfInt(int i)
    {
        return String.valueOf(i).length();
    }
}