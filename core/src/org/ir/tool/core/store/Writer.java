package org.ir.tool.core.store;

import java.io.IOException;

/**
 * Created by ekamolid on 12/23/2016.
 */
public abstract class Writer {
    public abstract void writeByte(byte b) throws IOException;
    public abstract void writeBytes(byte[] b, int offset, int length) throws IOException;

    public void writeVInt(int i) throws IOException {
        while ((i & ~0x7F) != 0) {
            writeByte((byte) ((i & 0x7F) | 0x80));
            i >>>= 7;
        }
        writeByte((byte) (i & 0x7F));
    }

    public void writeInt(int i) throws IOException {
        writeByte((byte) i);

        i >>>= 8;
        writeByte((byte) i);

        i >>>= 8;
        writeByte((byte) i);

        i >>>= 8;
        writeByte((byte) i);
    }
}
