package uk.org.netvu.core.cgi.displaypic;

import java.io.IOException;

interface Handler
{
    void handleJPEG( byte[] data ) throws IOException;
}
