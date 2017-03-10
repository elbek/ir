package org.ir.tool.core.tokenizer;

/**
 * Created by ekamolid on 12/21/2016.
 */
public class LowerCaseTokenizer extends Tokenizer {

    @Override
    protected boolean tokenize(TokenBuilder tokenBuilder) {
        tokenBuilder.replace((short) 0, tokenBuilder.toString().toLowerCase());
        return delegateTokenize(tokenBuilder);
    }
}