package edu.yu.cs.com1320.project;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    ///////////// HashTableImpl Tests ///////////////

    @Test
    public void getValue(){
        HashTableImpl hashTable = new HashTableImpl();
        hashTable.put(1, "Hello");
        hashTable.put(2,"Fish");
        Assert.assertEquals("Fish", hashTable.get(2));
    }
    @Test
    public void getNullValue(){
        HashTableImpl hashTable = new HashTableImpl();
        hashTable.put(1, "Hello");
        Assert.assertEquals(null, hashTable.get(3));
    }
    @Test
    public void putNoKeyPresent(){
        HashTableImpl hashTable = new HashTableImpl();
        Assert.assertEquals(null,hashTable.put(1, "Hello"));
    }
    @Test
    public void putWithKeyPresent(){
        HashTableImpl hashTable = new HashTableImpl();
        hashTable.put(1, "Hello");
        Assert.assertEquals("Hello", hashTable.put(1,"Cool"));
    }

    ///////////// DocumentImpl Tests ///////////////

    @Test
    public void getDocText(){
        URI uri = URI.create("www.apple.com");
        DocumentImpl doc = new DocumentImpl(uri,"whatsup");
        assertEquals("whatsup", doc.getDocumentTxt());
    }

    @Test
    public void getKey(){
        URI uri = URI.create("www.apple.com");
        DocumentImpl doc = new DocumentImpl(uri,"whatsup");
        Assert.assertEquals(uri, doc.getKey());
    }
    @Test
    public void UriNullError(){
        boolean caught = false;
        URI uri = null;
        try{
            DocumentImpl doc = new DocumentImpl(uri,"whatsup");
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void UriEmptyError(){
        boolean caught = false;
        URI uri = URI.create("");
        try{
            DocumentImpl doc = new DocumentImpl(uri,"whatsup");
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void StrNullError(){
        boolean caught = false;
        URI uri = URI.create("www.apple.com");
        String str = null;
        try{
            DocumentImpl doc = new DocumentImpl(uri,str);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }


    @Test
    public void TextEmptyError(){
        boolean caught = false;
        URI uri = URI.create("www.apple.com");
        try{
            DocumentImpl doc = new DocumentImpl(uri,"");
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void TextNullError(){
        boolean caught = false;
        String str = null;
        URI uri = URI.create("www.apple.com");
        try{
            DocumentImpl doc = new DocumentImpl(uri,str);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void ByteArrayEmptyError(){
        byte[] byteArray = new byte[0];
        boolean caught = false;
        URI uri = URI.create("www.apple.com");
        try{
            DocumentImpl doc = new DocumentImpl(uri,byteArray);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void ByteArrayNullError(){
        boolean caught = false;
        byte[] byteArray = null;
        URI uri = URI.create("www.apple.com");
        try{
            DocumentImpl doc = new DocumentImpl(uri,byteArray);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }

    ///////////// DocumentStoreImpl Tests ///////////////
    @Test
    public void putDocument() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        URI uri = URI.create("www.apple.com");
        Assert.assertEquals(0,docStore.putDocument(inputStream,uri, DocumentStore.DocumentFormat.BINARY));

    }
    @Test
    public void putDocumentAlreadyPresent() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        String sampleString2 = "google.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        InputStream inputStream2 = new ByteArrayInputStream(sampleString2.getBytes());
        URI uri = URI.create("www.apple.com");

        docStore.putDocument(inputStream,uri, DocumentStore.DocumentFormat.BINARY);
        Assert.assertNotEquals(0,docStore.putDocument(inputStream2,uri, DocumentStore.DocumentFormat.BINARY));

    }
    @Test
    public void uriIsNull() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        URI uri = null;
        boolean caught = false;
        try{
            docStore.putDocument(inputStream,uri, DocumentStore.DocumentFormat.BINARY);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void uriIsEmpty() throws IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        URI uri = URI.create("");
        boolean caught = false;
        try{
            docStore.putDocument(inputStream,uri, DocumentStore.DocumentFormat.BINARY);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void inputIsNullDelete() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        URI uri = URI.create("www.apple.com");

        Assert.assertEquals(0,docStore.putDocument(null,uri, DocumentStore.DocumentFormat.BINARY));

    }
    @Test
    public void inputIsNullDeleteWithPreviousEntry() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        URI uri = URI.create("www.apple.com");

        docStore.putDocument(inputStream,uri, DocumentStore.DocumentFormat.BINARY);

        Assert.assertNotEquals(0,docStore.putDocument(null,uri, DocumentStore.DocumentFormat.BINARY));

    }
    @Test
    public void formatIsNull() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        URI uri = URI.create("www.apple.com");
        boolean caught = false;
        try{
            docStore.putDocument(inputStream,uri, null);
        }catch (IllegalArgumentException e){
            caught = true;
        }
        Assert.assertTrue(caught);
    }
    @Test
    public void getMethod() throws IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String str = "How we doin";
        InputStream inputStream = new ByteArrayInputStream(str.getBytes());
        URI uri = URI.create("www.apple.com");

        docStore.putDocument(inputStream,uri, DocumentStore.DocumentFormat.BINARY);
        byte[] bytes = str.getBytes();
        Document doc = new DocumentImpl(uri,bytes);
        int hashCode = docStore.getDocument(uri).hashCode();
        assertEquals(doc.hashCode(),hashCode);
    }
    @Test
    public void deleteDocDoesntExist(){
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        URI uri = URI.create("www.apple.com");
        assertFalse(docStore.deleteDocument(uri));
    }
    @Test
    public void deleteDocThatExist() throws IOException{
        DocumentStoreImpl docStore = new DocumentStoreImpl();
        String sampleString = "howtodoinjava.com";
        InputStream inputStream = new ByteArrayInputStream(sampleString.getBytes());
        URI uri = URI.create("www.apple.com");
        docStore.putDocument(inputStream,uri, DocumentStore.DocumentFormat.BINARY);
        assertTrue(docStore.deleteDocument(uri));
    }

}
