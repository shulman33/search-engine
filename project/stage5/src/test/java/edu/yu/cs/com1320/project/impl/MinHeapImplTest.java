package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** This test is for stages 4 and 5*/
public class MinHeapImplTest {

    @Test
    public void testSimpleInsertRemove(){
        MinHeapImpl<Character> heap = new MinHeapImpl<>();
        heap.insert('A');
        Character removed = heap.remove();
        assertEquals('A',removed,"Inserted one character. Expected 'A' on remove, got " + removed);
    }
    @Test
    public void testSimpleInsertMultiple(){
        MinHeapImpl<Character> heap = new MinHeapImpl<>();
        heap.insert('C');
        heap.insert('B');
        heap.insert('A');
        Character removed = heap.remove();
        assertEquals('A',removed,"Inserted 3 characters (C,B,A), expected 'A', got " + removed);
    }
    @Test
    public void testArrayPositions() {
        MinHeapImpl<Character> heap = new MinHeapImpl<>();
        heap.insert('Z');
        int index = heap.getArrayIndex('Z');
        assertEquals(1,index,"Inserted one character, expected it to be at position 1, was at position " + index);
        heap.insert('A');
        index = heap.getArrayIndex('Z');
        assertEquals(2,index,"Inserted two characters (Z then A), expected 'Z' to be at position 2, was at position " + index);
        heap.insert('F');
        heap.insert('K');
        index = heap.getArrayIndex('Z');
        assertEquals(4,index,"Inserted four characters (Z, A, F, K), expected 'Z' to be at position 4, was at position " + index);
    }
}