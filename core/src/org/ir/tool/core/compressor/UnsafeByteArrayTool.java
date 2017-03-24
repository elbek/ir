package org.ir.tool.core.compressor;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Created by ekamolid on 1/16/2017.
 */
class UnsafeByteArrayTool {

    private static final Unsafe UNSAFE;
    private static final long BYTE_ARRAY_OFFSET;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            int boo = UNSAFE.arrayBaseOffset(byte[].class);
            // It seems not all Unsafe implementations implement the following method.
            UNSAFE.copyMemory(new byte[1], boo, new byte[1], boo, 1);
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void copyLong(byte[] src, int srcPos, byte[] dest, int destPos) {
        UNSAFE.putLong(dest, destPos + BYTE_ARRAY_OFFSET, UNSAFE.getLong(src, srcPos + BYTE_ARRAY_OFFSET));
    }

    public static void copyMemory(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        assert srcPos >= 0 : srcPos;
        assert srcPos <= src.length - length : srcPos;
        assert destPos >= 0 : destPos;
        assert destPos <= dest.length - length : destPos;
        UNSAFE.copyMemory(src, BYTE_ARRAY_OFFSET + srcPos, dest, BYTE_ARRAY_OFFSET + destPos, length);
    }

    static void copyUpTo32(byte[] in, int inputIndex, byte[] out, int outputIndex, int lengthMinusOne) {
        long inPtr = BYTE_ARRAY_OFFSET + inputIndex;
        long outPtr = BYTE_ARRAY_OFFSET + outputIndex;

        UNSAFE.putLong(out, outPtr, UNSAFE.getLong(in, inPtr));
        if (lengthMinusOne > 7) {
            inPtr += 8;
            outPtr += 8;
            UNSAFE.putLong(out, outPtr, UNSAFE.getLong(in, inPtr));
            if (lengthMinusOne > 15) {
                inPtr += 8;
                outPtr += 8;
                UNSAFE.putLong(out, outPtr, UNSAFE.getLong(in, inPtr));
                if (lengthMinusOne > 23) {
                    inPtr += 8;
                    outPtr += 8;
                    UNSAFE.putLong(out, outPtr, UNSAFE.getLong(in, inPtr));
                }
            }
        }
    }

    static void copy32(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        long inPtr = BYTE_ARRAY_OFFSET + srcPos;
        long outPtr = BYTE_ARRAY_OFFSET + destPos;
        int copied = 0;
        do {
            UNSAFE.putLong(dest, outPtr, UNSAFE.getLong(src, inPtr));
            inPtr += 8;
            outPtr += 8;
            UNSAFE.putLong(dest, outPtr, UNSAFE.getLong(src, inPtr));
            inPtr += 8;
            outPtr += 8;
            UNSAFE.putLong(dest, outPtr, UNSAFE.getLong(src, inPtr));
            inPtr += 8;
            outPtr += 8;
            UNSAFE.putLong(dest, outPtr, UNSAFE.getLong(src, inPtr));
            inPtr += 8;
            outPtr += 8;

            copied += 32;
        } while (copied < length);
    }
}
