package org.example;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.io.IOException;

public class CustomCharReplaceFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    protected CustomCharReplaceFilter(TokenStream input) {
        super(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            String term = new String(termAtt.buffer(), 0, termAtt.length());
            term = term.replace('ș', 'ş')
                    .replace('ț', 'ţ');
            termAtt.setEmpty().append(term);
            return true;
        } else {
            return false;
        }
    }
}
