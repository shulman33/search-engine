package edu.yu.cs.com1320.project.stage5;


import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreImplTest {
    public static class Utils {
        public static int calculateHashCode(URI uri, String text, byte[] binaryData) {
            int result = uri.hashCode();
            result = 31 * result + (text != null ? text.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(binaryData);
            return result;
        }

    }

    public static class TestUtils {

        public static void deleteTree(File base) {
            try {
                File[] files = base.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteTree(file);
                    }
                    else {
                        file.delete();
                    }
                }
            }
            catch (Exception e) {
            }
        }

        public static File uriToFile(File baseDir, URI uri) {
            String auth = uri.getAuthority();
            String path = uri.getRawPath().replaceAll("//", File.separator) + ".json";
            return new File(baseDir, auth + File.separator + path);
        }

        public static String getContents(File baseDir, URI uri) throws IOException {
            File file = uriToFile(baseDir, uri);
            if (!file.exists()) {
                return null;
            }
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes);
        }

        public static boolean equalButNotIdentical(Document first, Document second) throws IOException {
            if(System.identityHashCode(first) == System.identityHashCode(second)){
                return false;
            }
            if(!first.getKey().equals(second.getKey())){
                return false;
            }
            if(!first.getDocumentTxt().toLowerCase().equals(second.getDocumentTxt().toLowerCase())){
                return false;
            }
            return true;
        }
    }
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

    private int bytes1;
    private int bytes2;
    private int bytes3;
    private int bytes4;

    private File baseDir;

    private String updateAddition;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "This doc1 plain text string Computer Headphones";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "Text doc2 plain String";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "This is the text of doc3";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "This is the text of doc4";

        this.bytes1 = this.txt1.getBytes().length;
        this.bytes2 = this.txt2.getBytes().length;
        this.bytes3 = this.txt3.getBytes().length;
        this.bytes4 = this.txt4.getBytes().length;

        //create baseDir
        this.baseDir = Files.createTempDirectory("stage5").toFile();

        this.updateAddition = "UPDATED-UPDATED";
    }
    @AfterEach
    public void cleanUp(){
        TestUtils.deleteTree(this.baseDir);
        this.baseDir.delete();
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


    /**
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     * ***********************************************STAGE 5 TESTS***************************************************
     * ***************************************************************************************************************
     * ***************************************************************************************************************
     */

    private void checkContents(String errorMsg, String contents,String expected){
        assertNotNull(contents,errorMsg + ": contents were null");
        assertTrue(contents.toLowerCase().indexOf(expected.toLowerCase()) >= 0,errorMsg + ": expected content not found");
    }

    //in each of the tests below, assert as a precondtion that whatever should be on disk is, and whatever should be in memory is

    //test1a:
    // 1) put docA which didn't exist, and thus causes docB to be written to disk due to reaching MAX DOC COUNT
    // 2) get docA which was on disk, thus going over DOCUMENT COUNT limit and causing docB to be written to disk
    @Test
    public void stage5PushToDiskViaMaxDocCount() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        store.setMaxDocumentCount(2);
        pushAboveMaxViaPutNew(store);
    }

    private void pushAboveMaxViaPutNew(DocumentStoreImpl store) throws IOException{
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.getDocument(this.uri1);
        Document doc2 = store.getDocument(this.uri2);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);

        //at this point, 2 and 3 should be in memory, and 1 should be on disk, pushed out when doc3 was put
        String doc1Str = TestUtils.getContents(this.baseDir,this.uri1);
        checkContents("doc1 should've been on disk, but was not",doc1Str,this.txt1);
        assertNotNull(store.getDocument(this.uri2),"doc2 should be in memory");
        assertNotNull(store.getDocument(this.uri3),"doc3 should be in memory");
        assertNull(TestUtils.getContents(this.baseDir,this.uri2),"doc2 should NOT have been on disk");
        assertNull(TestUtils.getContents(this.baseDir,this.uri3),"doc3 should NOT have been on disk");
        //make sure that when doc1 is requested, it is NOT the same object as doc1 above, which was gotten BEFORE it was kicked out of memory
        //this search should bring doc1 back into memory and push doc2 out to disk
        store.search("doc1");
        Document doc1v2 = store.getDocument(this.uri1);
        assertTrue(TestUtils.equalButNotIdentical(doc1,doc1v2),"the original doc1 object should NOT have been returned - should be a different object in memory now");

        //check that doc2 is now on disk, but 1 and 3 are in memory
        String doc2Str = TestUtils.getContents(this.baseDir,this.uri2);
        checkContents("doc2 should've been on disk, but was not",doc2Str,this.txt2);
        assertNull(TestUtils.getContents(this.baseDir,this.uri1),"doc1 should NOT have been on disk");
        assertNull(TestUtils.getContents(this.baseDir,this.uri3),"doc3 should NOT have been on disk");

        //make sure that when doc2 is requested, it is NOT the same object as docs above, which was gotten BEFORE it was kicked out of memory
        //this search should bring doc2 back into memory
        store.search("doc2");
        Document doc2v2 = store.getDocument(this.uri2);
        assertTrue(TestUtils.equalButNotIdentical(doc2,doc2v2),"the original doc2 object should NOT have been returned - should be a different object in memory now");
    }

    //test4a: reach MAX MEMORY and have some docs on disk. Delete docs in memory. Assert that no docs were brought in from disk. Get docs that are on disk, assert they are back in memory and off disk.
    @Test
    public void stage5PushToDiskViaMaxDocCountBringBackInViaDeleteAndSearch() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        store.setMaxDocumentCount(2);
        deleteDocInMemoryBringInDocFromDisk(store);
    }

    /**
     * This method assumes only 2 docs fit in memory for whatever reason. It does the following:
     * 1) put docs1, doc2, and then doc3
     * 2) assert that doc1 is NOT in memory and IS on disk, that doc2 and doc3 ARE in memory
     * 3) deletes doc3, making room in memory for doc1
     * 4) assert that doc1 is still NOT in memory even though doc3 was deleted
     * 5) do a search that brings doc1 back into memory
     * 6) assert that doc2 is still in memory and doc1 is back in memory
     * @param store
     * @throws IOException
     */
    private void deleteDocInMemoryBringInDocFromDisk(DocumentStoreImpl store) throws IOException{
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.getDocument(this.uri1);
        Document doc2 = store.getDocument(this.uri2);
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);

        //at this point, 2 and 3 should be in memory, and 1 should be on disk, pushed out when doc3 was put
        assertNull(store.getDocument(this.uri1),"doc1 should NOT be in memory");
        String doc1Str = TestUtils.getContents(this.baseDir,this.uri1);
        checkContents("doc1 should've been on disk, but was not",doc1Str,this.txt1);
        assertNotNull(store.getDocument(this.uri2),"doc2 should be in memory");
        assertNotNull(store.getDocument(this.uri3),"doc3 should be in memory");
        assertNull(TestUtils.getContents(this.baseDir,this.uri2),"doc2 should NOT have been on disk");
        assertNull(TestUtils.getContents(this.baseDir,this.uri3),"doc3 should NOT have been on disk");

        //delete doc3, making room for doc1; assert that doc3 is gone but doc1 still not in memory
        store.deleteDocument(this.uri3);
        assertNull(store.getDocument(this.uri3),"doc3 should be gone/deleted");
        assertNull(store.getDocument(this.uri1),"doc1 should STILL not be in memory");

        //do a search that brings doc1 back into memory, assert that doc2 is still unaffected and doc1 is back in memory
        store.search("doc1");
        assertNotNull(store.getDocument(this.uri1),"doc1 should be back in memory");
        assertNull(TestUtils.getContents(this.baseDir,this.uri1),"doc1 should have been removed from disk");
        assertTrue(TestUtils.equalButNotIdentical(doc1,store.getDocument(this.uri1)),"doc1 should NOT be the same exact object in memory as earlier - a new object should've been created  when deserializing");
        assertFalse(TestUtils.equalButNotIdentical(doc2,store.getDocument(this.uri2)),"doc2 should still be the same exact object in memory");
    }


    //test5a: undo a delete which causes doc store to go over MAX MEMORY, causing docs to be written to disk.
    //Assert docs being in memory and on disk as pre/post conditions.
    @Test
    public void stage5PushToDiskViaMaxDocCountViaUndoDelete() throws IOException {
        DocumentStoreImpl store = new DocumentStoreImpl(this.baseDir);
        store.setMaxDocumentCount(2);
        overLimitViaUndo(store);
    }

    private void overLimitViaUndo(DocumentStoreImpl store) throws IOException{
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.getDocument(this.uri1);
        Document doc2 = store.getDocument(this.uri2);
        //delete doc2, making room for doc3
        store.deleteDocument(this.uri2);
        //put doc 3
        store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
        //at this point, 1 and 3 should be in memory, and 2 should be gone
        assertNotNull(store.getDocument(this.uri1),"doc1 should be in memory");
        assertNotNull(store.getDocument(this.uri3),"doc3 should be in memory");
        assertNull(store.getDocument(this.uri2),"doc2 should be null because it was deleted");
        //undo the deletion of doc2, which should push doc1 out to disk. doc2 and doc3 should be in memory
        store.undo(this.uri2);
        String doc1Str = TestUtils.getContents(this.baseDir,this.uri1);
        checkContents("doc1 should've been written out to disk, but was not",doc1Str,this.txt1);
        assertNull(TestUtils.getContents(this.baseDir,this.uri2),"doc2 should NOT be on disk");
        assertNotNull(store.getDocument(this.uri2),"doc2 should be in memory");
        assertNull(TestUtils.getContents(this.baseDir,this.uri3),"doc3 should NOT be on disk");
        assertNotNull(store.getDocument(this.uri3),"doc3 should be in memory");
    }



    //STAGE 4

    /*
Every time a document is used, its last use time should be updated to the relative JVM time, as measured in nanoseconds (see java.lang.System.nanoTime().)
A Document is considered to be “used” whenever it is accessed as a result of a call to any part of DocumentStore’s public API. In other words, if it is “put”,
or returned in any form as the result of any “get” or “search” request, or an action on it is undone via any call to either of the DocumentStore.undo methods.
     */

    @Test
    public void stage4testSetDocLastUseTimeOnGet() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc = store.getDocument(this.uri1);
        long first = doc.getLastUseTime();
        doc = store.getDocument(this.uri1);
        long second = doc.getLastUseTime();
        //was last use time updated on the put?
        assertTrue(first < second,"last use time should be changed when the DocumentStore.getDocument method is called");
    }

    @Test
    public void stage4testSetDocLastUseTimeOnPut() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        long before = System.nanoTime();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc = store.getDocument(this.uri1);
        //was last use time set on the put?
        assertTrue(before < doc.getLastUseTime(),"last use time should be after the time at which the document was put");
    }
    @Test
    public void stage4testUpdateDocLastUseTimeOnOverwrite() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        //was last use time updated on the put?
        long before = System.nanoTime();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc = store.getDocument(this.uri1);
        assertTrue(before < doc.getLastUseTime(),"last use time should be after the time at which the document was put");
        before = System.nanoTime();
        //was last use time updated on overwrite?
        store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.getDocument(this.uri1);
        assertTrue(before < doc2.getLastUseTime(),"last use time should be after the time at which the document was overwritten");
    }

    @Test
    public void stage4testUpdateDocLastUseTimeOnSearch() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        long before = System.nanoTime();
        //this search should return the contents of the doc at uri1
        List<Document> results = store.search("Computer");
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the search?
        assertTrue(before < doc.getLastUseTime(),"last use time of search result doc should be after the time at which the document was put");
    }
    @Test
    public void stage4testUpdateDocLastUseTimeOnSearchByPrefix() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        long before = System.nanoTime();
        //this search should return the contents of the doc at uri1
        List<Document> results = store.searchByPrefix("Comput");
        Document doc = store.getDocument(this.uri1);
        //was last use time updated on the searchByPrefix?
        assertTrue(before < doc.getLastUseTime(),"last use time of search result should be after the time at which the document was put");
    }


    //stage 3 tests

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
        assertEquals(2,results.size(),"expected 2 match, received " + results.size());
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