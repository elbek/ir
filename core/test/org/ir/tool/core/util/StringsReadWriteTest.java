package org.ir.tool.core.util;

import org.ir.tool.core.Worker;
import org.junit.Test;

/**
 * Created by ekamolid on 12/19/2016.
 */
public class StringsReadWriteTest {

    @Test
    public void generate() throws Exception {
        int size = 100_000_000;
        StringsReadWrite stringsReadWrite = new StringsReadWrite("C:/tmp/file/ASCII_NOT_NUMBER_LOWER.txt", 1024 * 1024, false);
        stringsReadWrite.generate(size, (byte) 2, (byte) 12, RandomUtil.RandomStringEnum.ASCII_NOT_NUMBER_LOWER);
        stringsReadWrite.close();
    }

    @Test
    public void generate1() throws Exception {
        int size = 100_000_000;
        StringsReadWrite stringsReadWrite = new StringsReadWrite("C:/tmp/file/NON_ASCII.txt", 1024 * 1024, false);
        stringsReadWrite.generate(size, (byte) 4, (byte) 12, RandomUtil.RandomStringEnum.NON_ASCII);
        stringsReadWrite.close();
    }

    @Test
    public void read() throws Exception {
        StringsReadWrite stringsReadWrite = new StringsReadWrite("C:/tmp/file/ASCII_NOT_NUMBER_LOWER.txt", 1024 * 1024, true);
        stringsReadWrite.read(100_000_000, new Worker() {
            @Override
            public void doWork(byte[] bytes, int offset, int count) {

            }
        });
        stringsReadWrite.close();
    }
}