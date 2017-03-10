package org.ir.tool.core.tokenizer;

/**
 * Created by ekamolid on 12/21/2016.
 */
public class PositionTokenizer extends Tokenizer {
    int position = -1;

    @Override
    protected boolean tokenize(TokenBuilder tokenBuilder) {
        position++;
        return delegateTokenize(tokenBuilder);
    }

    public int getPosition() {
        return position;
    }
}