package org.ir.tool.core;

/**
 * Created by ekamolid on 12/25/2016.
 */
public class BitArray {
    long array[];
    int maxBit = 0;

    public BitArray() {
        array = new long[1];
    }

    public void clear() {
        array = new long[1];
        maxBit = 0;
    }

    public BitArray(int byteCount) {
        if ((byteCount & 7) == 0) {
            array = new long[byteCount >> 3];
        } else {
            array = new long[(byteCount >> 3) + 1];
        }
        maxBit = byteCount * 8;
    }

    public void set(int bit) {
        int bucket = bit >> 6;
        resizeIfNeeded(bucket);
        if (bit > maxBit) {
            maxBit = bit;
        }
        int cell = bit & 63;
        array[bucket] |= 1L << cell;
    }

    public boolean get(int bit) {
        int bucket = bit >> 6;
        if (bit > maxBit) {
            return false;
        }
        int cell = bit & 63;
        return (array[bucket] & (1L << cell)) != 0;
    }

    public static BitArray fromBytes(byte[] bytes, int start, int length) {
        BitArray bitArray = new BitArray(length);
        bitArray.maxBit = length * 8 - 1;
        int bucket = 0;
        int i = start;
        long l = 0;
        byte shift = 0;
        for (; i < start + length; i++) {
            long aByte = bytes[i] & 0xFF;
            l |= aByte << (shift * 8);
            shift++;
            if (shift == 8) {
                bitArray.set(l, bucket++);
                l = 0;
                shift = 0;
            }
        }
        if (i != start && l != 0) {
            bitArray.set(l, bucket);
        }
        return bitArray;
    }

    private void set(long l, int bucket) {
        resizeIfNeeded(bucket);
        array[bucket] = l;
    }

    public byte[] toBytes() {
        byte[] bytes;
        if ((maxBit & 7) == 0) {
            bytes = new byte[(maxBit >> 3)];
        } else {
            bytes = new byte[((maxBit >> 3) + 1)];
        }
        int i = 0;
        for (; i < bytes.length; i += 8) {
            for (int k = 0; k < 8; k++) {
                if ((i + k) < bytes.length) {
                    bytes[i + k] = (byte) (array[i >> 3] >>> (k * 8));
                }
            }
        }
        return bytes;
    }

    private void resizeIfNeeded(int bucket) {
        if (bucket >= array.length) {
            int newSize = array.length * 2;
            if (newSize < bucket) {
                newSize = bucket + 1;
            }
            long[] newArray = new long[newSize];
            System.arraycopy(array, 0, newArray, 0, array.length);
            array = newArray;
        }
    }
}