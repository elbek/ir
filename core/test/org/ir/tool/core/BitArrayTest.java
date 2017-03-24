package org.ir.tool.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ekamolid on 12/25/2016.
 */
public class BitArrayTest {

    BitArray bitArray;

    @Before
    public void setUp() throws Exception {
        bitArray = new BitArray();
    }

    @After
    public void tearDown() throws Exception {
        bitArray.clear();
    }

    @Test
    public void get() throws Exception {
        bitArray = new BitArray(3 * 1024);
        for (int i = 0; i < 3 * 1024; i++) {
            if (i % 2 == 0) {
                bitArray.set(i);
            }
        }

        for (int i = 0; i < 3 * 1024; i++) {
            if (i % 2 == 0) {
                Assert.assertTrue(bitArray.get(i));
            } else {
                Assert.assertFalse(bitArray.get(i));
            }
        }
        Assert.assertFalse(bitArray.get(100000000));
    }

    @Test
    public void toBytes() throws Exception {
        for (int i = 0; i < 64; i++) {
            bitArray.set(i);
        }
        byte[] bytes = bitArray.toBytes();
        for (byte aByte : bytes) {
            Assert.assertEquals(aByte, -1);
        }
        bitArray.clear();

        for (int i = 6; i < 64; i += 8) {
            bitArray.set(i);
        }

        bytes = bitArray.toBytes();
        for (byte aByte : bytes) {
            Assert.assertEquals(aByte, 1 << 6);
        }
    }

    @Test
    public void fromBytes() throws Exception {
        for (int i = 0; i < 1024; i++) {
            bitArray.set(i);
        }
        byte[] bytes = bitArray.toBytes();
        Assert.assertArrayEquals(BitArray.fromBytes(bytes, 0, bytes.length).toBytes(), bytes);

        bitArray.clear();
        for (int i = 1000; i < 1250000; i++) {
            bitArray.set(i);
        }
        bytes = bitArray.toBytes();
        Assert.assertArrayEquals(bytes, BitArray.fromBytes(bytes, 0, bytes.length).toBytes());
        bitArray.clear();
        for (int i = 100; i < 102050000; i++) {
            bitArray.set(i);
        }
        bytes = bitArray.toBytes();
        Assert.assertArrayEquals(bytes, BitArray.fromBytes(bytes, 0, bytes.length).toBytes());
    }
}