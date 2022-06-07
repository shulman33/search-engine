package edu.yu.cs.com1320.project.stage5;

import java.io.IOException;

public interface PersistenceManager<Key,Value> {
    void serialize(Key key, Value val) throws IOException;
    Value deserialize(Key key) throws IOException;
    /**
     * delete the file stored on disk that corresponds to the given key
     * @param key
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    boolean delete(Key key) throws IOException;
}
