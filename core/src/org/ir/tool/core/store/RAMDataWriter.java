package org.ir.tool.core.store;

import org.ir.tool.core.ByteArraySlice;
import org.ir.tool.core.util.ArrayUtil;

import java.io.IOException;

/**
 * Created by ekamolid on 12/23/2016.
 */
public class RAMDataWriter extends Writer {
    byte[] data = new byte[1 << 5];
    private int pointer = 0;

    public RAMDataWriter() {

    }

    @Override
    public void writeByte(byte b) throws IOException {
        if (data.length == pointer) {
            data = ArrayUtil.grow(data);
        }
        data[pointer++] = b;
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        if (data.length < pointer + length) {
            data = ArrayUtil.grow(data, pointer + length);
        }
        System.arraycopy(b, offset, data, pointer, length);
        pointer += length;
    }

    public ByteArraySlice data() {
        return new ByteArraySlice(data, 0, pointer);
    }
}