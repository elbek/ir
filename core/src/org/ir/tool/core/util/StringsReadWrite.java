package org.ir.tool.core.util;

import org.ir.tool.core.Worker;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * Created by ekamolid on 12/5/2016.
 */
public class StringsReadWrite {
    private FileOutputStream fileOutputStream;
    private FileChannel fileChannel;
    private ByteBuffer byteBuffer;
    private RandomAccessFile aFile;
    private Random random = new Random();

    public StringsReadWrite(String path, int bufferSize, boolean isRead) throws FileNotFoundException {
        if (isRead) {
            aFile = new RandomAccessFile(path, "rw");
            fileChannel = aFile.getChannel();
        } else {
            fileOutputStream = new FileOutputStream(new File(path));
            fileChannel = fileOutputStream.getChannel();
        }
        byteBuffer = ByteBuffer.allocate(bufferSize);
    }

    /**
     * @param size
     * @param min
     * @param max
     * @param randomStringEnum
     * @throws IOException
     */
    public void generate(int size, byte min, byte max, RandomUtil.RandomStringEnum randomStringEnum) throws IOException {
        for (int i = 0; i < size; i++) {
            int len = random.nextInt((max - min) + 1) + min;
            String string = RandomUtil.generateRandomString(len, randomStringEnum);
            byte[] bytes = string.getBytes();
            if (byteBuffer.position() + bytes.length + 1 >= byteBuffer.capacity()) {
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            byteBuffer.put((byte) len);
            byteBuffer.put(bytes);
        }
        if (byteBuffer.position() > 0) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
        }
    }

    /**
     * 0000 0000-0000 007F | 0xxxxxxx
     * 0000 0080-0000 07FF | 110xxxxx 10xxxxxx
     * 0000 0800-0000 FFFF | 1110xxxx 10xxxxxx 10xxxxxx
     * 0001 0000-0010 FFFF | 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     *
     * @param size
     * @throws IOException
     */
    public void read(int size, Worker worker) throws IOException {
        read(fileChannel, byteBuffer);
        byte bytes[] = new byte[40]; //10 utf chars can take up to 40 chars
        int currentPointer;
        int bytesCount;

        for (int j = 0; j < size; j++) {
            currentPointer = 0;
            bytesCount = 0;
            if (!byteBuffer.hasRemaining()) {
                if (read(fileChannel, byteBuffer) <= 0) {
                    return;
                }
            }
            byte len = byteBuffer.get();
            for (int i = 0; i < len; i++) {
                if (!byteBuffer.hasRemaining()) {
                    if (read(fileChannel, byteBuffer) <= 0) {
                        return;
                    }
                }
                byte b = byteBuffer.get();
                bytes[currentPointer++] = b;
                bytesCount++;

                if ((b & 0xF0) == 0xF0) {
                    if (!read(fileChannel, byteBuffer, bytes, currentPointer, 3)) {
                        return;
                    }
                    bytesCount += 3;
                    currentPointer += 3;
                } else if ((b & 0xE0) == 0xE0) {
                    if (!read(fileChannel, byteBuffer, bytes, currentPointer, 2)) {
                        return;
                    }
                    bytesCount += 2;
                    currentPointer += 2;
                } else if ((b & 0xC0) == 0xC0) {
                    if (!read(fileChannel, byteBuffer, bytes, currentPointer, 1)) {
                        return;
                    }
                    bytesCount += 1;
                    currentPointer += 1;
                }
            }
            if (worker != null) {
                worker.doWork(bytes, 0, bytesCount);
            }
        }
    }

    private boolean read(FileChannel fileChannel, ByteBuffer byteBuffer, byte[] bytes, int offset, int count) throws IOException {
        for (int i = offset; i < offset + count; i++) {
            if (!byteBuffer.hasRemaining()) {
                if (read(fileChannel, byteBuffer) <= 0) {
                    return false;
                }
            }
            bytes[i] = byteBuffer.get();
        }
        return true;
    }

    private int read(FileChannel fileChannel, ByteBuffer byteBuffer) throws IOException {
        byteBuffer.clear();
        int read = fileChannel.read(byteBuffer);
        if (read > 0) {
            byteBuffer.flip();
        }
        return read;
    }

    public void close() throws IOException {
        if (aFile != null) {
            aFile.close();
        } else {
            fileOutputStream.close();
        }
    }


}