package org.ir.tool.core.compressor;

import org.ir.tool.core.tokenizer.TokenStreamTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Created by ekamolid on 2/3/2017.
 */
public class LZFCodecTest {

    LZFCodec lzfCodec;
    String str;

    @Before
    public void setUp() throws Exception {
        lzfCodec = new LZFCodec();
        str = new String(Files.readAllBytes(new File(TokenStreamTest.class.getResource("/org/ir/tool/core/compressor/text.txt").getPath()).toPath()));
    }

    @Test
    public void deCompress() throws Exception {
        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        byte[] output = new byte[data.length + 1];
        int len = lzfCodec.compress(data, output);
        byte[] iData = lzfCodec.deCompress(output);
        Assert.assertArrayEquals(data, iData);
    }

    @Test
    public void deCompress1() throws Exception {

    }

    @Test
    public void deCompress2() throws Exception {

    }

    @Test
    public void deCompress3() throws Exception {

    }

    @Test
    public void compress1() throws Exception {
        byte[] data = new byte[30];
        byte[] output = new byte[31];
        for (int i = 0; i < 30; i++) {
            data[i] = (byte) i;
        }
        int len = lzfCodec.compress(data, output);
        Assert.assertEquals(len, 31);
        Assert.assertEquals(output[0], 0);
    }

    @Test
    public void compress2() throws Exception {
        byte[] data = new byte[200];
        byte[] output = new byte[500];
        for (int i = 0; i < 30; i++) {
            data[i] = (byte) i;
        }
        int len = lzfCodec.compress(data, output);
        Assert.assertEquals(len, 201);
        Assert.assertEquals(output[0], 0);
    }

    @Test
    public void compress() throws Exception {

    }

}