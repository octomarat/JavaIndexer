package indexer.index;

import indexer.exceptions.InconsistentIndexException;
import indexer.tokenizer.Token;
import indexer.tokenizer.Tokenizer;
import indexer.utils.EncodedFile;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Concurrent version of HashFileIndex. Supports multiple readers (contains queries) and
 * one writer (add, search, remove and modify queries) at a time. Multiple search queries are
 * not supported at a time because HashFileIndex performs lazy removes on search (and so modifies
 * index state)
 *
 * @see indexer.index.HashFileIndex
 */
public class ConcurrentHashFileIndex implements FileIndex {
    private final HashFileIndex index;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public ConcurrentHashFileIndex(Tokenizer tokenizer) {
        this.index = new HashFileIndex(tokenizer);
    }

    @Override
    public List<String> search(Token tokenToFind) {
        writeLock.lock();
        try {
            return index.search(tokenToFind);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean addFile(EncodedFile encodedFile) {
        writeLock.lock();
        try {
            return index.addFile(encodedFile);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void addFiles(List<EncodedFile> files) {
        writeLock.lock();
        try {
            index.addFiles(files);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void removeFile(String filePath) {
        writeLock.lock();
        try {
            index.removeFile(filePath);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void forceRemoves() {
        writeLock.lock();
        try {
            index.forceRemoves();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean handleFileModification(EncodedFile encodedFile) throws InconsistentIndexException {
        writeLock.lock();
        try {
            return index.handleFileModification(encodedFile);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean containsFile(String filePath) {
        readLock.lock();
        try {
            return index.containsFile(filePath);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void removeDirectory(String dirPath) {
        writeLock.lock();
        try {
            index.removeDirectory(dirPath);
        } finally {
            writeLock.unlock();
        }
    }
}