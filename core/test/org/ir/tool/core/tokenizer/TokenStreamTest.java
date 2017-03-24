package org.ir.tool.core.tokenizer;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by ekamolid on 12/22/2016.
 */
public class TokenStreamTest {
    String str;

    @Before
    public void setUp() throws Exception {
        str = new String(Files.readAllBytes(new File(TokenStreamTest.class.getResource("/org/ir/tool/core/tokenizer/text.txt").getPath()).toPath()));

    }

    @Test
    public void reset() throws Exception {

    }

    @Test
    public void next() throws Exception {
        BaseTextTokenizer textTokenizer = new BaseTextTokenizer();

        RemoveSingleCharTokenizer removeSingleCharTokenizer = new RemoveSingleCharTokenizer();
        textTokenizer.setDelegate(removeSingleCharTokenizer);

        LowerCaseTokenizer lowerCaseTokenizer = new LowerCaseTokenizer();
        removeSingleCharTokenizer.setDelegate(lowerCaseTokenizer);

        StopWordsTokenizer stopWordsTokenizer = new StopWordsTokenizer();
        lowerCaseTokenizer.setDelegate(stopWordsTokenizer);

        PositionTokenizer positionTokenizer = new PositionTokenizer();
        stopWordsTokenizer.setDelegate(positionTokenizer);

        TokenStream tokenStream = textTokenizer.getStream(str);
        while (tokenStream.next()) {
            System.out.println(textTokenizer.getToken() + " = " + positionTokenizer.getPosition());
        }
    }
}