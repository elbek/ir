package org.ir.tool.core.tokenizer;

/**
 * Created by ekamolid on 12/21/2016.
 */
public abstract class Tokenizer {
    protected Tokenizer delegate;
    protected TokenBuilder tokenBuilder = new TokenBuilder();

    /**
     * if this method returns false, that token from {@link TokenBuilder} should not be indexed, and tokenization should move on to look for next token.
     * @param tokenBuilder
     * @return
     */
    protected abstract boolean tokenize(TokenBuilder tokenBuilder);

    public Tokenizer setDelegate(Tokenizer delegate) {
        this.delegate = delegate;
        return this;
    }

    protected boolean delegateTokenize(TokenBuilder tokenBuilder) {
        if (delegate != null) {
            return delegate.tokenize(tokenBuilder);
        }
        return true;
    }

    boolean next() {
        return false;
    }

    void reset(String string) {
        if (delegate != null) {
            delegate.reset(string);
        }
    }

    public TokenStream getStream(String str) {
        return null;
    }

    public String getToken() {
        return tokenBuilder.toString();
    }
}
