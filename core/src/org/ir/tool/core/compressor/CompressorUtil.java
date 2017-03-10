package org.ir.tool.core.compressor;

/**
 * Created by ekamolid on 1/14/2017.
 */
public final class CompressorUtil {
    public static int writeVInt(int i, byte[] data, int offset) {
        while ((i & ~0x7F) != 0) {
            data[offset++] = ((byte) ((i & 0x7F) | 0x80));
            i >>>= 7;
        }
        data[offset++] = ((byte) (i & 0x7F));
        return offset;
    }

    public static int readVInt(byte[] data, int[] offsetArray) {
        byte b = data[offsetArray[0]++];
        int i = b & 0x7F;
        for (int shift = 7; (b & 0x80) != 0; shift += 7) {
            b = data[offsetArray[0]++];
            i |= (b & 0x7F) << shift;
        }
        return i;
    }
}
