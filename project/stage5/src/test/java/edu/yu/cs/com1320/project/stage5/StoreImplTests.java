package edu.yu.cs.com1320.project.stage5;

import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StoreImplTests {
    private URI uri1;
    private String txt1;

    private URI uri2;
    private String txt2;

    private URI uri3;
    private String txt3;

    @BeforeEach
    public void init() throws Exception {
        this.uri1 = new URI("http://google.com");
        this.txt1 = "Random Words hello world CS";

        this.uri2 = new URI("http://amazon.com");
        this.txt2 = "more random words im hungry";

        this.uri3 = new URI("http://tesla.com");
        this.txt3 = "Gut Chodesh keys papers grades";
    }

    @Test
    public void setLastUseTimeGet() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc = store.getDocument(this.uri1);
        long firstAccess = doc.getLastUseTime();
        doc = store.getDocument(this.uri1);
        long secondAccess = doc.getLastUseTime();
        doc = store.getDocument(this.uri1);
        long thirdAccess = doc.getLastUseTime();
        assertTrue(firstAccess < secondAccess);
        assertTrue(secondAccess < thirdAccess);
    }

    @Test
    public void setLastUseTimeOnPut() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        long before = System.nanoTime();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc = store.getDocument(this.uri1);
        assertTrue(before < doc.getLastUseTime());
    }

    @Test
    public void lastUseTimeOnSearch() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        long before = System.nanoTime();
        List<Document> results = store.search("random");
        Document doc = store.getDocument(this.uri1);
        assertTrue(before < doc.getLastUseTime());
    }

    @Test
    public void maxDocCount() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        assertNull(store.getDocument(this.uri1));
        assertNull(store.getDocument(this.uri2));
        assertNotNull(store.getDocument(this.uri3));
    }

}
