package uk.org.netvu.data;

import java.nio.ByteBuffer;

interface Packet
{
    int getChannel();
    ByteBuffer getByteBuffer();
}