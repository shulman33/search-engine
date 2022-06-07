package edu.yu.cs.com1320.project.stage1;

import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    String txt2;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This is the text of doc1, in plain text. No fancy file format - just plain old String";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2. A plain old String.";
    }

    @Test
    public void testPutBinaryDocumentNoPreviousDocAtURI() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
    }

    @Test
    public void testPutTxtDocumentNoPreviousDocAtURI() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
    }

    @Test
    public void testPutDocumentWithNullArguments() throws IOException{
        DocumentStore store = new DocumentStoreImpl();
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), null, DocumentStore.DocumentFormat.TXT);
            fail("null URI should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
        try {
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, null);
            fail("null format should've thrown IllegalArgumentException");
        }catch(IllegalArgumentException e){}
    }

    @Test
    public void testPutNewVersionOfDocumentBinary() throws IOException {
        //put the first version
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
        Document doc1 = store.getDocument(this.uri1);
        assertArrayEquals(this.txt1.getBytes(),doc1.getDocumentBinaryData(),"failed to return correct binary text");

        //put the second version, testing both return value of put and see if it gets the correct text
        int expected = doc1.hashCode();
        returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);

        assertEquals(expected, returned,"should return hashcode of the old document");
        assertArrayEquals(this.txt2.getBytes(),store.getDocument(this.uri1).getDocumentBinaryData(),"failed to return correct data");
    }

    @Test
    public void testPutNewVersionOfDocumentTxt() throws IOException {
        //put the first version
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");

        //put the second version, testing both return value of put and see if it gets the correct text
        returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(Utils.calculateHashCode(this.uri1, this.txt1,null) == returned,"should return hashcode of old text");
        assertEquals(this.txt2,store.getDocument(this.uri1).getDocumentTxt(),"failed to return correct text");
    }

    @Test
    public void testGetTxtDoc() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertEquals(this.txt1,store.getDocument(this.uri1).getDocumentTxt(),"did not return a doc with the correct text");
    }

    @Test
    public void testGetTxtDocAsBinary() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(returned == 0);
        assertNull(store.getDocument(this.uri1).getDocumentBinaryData(),"a text doc should return null for binary");
    }

    @Test
    public void testGetBinaryDocAsBinary() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
        assertArrayEquals(this.txt2.getBytes(),store.getDocument(this.uri2).getDocumentBinaryData(),"failed to return correct binary array");
    }

    @Test
    public void testGetBinaryDocAsTxt() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        int returned = store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(returned == 0);
        assertNull(store.getDocument(this.uri2).getDocumentTxt(),"binary doc should return null for text");
    }

    @Test
    public void testDeleteDoc() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.deleteDocument(this.uri1);
        assertNull(store.getDocument(this.uri1),"calling get on URI from which doc was deleted should've returned null");
    }

    @Test
    public void testDeleteDocReturnValue() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        //should return true when deleting a document
        assertEquals(true,store.deleteDocument(this.uri1),"failed to return true when deleting a document");
        //should return false if I try to delete the same doc again
        assertEquals(false,store.deleteDocument(this.uri1),"failed to return false when trying to delete that which was already deleted");
        //should return false if I try to delete something that was never there to begin with
        assertEquals(false,store.deleteDocument(this.uri2),"failed to return false when trying to delete that which was never there to begin with");
    }
}