package org.ir.tool.core.store;

import org.ir.tool.core.ByteArraySlice;

import java.io.IOException;

/**
 * Created by ekamolid on 12/25/2016.
 */
public abstract class Reader {

    public abstract byte readByte() throws IOException;
    public abstract int position();
    public abstract void seek(int position) throws IOException;
    public abstract ByteArraySlice readBytes(int offset, int length) throws IOException;
    public abstract void readBytes(byte[] bytes, int length) throws IOException;

    public int readVInt() throws IOException {
        byte b = readByte();
        int i = b & 0x7F;
        for (int shift = 7; (b & 0x80) != 0; shift += 7) {
            b = readByte();
            i |= (b & 0x7F) << shift;
        }
        return i;
    }
}
