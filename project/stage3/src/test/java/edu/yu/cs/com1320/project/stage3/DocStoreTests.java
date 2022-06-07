package edu.yu.cs.com1320.project.stage3;

import edu.yu.cs.com1320.project.stage3.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocStoreTests {

    private URI uri1;
    private String txt1;

    private URI uri2;
    private String txt2;

    private DocumentStoreImpl putInStore() throws IOException {
        InputStream stream1 = new ByteArrayInputStream(txt1.getBytes());
        InputStream stream2 = new ByteArrayInputStream(txt2.getBytes());
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        docStore.putDocument(stream1,uri1, DocumentStore.DocumentFormat.TXT);
        docStore.putDocument(stream2,uri2, DocumentStore.DocumentFormat.TXT);
        return docStore;
    }
    @BeforeEach
    public void initialize() throws Exception {
        this.uri1 = new URI("http://amazon.com");
        this.txt1 = "hello computer mouse water coat spring";

        this.uri2 = new URI("http://google.com");
        this.txt2 = "whatsup this is a test for my water faucet - spring is almost here";
    }

    @Test
    public void searchTest() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        InputStream stream = new ByteArrayInputStream(txt1.getBytes());
        store.putDocument(stream,uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = store.search("computer");
        assertEquals(1, list.size());
    }

    @Test
    public void searchByPrefixTest() throws URISyntaxException, IOException {
        DocumentStore store = new DocumentStoreImpl();
        URI uri1 = new URI("http://amazon.com");
        String txt1 = "hello computer mouse water coat spring";
        InputStream stream = new ByteArrayInputStream(txt1.getBytes());
        store.putDocument(stream,uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = store.searchByPrefix("sp");
        assertEquals(1, list.size());
    }

    @Test
    public void deleteAllTest() throws IOException {
        String keyword = "spring";
        DocumentStoreImpl store = putInStore();
        store.deleteAll(keyword);
        List<Document> docs = store.search(keyword);
        assertEquals(0,docs.size());
    }

    @Test
    public void deleteAllWithPrefixTest() throws IOException {
        String keyword = "THi";
        DocumentStoreImpl store = putInStore();
        List<Document> prefixList = store.searchByPrefix(keyword);
        assertEquals(1,prefixList.size());
        store.deleteAllWithPrefix(keyword);
        prefixList = store.searchByPrefix(keyword);
        assertEquals(0,prefixList.size());

    }

    @Test
    public void undoMultTest() throws IOException {
        String keyword = "spring";
        DocumentStoreImpl store = putInStore();
        List<Document> documents = store.search("spring");
        assertEquals(2,documents.size());
        store.deleteAll(keyword);
        List<Document> docs = store.search(keyword);
        assertEquals(0,docs.size());
        store.undo();
        docs = store.search(keyword);
        assertEquals(2,docs.size());
    }

}
