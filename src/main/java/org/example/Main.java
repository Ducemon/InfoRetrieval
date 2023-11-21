package org.example;


import org.apache.lucene.queryparser.classic.ParseException;
import java.io.IOException;
import java.util.Objects;
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

        // Index documents
        try (Indexer indexer = new Indexer(pathToIndex)) {
            indexer.indexDocuments(pathToDocuments);
        } catch (IOException e) {
            System.out.println("Indexing failed: " + e.getMessage());
        }
        while (true){
            // Prompt the user for a search query
            System.out.print("Enter search query or 0 if you'd like to stop querying: ");
            String query = scanner.nextLine();
            if(Objects.equals(query, String.valueOf('0'))){
                scanner.close();
                break;
            }
            try (Searcher searcher = new Searcher()) {
                searcher.search(pathToIndex, query);
            } catch (Exception e) {
                System.out.println("Searching failed: " + e.getMessage());
            }
        }

    }
}
