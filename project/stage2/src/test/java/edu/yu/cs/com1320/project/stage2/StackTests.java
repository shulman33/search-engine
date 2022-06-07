package edu.yu.cs.com1320.project.stage2;

import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackTests {
    @Test
    public void peakEmptyStack(){
        StackImpl testStack = new StackImpl();
        assertEquals(null,testStack.peek());
        testStack.push(1);
        testStack.push(2);
        testStack.pop();
        testStack.pop();
        assertEquals(null,testStack.peek());
    }
    @Test
    public void pushAndGetSize(){
        StackImpl stack = new StackImpl();
        stack.push(1);
        stack.push(2);
        assertEquals(2,stack.size());
        stack.push(23);
        assertEquals(3,stack.size());

    }
    @Test
    public void peak(){
        StackImpl stack = new StackImpl();
        stack.push(46);
        assertEquals(46,stack.peek());
        stack.push(89);
        stack.push(2);
        assertEquals(2,stack.peek());
    }
    @Test
    public void popEmptyStack(){
        StackImpl stack = new StackImpl();
        assertEquals(null,stack.pop());
    }
    @Test
    public void pop(){
        StackImpl stack = new StackImpl();
        stack.push(1);
        stack.push("hi");
        stack.push(3);
        assertEquals(3,stack.pop());
        assertEquals(2,stack.size());
        assertEquals("hi",stack.peek());
        stack.push(5);
        assertEquals(5,stack.peek());
        assertEquals(3,stack.size());

    }
}
