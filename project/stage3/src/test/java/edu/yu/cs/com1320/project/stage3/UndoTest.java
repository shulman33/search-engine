package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UndoTest {

    //variables to hold possible values for doc1
    private URI uri1;
    private String txt1;

    //variables to hold possible values for doc2
    private URI uri2;
    private String txt2;

    //variables to hold possible values for doc2
    private URI uri3;
    private String txt3;

    //variables to hold possible values for doc2
    private URI uri4;
    private String txt4;

    private DocumentStoreImpl createStoreAndPutOne() throws IOException {
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        ByteArrayInputStream bas1 = new ByteArrayInputStream(this.txt1.getBytes());
        dsi.putDocument(bas1,this.uri1, DocumentStore.DocumentFormat.TXT);
        return dsi;
    }

    private DocumentStoreImpl createStoreAndPutAll() throws IOException {
        DocumentStoreImpl dsi = new DocumentStoreImpl();
        //doc1
        ByteArrayInputStream bas = new ByteArrayInputStream(this.txt1.getBytes());
        dsi.putDocument(bas,this.uri1, DocumentStore.DocumentFormat.TXT);
        //doc2
        bas = new ByteArrayInputStream(this.txt2.getBytes());
        dsi.putDocument(bas,this.uri2, DocumentStore.DocumentFormat.TXT);
        //doc3
        bas = new ByteArrayInputStream(this.txt3.getBytes());
        dsi.putDocument(bas,this.uri3, DocumentStore.DocumentFormat.TXT);
        //doc4
        bas = new ByteArrayInputStream(this.txt4.getBytes());
        dsi.putDocument(bas,this.uri4, DocumentStore.DocumentFormat.TXT);
        return dsi;
    }

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "keyword1 This is the text of doc1, in plain text. No fancy file format - just plain old String";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "keyword1 Text for doc2. A plain old String.";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "keyword123 This is the text of doc3 - doc doc goose";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "keyword12 doc4: how much wood would a woodchuck chuck...";
    }

    //undo most recent when most recent deleted multiple documents
    @Test
    public void stage3PlainUndoThatImpactsMultiple() throws IOException {
        String keyword1 = "keyword1";
        //step 1: put all documents in
        DocumentStoreImpl dsi = createStoreAndPutAll();

        //step 2: delete multiple docs that have the same keyword
        dsi.deleteAll(keyword1);            
        //make sure they are gone - search by keyword
        List<Document> results = dsi.search(keyword1);
        assertEquals(0,results.size(),"docs with keyword1 should be gone - List size should be 0");
        //make sure they are gone by URI - use protected method
        assertNull(dsi.getDocument(this.uri1),"document with URI " + this.uri1 + "should've been deleted");
        assertNull(dsi.getDocument(this.uri2),"document with URI " + this.uri2 + "should've been deleted");
        //make sure other docs are still there - use protected method
        assertNotNull(dsi.getDocument(this.uri3),"document with URI " + this.uri3 + "should NOT have been deleted");
        assertNotNull(dsi.getDocument(this.uri4),"document with URI " + this.uri4 + "should NOT have been deleted");

        //step 3: undo the last command, i.e. the delete
        dsi.undo();

        //check that they are back by keyword
        results = dsi.search(keyword1);
        assertEquals(2,results.size(),"docs with keyword1 should be back - List size should be 2");
        //check that they are back by URI - use protected method
        assertNotNull(dsi.getDocument(this.uri1),"document with URI " + this.uri1 + "should be back");
        assertNotNull(dsi.getDocument(this.uri2),"document with URI " + this.uri2 + "should be back");
        //make sure the other docs are still unaffected - by protected method
        assertNotNull(dsi.getDocument(this.uri3),"document with URI " + this.uri3 + "should NOT have been deleted");
        assertNotNull(dsi.getDocument(this.uri4),"document with URI " + this.uri4 + "should NOT have been deleted");
    }
    //undo by URI which is part of most recent which deleted multiple documents
    @Test
    public void stage3UndoByURIThatImpactsOne() throws IOException {
        String keyword1 = "keyword1";
        //step 1: put all documents in
        DocumentStoreImpl dsi = createStoreAndPutAll();

        //step 2: delete multiple docs that have the same keyword        
        dsi.deleteAll(keyword1);
        //make sure they are gone - search by keyword
        List<Document> results = dsi.search(keyword1);
        assertEquals(0,results.size(),"docs with keyword1 should be gone - List size should be 0");
        //make sure they are gone by URI - use protected method
        assertNull(dsi.getDocument(this.uri1),"document with URI " + this.uri1 + "should've been deleted");
        assertNull(dsi.getDocument(this.uri2),"document with URI " + this.uri2 + "should've been deleted");
        //make sure other docs are still there - use protected method
        assertNotNull(dsi.getDocument(this.uri3),"document with URI " + this.uri3 + "should NOT have been deleted");
        assertNotNull(dsi.getDocument(this.uri4),"document with URI " + this.uri4 + "should NOT have been deleted");

        //step 3: undo the deletion of doc 2
        dsi.undo(this.uri2);

        //check that doc2 is back by keyword
        results = dsi.search(keyword1);
        assertEquals(1,results.size(),"doc2 should be back - List size should be 1");
        assertEquals(this.txt2,results.get(0).getDocumentTxt(),"doc2 should be back");
        //check that doc2 is back by URI but doc 1 is still null- use protected method
        assertNotNull(dsi.getDocument(this.uri2),"document with URI " + this.uri2 + "should be back");
        assertNull(dsi.getDocument(this.uri1),"document with URI " + this.uri1 + "should still be null");
        //make sure the other docs are still unaffected - by protected method
        assertNotNull(dsi.getDocument(this.uri3),"document with URI " + this.uri3 + "should NOT have been deleted");
        assertNotNull(dsi.getDocument(this.uri4),"document with URI " + this.uri4 + "should NOT have been deleted");
    }
    //undo by URI which is EARLIER than most recent
    @Test
    public void stage3UndoByURIThatImpactsEarlierThanLast() throws IOException {
        String prefix = "keyword12";
        String keyword = "keyword1";
        //step 1: put all documents in
        DocumentStoreImpl dsi = createStoreAndPutAll();

        //step 2: delete multiple docs that have the same prefix, and then delete others by keyword
        dsi.deleteAllWithPrefix(prefix);
        dsi.deleteAll(keyword);
        //make sure they are gone - search by keyword
        List<Document> results = dsi.search(keyword);
        assertEquals(0,results.size(),"docs with keyword1 should be gone - List size should be 0");
        results = dsi.searchByPrefix(prefix);
        assertEquals(0,results.size(),"docs with prefix " + prefix + " should be gone - List size should be 0");
        //make sure they are gone by URI - use protected method
        assertNull(dsi.getDocument(this.uri1),"document with URI " + this.uri1 + "should've been deleted");
        assertNull(dsi.getDocument(this.uri2),"document with URI " + this.uri2 + "should've been deleted");
        assertNull(dsi.getDocument(this.uri3),"document with URI " + this.uri3 + "should've been deleted");
        assertNull(dsi.getDocument(this.uri4),"document with URI " + this.uri4 + "should've been deleted");

        //step 3: undo the deletion of doc 3
        dsi.undo(this.uri3);

        //check that doc3 is back by keyword
        results = dsi.search("keyword123");
        assertEquals(1,results.size(),"doc3 should be back - List size should be 1");
        //check that doc3 is back but none of the others are back
        assertNotNull(dsi.getDocument(this.uri3),"document with URI " + this.uri3 + "should be back");
        assertNull(dsi.getDocument(this.uri1),"document with URI " + this.uri1 + "should still be null");
        assertNull(dsi.getDocument(this.uri2),"document with URI " + this.uri2 + "should NOT have been deleted");
        assertNull(dsi.getDocument(this.uri4),"document with URI " + this.uri4 + "should NOT have been deleted");
    }

    //stage 2 tests

    @Test
    public void undoAfterOnePut() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutOne();
        //undo after putting only one doc
        Document doc1 = new DocumentImpl(this.uri1, this.txt1);
        Document returned1 = dsi.getDocument(this.uri1);
        assertNotNull(returned1,"Did not get a document back after putting it in");
        assertEquals(doc1.getKey(),returned1.getKey(),"Did not get doc1 back");
        dsi.undo();
        returned1 = dsi.getDocument(this.uri1);
        assertNull(returned1,"Put was undone - should have been null");
        try {
            dsi.undo();
            fail("no documents - should've thrown IllegalStateException");
        }catch(IllegalStateException e){}
    }

    @Test
    public void undoWhenEmptyShouldThrow() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutOne();
        //undo after putting only one doc
        dsi.undo();
        assertThrows(IllegalStateException.class,()->{dsi.undo();},"undo should throw an exception when there's nothing to undo");

    }

    @Test
    public void undoByURIWhenEmptyShouldThrow() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutOne();
        //undo after putting only one doc
        dsi.undo();
        assertThrows(IllegalStateException.class,()->{dsi.undo(this.uri1);},"undo by uri should throw an exception when there's nothing to undo");
    }

    @Test
    public void undoAfterMultiplePuts() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        //undo put 4 - test before and after
        Document returned = dsi.getDocument(this.uri4);
        assertEquals(this.uri4,returned.getKey(),"should've returned doc with uri4");
        dsi.undo();
        assertNull(dsi.getDocument(this.uri4),"should've been null - put doc4 was undone");

        //undo put 3 - test before and after
        returned = dsi.getDocument(this.uri3);
        assertEquals(this.uri3,returned.getKey(),"should've returned doc with uri3");
        dsi.undo();
        assertNull(dsi.getDocument(this.uri3),"should've been null - put doc3 was undone");

        //undo put 2 - test before and after
        returned = dsi.getDocument(this.uri2);
        assertEquals(this.uri2,returned.getKey(),"should've returned doc with uri3");
        dsi.undo();
        assertNull(dsi.getDocument(this.uri2),"should've been null - put doc2 was undone");

        //undo put 1 - test before and after
        returned = dsi.getDocument(this.uri1);
        assertEquals(this.uri1,returned.getKey(),"should've returned doc with uri1");
        dsi.undo();
        assertNull(dsi.getDocument(this.uri1),"should've been null - put doc1 was undone");
        try {
            dsi.undo();
            fail("no documents - should've thrown IllegalStateException");
        }catch(IllegalStateException e){}
    }

    @Test
    public void undoNthPutByURI() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        //undo put 2 - test before and after
        Document returned = dsi.getDocument(this.uri2);
        assertEquals(this.uri2,returned.getKey(),"should've returned doc with uri2");
        dsi.undo(this.uri2);
        assertNull(dsi.getDocument(this.uri2),"should've returned null - put was undone");
    }

    @Test
    public void undoDelete() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        assertTrue(dsi.getDocument(this.uri3).getDocumentTxt().equals(this.txt3),"text was not as expected");
        dsi.deleteDocument(this.uri3);
        assertNull(dsi.getDocument(this.uri3),"doc should've been deleted");
        dsi.undo(this.uri3);
        assertTrue(dsi.getDocument(this.uri3).getKey().equals(this.uri3),"should return doc3");
    }

    @Test
    public void undoNthDeleteByURI() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        assertTrue(dsi.getDocument(this.uri3).getDocumentTxt().equals(this.txt3),"text was not as expected");
        dsi.deleteDocument(this.uri3);
        dsi.deleteDocument(this.uri2);
        assertNull(dsi.getDocument(this.uri2),"should've been null");
        dsi.undo(this.uri2);
        assertTrue(dsi.getDocument(this.uri2).getKey().equals(this.uri2),"should return doc2");
    }

    @Test
    public void undoOverwriteByURI() throws Exception {
        DocumentStoreImpl dsi = createStoreAndPutAll();
        String replacement = "this is a replacement for txt2";
        dsi.putDocument(new ByteArrayInputStream(replacement.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(dsi.getDocument(this.uri2).getDocumentTxt().equals(replacement),"should've returned replacement text");
        dsi.undo(this.uri2);
        assertTrue(dsi.getDocument(this.uri2).getDocumentTxt().equals(this.txt2),"should've returned original text");
    }
}