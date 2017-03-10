package org.ir.tool.core.tokenizer;

/**
 * Created by ekamolid on 12/21/2016.
 */
public class TokenStream {
    Tokenizer tokenizer;

    public TokenStream(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    void reset(String string) {
        tokenizer.reset(string);
    }

    public boolean next() {
        return tokenizer.next();
    }

    public static void main(String[] args) {
        BaseTextTokenizer textTokenizer = new BaseTextTokenizer();

        LowerCaseTokenizer lowerCaseTokenizer = new LowerCaseTokenizer();
        textTokenizer.setDelegate(lowerCaseTokenizer);

        StopWordsTokenizer stopWordsTokenizer = new StopWordsTokenizer();
        lowerCaseTokenizer.setDelegate(stopWordsTokenizer);

        PositionTokenizer positionTokenizer = new PositionTokenizer();
        stopWordsTokenizer.setDelegate(positionTokenizer);

        TokenStream tokenStream = textTokenizer.getStream("Hello and or the a is are world, yana-hello?nima gap!!!!qayerda?kimga...nima  heylo       @ sanga- manga, qalaysan, nima kk, ishlar zurmi/,/nima asjkdhasd alksdklas dlaks d adka sdlka sdlkah3qh324b234 324 234 asdfj df sdf osfa zn.v mz.x/cz,.xmvn.,xzmbzxmvcn/.xcv,bnmfz/xn");
        while (tokenStream.next()) {
            System.out.println(textTokenizer.getToken() + " = " + positionTokenizer.getPosition());
        }
    }
}