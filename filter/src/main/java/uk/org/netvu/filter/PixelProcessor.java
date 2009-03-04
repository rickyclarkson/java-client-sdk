package uk.org.netvu.filter;

import java.awt.image.DataBuffer;

interface PixelProcessor
{
    void setPixels(int[] pixels, DataBuffer originalData);
}