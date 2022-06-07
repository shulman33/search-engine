package edu.yu.cs.com1320.project.stage4.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import org.junit.jupiter.api.Test;

public class HashTableAPITest {

    @Test
    public void interfaceCount() {//tests that the class only implements one interface and its the correct one
        @SuppressWarnings("rawtypes")
        Class[] classes = HashTableImpl.class.getInterfaces();
        assertTrue(classes.length == 1);
        assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.HashTable"));
    }

    @Test
    public void methodCount() {//tests for public and protected methods. the expected number should match the number of methods explicitly tested below save for the constructor
        Method[] methods = HashTableImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                if(!method.getName().equals("equals") && !method.getName().equals("hashCode")) {
                    publicMethodCount++;
                }
            }
        }
        assertTrue(publicMethodCount == 2);
    }

    @Test
    public void fieldCount() {//tests for public or protected fields
        Field[] fields = HashTableImpl.class.getFields();
        int publicProtectedFieldCount = 0;
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) || Modifier.isProtected(field.getModifiers())) {
                publicProtectedFieldCount++;
            }
        }
        assertTrue(publicProtectedFieldCount == 0);
    }

    @Test
    public void subClassCount() {//tests if any subclasses are public/protected
        @SuppressWarnings("rawtypes")
        Class[] classes = HashTableImpl.class.getClasses();
        assertTrue(classes.length == 0);
    }

    @Test
    public void constructorExists() {
        new HashTableImpl<String,String>();
    }

    @Test
    public void putExists() {
        try {
            new HashTableImpl<String,String>().put("hello", "there");
        } catch (RuntimeException e) {}//catch any run time error this input might cause. This is meant to test method existence, not correctness
    }

    @Test
    public void getExists() {
        try {
            new HashTableImpl<String,String>().get("hello");
        } catch (RuntimeException e) {}//catch any run time error this input might cause. This is meant to test method existence, not correctness
    }
}
