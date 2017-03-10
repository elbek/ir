package org.ir.tool.core.tokenizer;

/**
 * Created by ekamolid on 12/21/2016.
 */
public class RemoveSingleCharTokenizer extends Tokenizer {

    @Override
    protected boolean tokenize(TokenBuilder tokenBuilder) {
        if (tokenBuilder.length() == 1) {
            return false;
        }
        return delegateTokenize(tokenBuilder);
    }
}