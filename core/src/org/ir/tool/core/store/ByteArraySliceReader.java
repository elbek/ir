package org.ir.tool.core.store;

import org.ir.tool.core.ByteArraySlice;

import java.io.IOException;

/**
 * Created by ekamolid on 12/23/2016.
 */
public class ByteArraySliceReader extends Reader {
    private ByteArraySlice byteArraySlice;
    private int pointer = 0;

    public ByteArraySliceReader(ByteArraySlice byteArraySlice) {
        this.byteArraySlice = byteArraySlice;
        pointer = byteArraySlice.getOffset();
    }

    @Override
    public byte readByte() throws IOException {
        if (pointer >= byteArraySlice.getMaxBytePoint()) {
            throw new IOException("read exceeded the data size");
        }
        return byteArraySlice.getData()[pointer++];
    }

    @Override
    public int position() {
        return pointer;
    }

    @Override
    public void seek(int position) throws IOException {
        pointer = position;
    }

    @Override
    public ByteArraySlice readBytes(int offset, int length) throws IOException {
        if (byteArraySlice.getMaxBytePoint() <= offset + length) {
            throw new IOException("read exceeded the data size");
        }
        return new ByteArraySlice(byteArraySlice.getData(), offset, length);
    }

    @Override
    public void readBytes(byte[] bytes, int length) throws IOException {
        if (byteArraySlice.getLength() <= pointer + length) {
            throw new IOException("read exceeded the data size");
        }
        System.arraycopy(byteArraySlice.getData(), pointer, bytes, 0, length);
        pointer += length;
    }

    public ByteArraySlice data() {
        return byteArraySlice;
    }
}