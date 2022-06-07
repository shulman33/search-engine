package edu.yu.cs.com1320.project.stage4.impl;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentAPITest {


    @Test
    public void interfaceCount() {//tests that the class only implements the required interfaces
        @SuppressWarnings("rawtypes")
        Class[] classes = DocumentImpl.class.getInterfaces();
        assertTrue(classes.length == 1, "length of classes is " + classes.length);
        assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.stage4.Document"));
    }

    @Test
    public void methodCount() {//need only test for non constructors
        Method[] methods = DocumentImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        String names = "";
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                names += method.getName() + "\n";
                if(!method.getName().equals("equals") && !method.getName().equals("hashCode") && !method.getName().equals("compareTo")) {
                    publicMethodCount++;
                }
            }
        }
        assertTrue(publicMethodCount == 7, "method count is " + publicMethodCount + " names are " + names);
    }

    //STAGE 4 tests
    @Test
    public void stage4GetLastUseTimeExists()throws URISyntaxException {
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi").getLastUseTime();
        } catch (RuntimeException e) {}
    }
    @Test
    public void stage4SetLastUseTimeExists()throws URISyntaxException {
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi").setLastUseTime(100);
        } catch (RuntimeException e) {}
    }

    //STAGE 3 tests
    @Test
    public void stage3WordCountExists() throws URISyntaxException {
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi").wordCount("hi");
        } catch (RuntimeException e) {}
    }


    //stage 1 tests
        @Test
    public void fieldCount() {
        Field[] fields = DocumentImpl.class.getFields();
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
        Class[] classes = DocumentImpl.class.getClasses();
        assertTrue(classes.length == 0);
    }

    @Test
    public void constructor1Exists() throws URISyntaxException {
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi");
        } catch (RuntimeException e) {}
    }

    @Test
    public void constructor2Exists() throws URISyntaxException {
        URI uri = new URI("https://this.com");
        byte[] ary = {0,0,0};
        try {
            new DocumentImpl(uri, ary );
        } catch (RuntimeException e) {}
    }

    @Test
    public void getDocumentBinaryDataExists() throws URISyntaxException{
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi".getBytes()).getDocumentBinaryData();
        } catch (RuntimeException e) {}
    }

    @Test
    public void getDocumentTxtExists() throws URISyntaxException{
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi").getDocumentTxt();
        } catch (RuntimeException e) {}
    }

    @Test
    public void getKeyExists() throws URISyntaxException {
        URI uri = new URI("https://this.com");
        try {
            new DocumentImpl(uri, "hi").getKey();
        } catch (RuntimeException e) {}
    }

}