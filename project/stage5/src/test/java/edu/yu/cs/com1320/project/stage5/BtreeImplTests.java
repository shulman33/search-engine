package edu.yu.cs.com1320.project.stage5;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import java.net.URI;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import java.io.File;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BtreeImplTests {
    private BTree<Integer, String> bTree;

    @Test
    public void putAndGet() throws IOException {
        this.bTree = new BTreeImpl<>();
        this.bTree.put(1, "one");
        bTree.put(2, "two");
        bTree.put(3, "three");
        bTree.put(4, "four");
        bTree.put(5, "five");
        bTree.put(6, "six");
        bTree.put(7, "seven");
        bTree.put(8, "eight");
        bTree.put(9, "nine");
        bTree.put(10, "ten");
        bTree.put(11, "eleven");
        bTree.put(12, "twelve");
        bTree.put(13, "thirteen");
        bTree.put(14, "fourteen");
        bTree.put(15, "fifteen");
        bTree.put(16, "sixteen");
        bTree.put(17, "seventeen");
        bTree.put(18, "eighteen");
        bTree.put(19, "nineteen");
        bTree.put(20, "twenty");
        bTree.put(21, "twenty one");
        bTree.put(22, "twenty two");
        bTree.put(23, "twenty three");
        bTree.put(24, "twenty four");
        bTree.put(25, "twenty five");
        bTree.put(26, "twenty six");
        assertEquals("one", this.bTree.get(1));
        assertEquals("twenty four", this.bTree.get(24));
        assertEquals("fifteen", this.bTree.get(15));
    }
}
