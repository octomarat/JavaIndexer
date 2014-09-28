package indexer.index;

import indexer.TmpFsCreator;
import indexer.tokenizer.Tokenizer;
import indexer.tokenizer.Word;
import indexer.tokenizer.WordsTokenizer;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HashFileIndexTest extends TmpFsCreator {
    private Tokenizer tokenizer = new WordsTokenizer();

    @Test
    public void testAddAndSearch() throws Exception {
        FileIndex hashFileIndex = new HashFileIndex(tokenizer);
        hashFileIndex.addFile(file1.getAbsolutePath());

        assertTrue(hashFileIndex.search(new Word("file1")).size() == 1);
        assertTrue(hashFileIndex.search(new Word("content")).size() == 1);
        assertTrue(hashFileIndex.search(new Word("notInFile")).size() == 0);
    }

    @Test
    public void testContainsFile() throws Exception {
        FileIndex hashFileIndex = new HashFileIndex(tokenizer);
        hashFileIndex.addFile(file1.getAbsolutePath());

        assertTrue(hashFileIndex.containsFile(file1.getAbsolutePath()));
        assertTrue(!hashFileIndex.containsFile(file2.getAbsolutePath()));
    }

    @Test
    public void testRemoveFileReadingDisk() throws Exception {
        FileIndex hashFileIndex = new HashFileIndex(tokenizer);
        hashFileIndex.addFile(dir2SubFile1.getAbsolutePath());

        assertTrue(hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 1);

        hashFileIndex.removeFileReadingDisk(dir2SubFile1.getAbsolutePath());

        assertTrue(!hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 0);
    }

    @Test
    public void testRemoveFileIteratingAll() throws Exception {
        FileIndex hashFileIndex = new HashFileIndex(tokenizer);
        hashFileIndex.addFile(dir2SubFile1.getAbsolutePath());

        assertTrue(hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 1);

        if(!dir2SubFile1.delete()) {
            fail("Manual deleting problem occurred");
        }
        hashFileIndex.removeFileIteratingAll(dir2SubFile1.getAbsolutePath());

        assertTrue(!hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 0);
    }

    @Test
    public void testHandleFileModificationAppend() throws Exception {
        FileIndex hashFileIndex = new HashFileIndex(tokenizer);
        hashFileIndex.addFile(dir2SubFile1.getAbsolutePath());

        assertTrue(hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 1);

        if(!appendTextToFile(dir2SubFile1, " appendix")) {
            fail("append text failed");
        }
        hashFileIndex.handleFileModification(dir2SubFile1.getAbsolutePath());

        assertTrue(hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 1);
        assertTrue(hashFileIndex.search(new Word("appendix")).size() == 1);
    }

    @Test
    public void testHandleFileModificationReplace() throws Exception {
        FileIndex hashFileIndex = new HashFileIndex(tokenizer);
        hashFileIndex.addFile(dir2SubFile1.getAbsolutePath());

        assertTrue(hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 1);

        if(!rewriteFileWithText(dir2SubFile1, "replacement")) {
            fail("rewrite text failed");
        }
        hashFileIndex.handleFileModification(dir2SubFile1.getAbsolutePath());

        assertTrue(hashFileIndex.containsFile(dir2SubFile1.getAbsolutePath()));
        assertTrue(hashFileIndex.search(new Word("Lorem")).size() == 0);
        assertTrue(hashFileIndex.search(new Word("ipsum")).size() == 0);
        assertTrue(hashFileIndex.search(new Word("replacement")).size() == 1);
    }
}