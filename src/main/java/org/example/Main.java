package org.example;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        String pathToDocuments;
        String pathToIndex;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the absolute/relative path to the documents (leave blank if you prefer the default): ");
        String input = scanner.nextLine();
        if (input.isEmpty()) {
            pathToDocuments = "docomente";
        }else{
            pathToDocuments = input;
        }
        System.out.print("Enter the absolute/relative path to the index (leave blank if you prefer the default): ");
        input = scanner.nextLine();
        if (input.isEmpty()) {
            pathToIndex = "IndexusInversus";
        }else{
            pathToIndex = input;
        }

        // Index documents...
        try (Indexer indexer = new Indexer(pathToIndex)) {
            indexer.indexDocuments(pathToDocuments);
        } catch (IOException e) {
            System.out.println("Indexing failed: " + e.getMessage());
        }

        // Prompt the user for a search query
        System.out.print("Enter search query: ");
        String query = scanner.nextLine();
        try (Searcher searcher = new Searcher()) {
            searcher.search(pathToIndex, query);
        } catch (Exception e) {
            System.out.println("Searching failed: " + e.getMessage());
        }
        scanner.close();

    }
}
