package org.ir.tool.core.compressor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Created by ekamolid on 1/2/17.
 */
public class LZFCodec {
    private static final int HASH_LOG = 14;
    static final int HASH_TABLE_SIZE = 1 << HASH_LOG;
    int hashTable[] = new int[HASH_TABLE_SIZE];
    static int INITIAL_HASH_VALUE = -1;
    final static int MAX_OFF = 1 << 13;
    final static int MAX_MATCH = (1 << 8) + (1 << 3);
    final static byte MAX_LITERAL_LEN = (1 << 5);
    final static byte MAX_LITERAL_LEN_1 = MAX_LITERAL_LEN - 1;

    int deCompress(byte[] data, int start, int len, byte[] output) {
        int currentPosition = start;
        int outputPosition = 0;
        while (currentPosition < len) {
            byte typeByte = (byte) ((data[currentPosition++] & 0xFF) >>> 5);
            if (typeByte == 0) {
                int tmp = (data[currentPosition - 1] & 0x1F);
                UnsafeByteArrayTool.copyUpTo32(data, currentPosition, output, outputPosition, tmp);
                outputPosition += tmp + 1;
                currentPosition += tmp + 1;
            } else if (typeByte < 7) {
                int offset = ((data[currentPosition - 1] & 0x1F) << 8 | (data[currentPosition++] & 0xff));
                UnsafeByteArrayTool.copyLong(output, outputPosition - offset - 1, output, outputPosition);
                outputPosition += typeByte + 2;
            } else {
                assert typeByte == 7;
                int matchLen = (data[currentPosition++] & 0xFF);
                int offset = ((data[currentPosition - 2] & 0x1F) << 8 | (data[currentPosition++] & 0xff));
                UnsafeByteArrayTool.copy32(output, outputPosition - offset - 1, output, outputPosition, matchLen + 9);
                outputPosition += matchLen + 9;
            }
        }
        return outputPosition;
    }

    byte[] deCompress(byte[] data, int len) {
        int tmpArr[] = {1};
        int size = CompressorUtil.readVInt(data, tmpArr);
        assert size > 0;
        byte[] output;
        if (data[0] == 0) { //not compressable
            output = new byte[data.length - 1];
            handleNotCompressable(data, output);
            return output;
        } else {
            output = new byte[size];
        }
        deCompress(data, tmpArr[0], len, output);
        return output;
    }

    private void handleNotCompressable(byte[] data, byte[] output) {
        assert data[0] == 0;
        assert output.length >= data.length - 1;
        System.arraycopy(data, 1, output, 0, data.length - 1);
    }

    int deCompress(byte[] data, int len, byte[] output) {
        int tmpArr[] = {1}; //first byte tells compressed or not compressed.
        int size = CompressorUtil.readVInt(data, tmpArr);
        assert output.length >= size;
        if (data[0] == 0) { //not compressable
            handleNotCompressable(data, output);
            return data.length - 1;
        }
        return deCompress(data, tmpArr[0], len, output);
    }

    byte[] deCompress(byte[] data) {
        return deCompress(data, data.length);
    }

    public int compress(byte[] data, byte[] output) {
        assert data.length < output.length;
        if (data.length < 64) { //don't try to compress
            output[0] = 0;
            System.arraycopy(data, 0, output, 1, data.length);
            return data.length + 1;
        }
        try {
            int len = compressUnsafe(data, output);
            if (len > data.length) { //mark it non compressable
                output[0] = 0;
                System.arraycopy(data, 0, output, 1, data.length);
                return data.length + 1;
            }
            return len;
        } catch (ArrayIndexOutOfBoundsException e) { //not compressable
            output[0] = 0;
            System.arraycopy(data, 0, output, 1, data.length);
            return data.length + 1;
        }
    }

    private int compressUnsafe(byte[] data, byte[] output) {
        reset();
        byte literalLen = 0;
        int srcLimit = data.length - 24;
        int workingPosition = 0;
        int hash;
        int currentNumber;
        currentNumber = (data[workingPosition] & 0xFF) << 8 | (data[workingPosition + 1] & 0xFF);
        output[0] = 1;//compressable
        int currentPosition = CompressorUtil.writeVInt(data.length, output, 1) + 1; //this starts from plus one for literal run len
        while (workingPosition < srcLimit) {
            currentNumber = (currentNumber << 8 | (data[workingPosition + 2] & 0xFF)) & 0x00FFFFFF;
            hash = hash(currentNumber);
            int lasPos = hashTable[hash];
            hashTable[hash] = workingPosition;

            if (lasPos != INITIAL_HASH_VALUE && workingPosition - lasPos <= MAX_OFF && workingPosition - lasPos > 2 &&
                    ((data[lasPos] & 0xFF) << 16 | (data[lasPos + 1] & 0xFF) << 8 | (data[lasPos + 2] & 0xFF)) == currentNumber) {
                if (literalLen == 0) {
                    currentPosition--; //remove allocated place for literal len
                } else {
                    assert output[currentPosition - literalLen - 1] == 0;
                    output[currentPosition - literalLen - 1] = (byte) (literalLen - 1);
                    literalLen = 0;
                }

                short matchLen = getMatchLen(data, lasPos, workingPosition);
                assert matchLen <= MAX_MATCH;
                matchLen -= 2;
                int offset = workingPosition - lasPos - 1;

                if (matchLen < 7) { //short ref
                    output[currentPosition++] = (byte) ((matchLen << 5) | (offset >>> 8));
                } else { //long ref
                    output[currentPosition++] = (byte) ((7 << 5) | (offset >>> 8));
                    output[currentPosition++] = (byte) (matchLen - 7);
                }
                output[currentPosition++] = (byte) offset;

                workingPosition += matchLen + 2;
                if (workingPosition + 1 < data.length) {
                    currentNumber = (data[workingPosition] & 0xFF) << 8 | (data[workingPosition + 1] & 0xFF);
                }
                currentPosition++; //add one for next literal len run
                continue;
            } else { //make this as literal, since not in hashtable
                if (literalLen == MAX_LITERAL_LEN) {
                    assert output[currentPosition - MAX_LITERAL_LEN - 1] == 0;
                    output[currentPosition - MAX_LITERAL_LEN - 1] = MAX_LITERAL_LEN_1;
                    currentPosition++; //add one for next literal len run
                    literalLen = 0;
                }
                output[currentPosition++] = data[workingPosition];
                literalLen++;
            }
            workingPosition++;
        }

        if (workingPosition == data.length) {
            return currentPosition - 1;
        }

        //handle remaining piece here
        if (literalLen == MAX_LITERAL_LEN) {
            assert output[currentPosition - MAX_LITERAL_LEN - 1] == 0;
            output[currentPosition - MAX_LITERAL_LEN - 1] = MAX_LITERAL_LEN_1;
            currentPosition++; //add one for next literal len run
            literalLen = 0;
        }

        while (workingPosition < data.length) {
            if (literalLen == MAX_LITERAL_LEN) {
                assert output[currentPosition - MAX_LITERAL_LEN - 1] == 0;
                output[currentPosition - MAX_LITERAL_LEN - 1] = MAX_LITERAL_LEN_1;
                currentPosition++; //add one for next literal len run
                literalLen = 0;
            }
            output[currentPosition++] = data[workingPosition++];
            literalLen++;
        }
        assert output[currentPosition - literalLen - 1] == 0;
        output[currentPosition - literalLen - 1] = (byte) (literalLen - 1);
        return currentPosition;
    }

    private int hash(int h) {
        return ((h * 57321) >> 9) & (HASH_TABLE_SIZE - 1);
    }

    public void reset() {
        Arrays.fill(hashTable, INITIAL_HASH_VALUE);
    }

    private static short getMatchLen(byte[] bytes, int pos1, int pos2) {
        short matchLen = 3; //min match len
        assert pos1 < pos2;
        while (matchLen < MAX_MATCH && pos2 + matchLen < bytes.length && pos1 + matchLen < pos2 && bytes[pos1 + matchLen] == bytes[pos2 + matchLen]) {
            matchLen++;
        }
        return matchLen;
    }

    public static void main(String[] args) throws IOException {

        LZFCodec matcher = new LZFCodec();
        RandomAccessFile accessFile = new RandomAccessFile("C:/tmp/file/enwiki-20170101-pages-meta-current7.xml-p000892914p001268691", "r");
//        RandomAccessFile accessFile = new RandomAccessFile("C:/tmp/file/book.txt", "r");
        FileChannel fileChannel = accessFile.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(500 * 1024 * 1024);
        fileChannel.read(byteBuffer);
        byteBuffer.flip();
        byte[] arr = new byte[byteBuffer.remaining()];
        byte[] output = new byte[byteBuffer.remaining()];
        byte[] deComp = new byte[byteBuffer.remaining()];
        byteBuffer.get(arr);
        int len = matcher.compress(arr, output);
        System.out.println(len / (float) arr.length);
        matcher.deCompress(output, len, deComp);
        len = matcher.compress(arr, output);
        matcher.deCompress(output, len, deComp);
        len = matcher.compress(arr, output);
        matcher.deCompress(output, len, deComp);
        len = matcher.compress(arr, output);
        matcher.deCompress(output, len, deComp);
        long l = System.currentTimeMillis();
        len = matcher.compress(arr, output);
        System.out.println(System.currentTimeMillis() - l);
        l = System.currentTimeMillis();
        len = matcher.deCompress(output, len, deComp);
        System.out.println(System.currentTimeMillis() - l);
        int i = 0;
        while (i < arr.length) {
            if (deComp[i] != arr[i]) {
                System.out.println(i);
            }
            i++;
        }
    }
}