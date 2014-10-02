package indexer;

import indexer.tokenizer.WordsTokenizer;

import java.io.*;

/**
 * Created by mrx on 27.09.14.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        try (FSIndexer fsIndexer = Indexers.newSimpleFsIndexer(new WordsTokenizer(), System.out)) {
            ExampleRepl repl = new ExampleRepl(fsIndexer);
            repl.start(args.length == 0 ? null : args[0]);
        } catch (IOException e) {
            System.out.println("Index creation error: " + e.getMessage());
        }
    }
}
