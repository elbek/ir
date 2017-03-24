package org.ir.tool.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ekamolid on 10/21/2016.
 */
public class Test1 {
    FileChannel inChannel;
    ByteBuffer buffer;
    RandomAccessFile aFile;
    int byteCount = 0;
    static int limit = 20_000_000;

    public Test1(String path) throws FileNotFoundException {
        aFile = new RandomAccessFile(path, "rw");
        inChannel = aFile.getChannel();
        buffer = ByteBuffer.allocate(1024 * 1024);
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(String.format("%8s", Integer.toBinaryString(aByte & 0xFF)).replace(' ', '0'));
        writeTestRegular("C:/tmp/rg.b");
        writeTest("C:/tmp/compressed.b");
    }

    static void writeTestRegular(String path) throws IOException {
        Test1 test1 = new Test1(path);
        for (int i = 0; i < limit; i++) {
            test1.writeInt(i % 100);
        }
        test1.write();
        test1.inChannel.close();
        test1.aFile.close();
        System.out.println(test1.byteCount);
    }

    static void writeTest(String path) throws IOException {
        Test1 test1 = new Test1(path);
        for (int i = 0; i < limit; i++) {
            test1.writeVInt(i % 128);
        }
        test1.write();
        test1.inChannel.close();
        test1.aFile.close();
        System.out.println(test1.byteCount);
    }

    static void readTest(String path) throws IOException {
        Test1 test1 = new Test1(path);
        test1.read();
        for (int i = 0; i < 10_000_000; i++) {
            int q = test1.readVInt();
            if (i != q) {
                System.out.println(i + q);
            }
        }
        test1.inChannel.close();
        test1.aFile.close();
    }

    void read() throws IOException {
        buffer.clear();
        inChannel.read(buffer);
        buffer.flip();
    }

    byte readByte() throws IOException {
        if (!buffer.hasRemaining()) {
            read();
        }
        return buffer.get();
    }

    void write() throws IOException {
        if (buffer.position() > 0) {
            buffer.flip();
            inChannel.write(buffer);
            buffer.clear();
        }
    }

    void writeByte(byte b) throws IOException {
        if (!buffer.hasRemaining()) {
            write();
        }
        buffer.put(b);
        byteCount++;
    }

    void writeVInt(int i) throws IOException {
        while ((i & ~0x7F) != 0) {
            writeByte((byte) ((i & 0x7F) | 0x80));
            i >>>= 7;
        }
        writeByte((byte) (i & 0x7F));
    }

    void writeInt(int i) throws IOException {
        writeByte((byte) i);

        i >>>= 8;
        writeByte((byte) i);

        i >>>= 8;
        writeByte((byte) i);

        i >>>= 8;
        writeByte((byte) i);
    }

    int readVInt() throws IOException {
        byte b = readByte();
        int i = b & 0x7F;
        for (int shift = 7; (b & 0x80) != 0; shift += 7) {
            b = readByte();
            i |= (b & 0x7F) << shift;
        }
        return i;
    }
}