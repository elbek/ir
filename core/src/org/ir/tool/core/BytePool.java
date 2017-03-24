package org.ir.tool.core;

/**
 * Created by ekamolid on 2/6/2017.
 */
public class BytePool {
    static int MAX_LEN = 1 << 14;
    byte[][] data = new byte[1][];
    int level = 0;
    int pointer = 0;
    static short allocSize = 200;

    public int add(byte[] data) {
        return 0;
    }

    public void add(int start, byte[] data) {
        int size = allocSize;
        if (data.length + 4 >= size) {
            size = data.length + 4;
        }
        int startPointer = pointer;
        alloc(size);
    }

    void alloc(int size) {
        if (pointer + size > MAX_LEN) {
            level += 1;
            pointer = pointer + size - MAX_LEN;
        } else {
            pointer = pointer + size;
        }
    }
}
