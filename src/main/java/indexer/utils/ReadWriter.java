package indexer.utils;

import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by mrx on 04.10.14.
 */
public interface ReadWriter extends AutoCloseable {
    public String interact() throws IOException;
    public String readLine() throws IOException;
    public void println(String msg) throws IOException;
    public void print(String msg) throws IOException;
    public void close() throws Exception;
}