package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.tartarus.snowball.ext.RomanianStemmer;
import org.apache.lucene.analysis.icu.ICUFoldingFilter;

import java.text.Normalizer;
import java.util.regex.Pattern;


public class ExtendedRomanianAnalyzer extends Analyzer {
    private final CharArraySet stopwords;
    private final CharArraySet stemExclusionSet;

    public static String removeDiacritics(String str) {
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public static CharArraySet stripDiacritics(CharArraySet originalSet) {
        CharArraySet strippedSet = new CharArraySet(originalSet.size(), true);
        for (Object entry : originalSet) {
            if (entry instanceof char[]) {
                String word = new String((char[]) entry);
                String strippedWord = removeDiacritics(word);
                strippedSet.add(strippedWord);
            }
        }
        return strippedSet;
    }


    // Constructor
    public ExtendedRomanianAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet) {

        CharArraySet strippedStopWords = stripDiacritics(stopwords);

        // Combine original and stripped stopwords
        this.stopwords = new CharArraySet(stopwords.size() + strippedStopWords.size(), true);
        this.stopwords.addAll(stopwords);
        this.stopwords.addAll(strippedStopWords);

        this.stemExclusionSet = stemExclusionSet;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(source);
        result = new CustomCharReplaceFilter(result); // Add custom filter
        result = new StopFilter(result, stopwords);
        if (!stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter(result, stemExclusionSet);
        }
        result = new ICUFoldingFilter(result); // Add ICU folding filter
        result = new SnowballFilter(result, new RomanianStemmer());
        return new TokenStreamComponents(source, result);
    }
}
