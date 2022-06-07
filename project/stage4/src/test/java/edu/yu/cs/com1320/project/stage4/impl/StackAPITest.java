package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StackAPITest {

    @Test
    public void interfaceCount() {//tests that the class only implements one interface and its the correct one
        @SuppressWarnings("rawtypes")
        Class[] classes = StackImpl.class.getInterfaces();
        assertTrue(classes.length == 1);
        assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.Stack"));
    }

    @Test
    public void methodCount() {//need only test for non constructors
        Method[] methods = StackImpl.class.getDeclaredMethods();
        int publicMethodCount = 0;
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                if(!method.getName().equals("equals") && !method.getName().equals("hashCode")) {
                    publicMethodCount++;
                }
            }
        }
        assertTrue(publicMethodCount == 4);
    }

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
    public void noArgsConstructorExists(){
        try {
            new StackImpl();
        } catch (RuntimeException e) {}
    }

    @Test
    public void pushExists(){
        StackImpl<String> stack = new StackImpl<>();
        stack.push("Hello");
    }

    @Test
    public void popExists(){
        StackImpl<String> stack = new StackImpl<>();
        String str = stack.pop();
    }

    @Test
    public void peekExists(){
        StackImpl<String> stack = new StackImpl<>();
        String str = stack.peek();
    }

    @Test
    public void sizeExists(){
        StackImpl<String> stack = new StackImpl<>();
        int size = stack.size();
    }
}