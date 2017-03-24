package org.ir.tool.core.index;

import org.ir.tool.core.tokenizer.BaseTextTokenizer;
import org.ir.tool.core.tokenizer.PositionTokenizer;
import org.ir.tool.core.tokenizer.TokenStream;
import org.ir.tool.core.tree.BurstTree;

/**
 * Created by ekamolid on 2/6/2017.
 */
public class IndexBuilder {
    private final PositionTokenizer positionTokenizer;
    BurstTree burstTree;
    BaseTextTokenizer textTokenizer;
    int docId = 0;
    boolean isPositionEnabled = false;

    public IndexBuilder(BaseTextTokenizer baseTextTokenizer, boolean isPositionEnabled, PositionTokenizer positionTokenizer) {
        burstTree = new BurstTree();
        textTokenizer = baseTextTokenizer;
        this.isPositionEnabled = isPositionEnabled;
        this.positionTokenizer = positionTokenizer;
    }

    public void index(String data) {
        docId++;
        TokenStream tokenStream = textTokenizer.getStream(data); //TODO, use reset here instead of creating new token stream
        while (tokenStream.next()) {

        }
    }

}
