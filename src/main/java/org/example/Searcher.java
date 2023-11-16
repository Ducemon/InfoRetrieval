package org.example;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;

public class Searcher implements AutoCloseable{
    public void search(String indexDir, String queryStr) throws Exception {
        // Searching
        ExtendedRomanianAnalyzer analyzer = new ExtendedRomanianAnalyzer(RomanianAnalyzer.getDefaultStopSet(), new CharArraySet(0, true));
        // the "contents" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser("contents", analyzer).parse(queryStr);
        System.out.println(q);

        // 3. search
        int hitsPerPage = 10;
        try (IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)))) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("path"));
            }

        }
    }

    public static void main(String[] args) {
        try {
            final String pathToIndex = "IndexusInversus";
            Searcher searcher = new Searcher();
            searcher.search(pathToIndex, "si");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {

    }
}
