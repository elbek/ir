package org.ir.tool.core;

/**
 * Created by ekamolid on 12/3/2016.
 */
public class ByteArraySlice {
    byte[] data;
    int offset;
    int length;

    public ByteArraySlice(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public void reset(byte[] data, int start, int len) {
        this.data = data;
        this.offset = start;
        this.length = len;
    }

    @Override
    public String toString() {
        if (data == null) {
            return null;
        }
        return new String(data, offset, length);
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[length];
        System.arraycopy(data, offset, bytes, 0, length);
        return bytes;
    }

    public void toBytes(byte[] bytes, int at) {
        assert bytes.length >= at + length;
        System.arraycopy(data, offset, bytes, at, length);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public int getMaxBytePoint() {
        return offset + length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
