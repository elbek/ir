package org.ir.tool.core.tokenizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ekamolid on 12/21/2016.
 */
public class StopWordsTokenizer extends Tokenizer {

    static Set<String> STOP_WORDS = new HashSet<>(Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with"));
    Set<String> stopWords;

    public StopWordsTokenizer() {
        stopWords = STOP_WORDS;
    }

    public StopWordsTokenizer(Set<String> stopWords) {
        assert stopWords != null;
        this.stopWords = stopWords;
    }

    @Override
    protected boolean tokenize(TokenBuilder tokenBuilder) {
        if (stopWords.contains(tokenBuilder.toString())) {
            return false;
        }
        return delegateTokenize(tokenBuilder);
    }
}