package edu.yu.cs.com1320.project.stage5;

import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DocumentTests {
    @Test
    public void wordCount() throws URISyntaxException, IOException {
        URI uri1 = new URI("http://amazon.com");
        String txt1 = "hello computer mouse water coat spring coAt";
        Document document = new DocumentImpl(uri1,txt1,null);
        assertEquals(1, document.wordCount("computer"));
        assertEquals(1,document.wordCount("Computer"));
        assertEquals(2,document.wordCount("COAT"));
    }

    @Test
    public void wordSet() throws URISyntaxException {
        URI uri1 = new URI("http://amazon.com");
        URI uri2 = new URI("http://google.com");
        String txt1 = "hello computer mouse water coat spring coAt";
        String txt2 = " ";
        Document document1 = new DocumentImpl(uri1,txt1,null);
        Document document2 = new DocumentImpl(uri2,txt2,null);
        assertEquals(6,document1.getWords().size());
        assertEquals(0,document2.getWords().size());
    }

    @Test
    public void setAndGetLastUseTime() throws URISyntaxException {
        long setTime = System.nanoTime();
        URI uri1 = new URI("http://amazon.com");
        String txt1 = "hello computer mouse water coat spring coAt";
        Document document1 = new DocumentImpl(uri1,txt1, null);
        document1.setLastUseTime(System.nanoTime());
        assertTrue(setTime < document1.getLastUseTime());

    }
    @Test
    public void setAndGetTree() throws URISyntaxException {
        Map<String, Integer> testMap = new HashMap<>();
        testMap.put("hello",1);
        testMap.put("computer", 1);
        URI uri1 = new URI("http://amazon.com");
        String txt1 = "hello computer mouse water coat spring coAt";
        Document doc = new DocumentImpl(uri1,txt1, null);
        doc.setWordMap(testMap);
        assertEquals(testMap,doc.getWordMap());
    }
}
