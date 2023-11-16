package org.example;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Indexer implements AutoCloseable{

    private IndexWriter writer;


    private String parseFileUsingTika(Path path) {
        Tika tika = new Tika();
        try {
            return tika.parseToString(path.toFile());
        } catch (IOException | TikaException e) {
            System.err.println("Error parsing file " + path + ": " + e.getMessage());
            return "";
        }
    }

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        IndexWriterConfig config = new IndexWriterConfig(new ExtendedRomanianAnalyzer(RomanianAnalyzer.getDefaultStopSet(), new CharArraySet(0, true)));
        writer = new IndexWriter(dir, config);
    }

    public void close() throws IOException {
        writer.close();
    }

    public void indexDocuments(String docsPath) throws IOException {
        Files.walkFileTree(Paths.get(docsPath), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                indexFile(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void indexFile(Path file) throws IOException {
        String filePath = file.toString();
        System.out.println("Processing file: " + filePath); // Debugging line

        String fileContent = parseFileUsingTika(file);



        Document doc = new Document();
        doc.add(new StringField("path", filePath, Field.Store.YES));
        doc.add(new TextField("contents", fileContent, Field.Store.NO));


        writer.updateDocument(new Term("path", filePath), doc);
    }


    public static void main(String[] args) {
        // Example usage

        CharArraySet stopWords = RomanianAnalyzer.getDefaultStopSet();
        System.out.println("Stopwords used by RomanianAnalyzer:");
        for (Object stopWord : stopWords) {
            System.out.println(new String((char[]) stopWord));
        }
        try {
            final String pathToDocuments = "docomente";
            final String pathToIndex = "IndexusInversus";
            // Index documents...
            Indexer indexer = new Indexer(pathToIndex);
            indexer.indexDocuments(pathToDocuments);
            indexer.close();
            // Close the indexer
            indexer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}