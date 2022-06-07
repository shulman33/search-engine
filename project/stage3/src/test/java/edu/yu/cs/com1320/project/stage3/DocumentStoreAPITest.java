package edu.yu.cs.com1320.project.stage3.impl;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URI;

import edu.yu.cs.com1320.project.stage3.DocumentStore.DocumentFormat;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentStoreAPITest {

    //stage 3 tests
    @Test
    public void interfaceCount() {//tests that the class only implements one interface and its the correct one
        @SuppressWarnings("rawtypes")
        Class[] classes = DocumentStoreImpl.class.getInterfaces();
        assertTrue(classes.length == 1);
        assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.stage3.DocumentStore"));
    }

    @Test
    public void methodCount() {
        Method[] methods = DocumentStoreImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                publicMethodCount++;
            }
        }
        assertTrue(publicMethodCount == 9);
    }

    @Test
    public void stage3SearchExists(){
            new DocumentStoreImpl().search("test");
    }
    @Test
    public void stage3DeleteAllExists(){
        try{
            new DocumentStoreImpl().deleteAll("test");
        }catch(Throwable e){
            e.printStackTrace();
            throw e;
        }
    }
    @Test
    public void stage3SearchByPrefixExists(){
        try{
            new DocumentStoreImpl().searchByPrefix("test");            
        }catch(Throwable e){
            e.printStackTrace();
            throw e;
        }
    }
    @Test
    public void stage3DeleteAllWithPrefixExists(){
        new DocumentStoreImpl().deleteAllWithPrefix("test");
    }

    //stage 2 tests
    @Test
    public void stage2UndoExists(){
        try {
            new DocumentStoreImpl().undo();
        } catch (Exception e) {}
    }

    @Test
    public void stage2UndoByURIExists(){
        try {
            new DocumentStoreImpl().undo(new URI("hi"));
        } catch (Exception e) {}
    }


    //stage 1 tests
        @Test
    public void fieldCount() {
        Field[] fields = DocumentStoreImpl.class.getFields();
        int publicFieldCount = 0;
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers())) {
                publicFieldCount++;
            }
        }
        assertTrue(publicFieldCount == 0);
    }

    @Test
    public void subClassCount() {
        @SuppressWarnings("rawtypes")
        Class[] classes = DocumentStoreImpl.class.getClasses();
        assertTrue(classes.length == 0);
    }

    @Test
    public void constructorExists() {
        try {
            new DocumentStoreImpl();
        } catch (Exception e) {}
    }

    @Test
    public void putDocumentExists() throws URISyntaxException{
        try {
            new DocumentStoreImpl().putDocument(null, new URI("hi"), DocumentFormat.BINARY);
        } catch (Exception e) {}
    }

    @Test
    public void getDocumentExists() throws URISyntaxException{
        try {
            new DocumentStoreImpl().getDocument(new URI("hi"));
        } catch (Exception e) {}
    }

    @Test
    public void deleteDocumentExists() throws URISyntaxException {
        try {
            new DocumentStoreImpl().deleteDocument(new URI("hi"));
        } catch (Exception e) {}
    }

}