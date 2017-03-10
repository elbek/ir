package org.ir.tool.core.store;

import org.ir.tool.core.ByteArraySlice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ekamolid on 12/23/2016.
 */
public class FileDataWriter extends Writer {
    private final FileOutputStream fileOutputStream;
    private final FileChannel fileChannel;
    File file;
    ByteBuffer byteBuffer;

    public FileDataWriter(File file) throws FileNotFoundException {
        this.file = file;
        fileOutputStream = new FileOutputStream(file);
        fileChannel = fileOutputStream.getChannel();
        byteBuffer = ByteBuffer.allocate(1024 * 1024);
    }

    public void writeByte(byte b) throws IOException {
        if (!byteBuffer.hasRemaining()) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }
        byteBuffer.put(b);
    }

    @Override
    public void writeBytes(byte[] b, int offset, int length) throws IOException {
        //TODO
    }

    public ByteArraySlice data() {
        //TODO
        return null;
    }
}