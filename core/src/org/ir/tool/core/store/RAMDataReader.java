package org.ir.tool.core.store;

import org.ir.tool.core.ByteArraySlice;

import java.io.IOException;

/**
 * Created by ekamolid on 12/23/2016.
 */
public class RAMDataReader extends Reader {
    byte[] data;
    private int pointer = 0;

    public RAMDataReader(byte[] data) {
        this.data = data;
    }


    @Override
    public byte readByte() throws IOException {
        if (pointer >= data.length) {
            throw new IOException("read exceeded the data size");
        }
        return data[pointer++];
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
        if (data.length <= offset + length) {
            throw new IOException("read exceeded the data size");
        }
        return new ByteArraySlice(data, offset, length);
    }

    @Override
    public void readBytes(byte[] bytes, int length) throws IOException {
        if (data.length <= length) {
            throw new IOException("read exceeded the data size");
        }
        System.arraycopy(data, pointer, bytes, 0, length);
        pointer += length;
    }

    public byte[] getData() {
        return data;
    }
}