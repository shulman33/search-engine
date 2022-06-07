package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.Utils;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;

    //variables to hold possible values for doc3
    private URI uri3;
    private String txt3;

    //variables to hold possible values for doc4
    private URI uri4;
    private String txt4;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "the text of doc1, in plain text. No fancy file format - just plain old String. Computer. Headphones.";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text for doc2. A plain old String.";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "the text of doc3, this is";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "This is the text of doc4";
    }
    private DocumentStore getStoreWithTextAdded() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
        return store;
    }

    private DocumentStore getStoreWithBinaryAdded() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
        return store;
    }

    @Test
    public void stage3Search() throws IOException {
        List<Document> results = this.search(this.getStoreWithTextAdded(),"plain",2);
        assertTrue(this.containsDocWithUri(results,this.uri1),"Result set should've included " + this.uri1);
        assertTrue(this.containsDocWithUri(results,this.uri2),"Result set should've included " + this.uri2);
        this.search(this.getStoreWithTextAdded(),"missing",0);
    }
    @Test
    public void stage3SearchBinary() throws IOException {
        List<Document> results = this.search(this.getStoreWithBinaryAdded(),"plain",2);
        assertTrue(this.containsDocWithUri(results,this.uri1),"Result set should've included " + this.uri1);
        assertTrue(this.containsDocWithUri(results,this.uri2),"Result set should've included " + this.uri2);
        this.search(this.getStoreWithBinaryAdded(),"missing",0);
    }

    @Test
    public void stage3DeleteAllTxt() throws IOException {
        DocumentStore store = this.getStoreWithTextAdded();
        String keyword = "plain";
        store.deleteAll(keyword);
        List<Document> results = store.search(keyword);
        URI[] absent = {this.uri1,this.uri2,this.uri3,this.uri4};
        URI[] present = new URI[0];
        this.checkContents(results,present,absent);
    }

    @Test
    public void stage3DeleteAllBinary() throws IOException {
        DocumentStore store = this.getStoreWithBinaryAdded();
        String keyword = "Headphones";
        store.deleteAll(keyword);
        List<Document> results = store.search(keyword);
        URI[] absent = {this.uri1,this.uri2,this.uri3,this.uri4};
        URI[] present = new URI[0];
        this.checkContents(results,present,absent);
    }

    @Test
    public void stage3SearchTxtByPrefix() throws IOException {
        this.stage3SearchByPrefix(this.getStoreWithTextAdded());
    }

    @Test
    public void stage3SearchBinaryByPrefix() throws IOException {
        this.stage3SearchByPrefix(this.getStoreWithBinaryAdded());
    }

    @Test
    public void stage3DeleteAllWithPrefix() throws IOException {
        DocumentStore store = this.getStoreWithTextAdded();
        //delete all starting with thi
        store.deleteAllWithPrefix("thi");
        List<Document> results = store.searchByPrefix("thi");
        assertEquals(0,results.size(),"search should've returned 0 results");
        URI[] present = new URI[0];
        URI[] absent = {this.uri1,this.uri2,this.uri3,this.uri4};
        this.checkContents(results,present,absent);

    }

    private List<Document> search(DocumentStore store, String keyword, int expectedMatches){
        List<Document> results = store.search(keyword);
        assertEquals(expectedMatches,results.size(),"expected " + expectedMatches + " matches, received " + results.size());
        return results;
    }

    private boolean containsDocWithUri(List<Document> docs, URI uri){
        for(Document doc : docs){
            if(doc.getKey().equals(uri)){
                return true;
            }
        }
        return false;
    }

    private void checkContents(List<Document> results, URI[] present, URI[] absent){
        for(URI uri : present){
            if(!this.containsDocWithUri(results, uri)){
                fail(uri + " should be in the result set, but is not");
            }
        }
        for(URI uri : absent){
            if(this.containsDocWithUri(results, uri)){
                fail(uri + " should NOT be in the result set, but is");
            }
        }
    }

    private void stage3SearchByPrefix(DocumentStore store){
        List<Document> results = store.searchByPrefix("str");
        assertEquals(2,results.size(),"expected 2 matches, received " + results.size());
        URI[] present = {this.uri1,this.uri2};
        URI[] absent = {this.uri3,this.uri4};
        this.checkContents(results,present,absent);

        results = store.searchByPrefix("comp");
        assertEquals(1,results.size(),"expected 1 match, received " + results.size());
        URI[] present2 = {this.uri1};
        URI[] absent2 = {this.uri3,this.uri4,this.uri2};
        this.checkContents(results,present2,absent2);

        results = store.searchByPrefix("doc2");
        assertEquals(1,results.size(),"expected 1 match, received " + results.size());
        URI[] present3 = {this.uri2};
        URI[] absent3 = {this.uri3,this.uri4,this.uri1};
        this.checkContents(results,present3,absent3);

        results = store.searchByPrefix("blah");
        assertEquals(0,results.size(),"expected 0 match, received " + results.size());            
    }

    //stage 1 tests
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