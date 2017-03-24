package org.ir.tool.core;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ekamolid on 8/22/2016.
 */
public class Test {
    public static void main(String[] args) throws IOException {

//        writeTest("C:/tmp/out.txt");
        long st = System.currentTimeMillis();
//        readTest("C:/tmp/out.txt");
//        readTestBufferedReader("C:/tmp/out.txt");
        LineReader lineReader = new LineReader(64 * 1024, "C:/tmp/out.txt");
        int i = 0;
        String last = null;
        while (lineReader.hasNewLine()) {
            last = (lineReader.getNewLine());
        }
        System.out.println(last);
        System.out.println((System.currentTimeMillis() - st) / 1000);

    }

    static class LineReader implements Closeable {
        String newLine;
        FileInputStream inputStream;
        FileChannel fch;
        ByteBuffer buffer;
        boolean read = false;
        ByteList byteList;
        static byte[] endLineBytes = System.getProperty("line.separator").getBytes();

        LineReader(int size, String path) throws FileNotFoundException {
            inputStream = new FileInputStream(new File(path));
            fch = inputStream.getChannel();
            buffer = ByteBuffer.allocateDirect(size);
            byteList = new ByteList();
        }

        Byte last = null;
        boolean doWhile = true;

        boolean hasNewLine() throws IOException {
            if (!read) {
                buffer.clear();
                int i = fch.read(buffer);
                buffer.flip();
                doWhile = (i > 0);
                read = true;
                if (last == null) {
                    if (buffer.position() < buffer.limit()) {
                        last = buffer.get();
                        if (isNotEndLineChar(last)) {
                            byteList.add(last);
                        }
                    }
                }
            }
            while (doWhile) {
                Byte b = buffer.get();
                if (buffer.position() == buffer.limit()) {
                    buffer.clear();
                    int i = fch.read(buffer);
                    buffer.flip();
                    doWhile = (i > 0);

                    if (isEndLineChar(last, b)) {
                        //newLine = new String(byteList.getArray(), 0, byteList.length());
                        byteList.clear();
                        return true;
                    } else if (isPreEndLineChar(b)) {
                        last = b;
                    } else {
                        byteList.add(b);
                    }

                    continue;
                }

                if (isEndLineChar(last, b)) {
                    //newLine = new String(byteList.getArray(), 0, byteList.length());
                    byteList.clear();
                    return true;
                } else {
                    if (isNotEndLineChar(b)) {
                        byteList.add(b);
                    }
                    last = b;
                }
            }
            return false;
        }

        public String getNewLine() {
            return newLine;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }

        public static boolean isEndLineChar(Byte last, Byte b) {
            if (endLineBytes.length > 1) {
                if (last.equals((byte) 0xD) && b.equals((byte) 0xA)) {
                    return true;
                }
            } else {
                if (b.equals((byte) 0xA)) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isPreEndLineChar(Byte b) {
            if (endLineBytes.length > 1) {
                if (b.equals(endLineBytes[0])) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isNotEndLineChar(Byte last) {
            if (endLineBytes.length > 1) {
                if (!last.equals((byte) 0xD) && !last.equals((byte) 0xA)) {
                    return true;
                }
            } else {
                if (!last.equals((byte) 0xA)) {
                    return true;
                }
            }
            return false;
        }
    }

    static class ByteList {
        byte[] array = new byte[10];
        int i = 0;

        public void add(byte b) {
            if (i == array.length) {
                byte[] array1 = new byte[array.length * 2];
                System.arraycopy(array, 0, array1, 0, array.length);
                array = array1;
            }
            array[i++] = b;
        }

        public byte[] getArray() {
            return array;
        }

        public void clear() {
            i = 0;
        }

        public int getLength() {
            return i;
        }
    }

    static void writeTest(String path) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
        FileChannel fileChannel = fileOutputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        for (int i = 0; i < 100000000; i++) {
            byte[] bt = ("line" + i).getBytes();
            if (byteBuffer.position() + 2 + bt.length >= byteBuffer.capacity()) {
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            byteBuffer.put(bt);
            byteBuffer.put(System.getProperty("line.separator").getBytes());
        }
        if (byteBuffer.position() > 0) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
        }
        fileOutputStream.close();
    }

    static void readTest(String path) throws IOException {
        RandomAccessFile aFile = new RandomAccessFile(path, "r");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int k = 0;
        while (inChannel.read(buffer) > 0) {
            buffer.flip();
            for (int i = 0; i < buffer.limit(); i++) {
                char ch = ((char) buffer.get());
                k+=ch;
            }
            buffer.clear(); // do something with the data and clear/compact it.
        }
        System.out.println(k);
        inChannel.close();
        aFile.close();
    }

    public static String readTestBufferedReader(String path) {
        String strLine = "";
        try {
            // Get the object of DataInputStream
            InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(path)));
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                strLine = line;
            }
            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println(strLine);
        return strLine;
    }
}