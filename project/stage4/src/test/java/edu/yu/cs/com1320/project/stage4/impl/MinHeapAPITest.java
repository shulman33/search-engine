package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinHeapAPITest {


    @Test
    public void interfaceCount() {//tests that the class only implements one interface and its the correct one
        @SuppressWarnings("rawtypes")
        Class[] classes = MinHeapImpl.class.getInterfaces();
        assertTrue(classes.length == 0);
        Class clazz = MinHeapImpl.class.getSuperclass();
        assertTrue(clazz.getName().equals("edu.yu.cs.com1320.project.MinHeap"));
    }

    @Test
    public void methodCount() {//need only test for non constructors
        Method[] methods = MinHeapImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                if (!method.getName().equals("equals") && !method.getName().equals("hashCode")) {
                    publicMethodCount++;
                }
            }
        }
        assertTrue(publicMethodCount <= 3);
    }

    @Test
    public void fieldCount() {
        Field[] fields = MinHeapImpl.class.getFields();
        int publicFieldCount = 0;
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers())) {
                publicFieldCount++;
            }
        }
        assertTrue(publicFieldCount == 0);
    }

    @Test
    public void noargsConstructorExists() {
        new MinHeapImpl();
    }
}