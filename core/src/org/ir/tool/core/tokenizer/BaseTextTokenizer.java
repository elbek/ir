package org.ir.tool.core.tokenizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ekamolid on 12/21/2016.
 */
public class BaseTextTokenizer extends Tokenizer {
    private Set<Character> stoppers;
    String string;
    int pointer = 0;

    public BaseTextTokenizer() {
        stoppers = new HashSet<>();
        stoppers.addAll(Arrays.asList(
                ' ', ',', '.', '!', '?', '/', '{', '}', '<', '>', ':', ';', '\r', '\n', ')', '(', '[', ']', '|', '-', '\t', '^'
        ));
    }

    public BaseTextTokenizer(Set<Character> stoppers) {
        assert stoppers != null;
        assert stoppers.size() > 0;
        this.stoppers = stoppers;
    }


    protected boolean tokenize(TokenBuilder tokenBuilder) {
        return delegateTokenize(tokenBuilder);
    }

    @Override
    void reset(String string) {
        this.string = string;
        pointer = -1;
        super.reset(string);
    }

    boolean next() {
        if (pointer >= string.length()) {
            return false;
        }
        short sh = 0;
        tokenBuilder.reset();
        for (int i = pointer; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (stoppers.contains(ch)) {
                pointer++;
                if (tokenBuilder.length() > 0) {
                    break;
                } else {
                    continue;
                }
            }
            sh++;
            tokenBuilder.append(string.charAt(i));
            if (sh + 1 == TokenBuilder.MAX_LENGTH) {
                //TODO
            }
        }
        if (tokenBuilder.length() == 0) {
            return false;
        }
        pointer += tokenBuilder.length();
        if (!tokenize(tokenBuilder)) {
            next();
        }
        return true;
    }

    public TokenStream getStream(String str) {
        string = str;
        return new TokenStream(this);
    }
}
