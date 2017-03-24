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
    public void deCompress1() throws Exception {

    }

    @Test
    public void deCompress2() throws Exception {

    }

    @Test
    public void deCompress3() throws Exception {

    }


    @Test
    public void compress() throws Exception {

    }

}